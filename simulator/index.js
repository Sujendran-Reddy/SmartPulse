const mqtt = require("mqtt");

const MQTT_URL = process.env.MQTT_URL || "mqtt://localhost:1883";
const API_URL = process.env.API_URL || "http://localhost:8080";
const INTERVAL_MS = Number(process.env.INTERVAL_MS || 1000);

const deviceStates = new Map();

function randomBetween(min, max) {
    return Math.random() * (max - min) + min;
}

function clamp(value, min, max) {
    return Math.min(Math.max(value, min), max);
}

function round(value, decimals = 1) {
    const factor = 10 ** decimals;
    return Math.round(value * factor) / factor;
}

function initializeDevice(deviceId) {
    if (!deviceStates.has(deviceId)) {
        deviceStates.set(deviceId, {
            temperature: randomBetween(22, 28),
            humidity: randomBetween(50, 70),
            battery: 100,
            ticks: 0
        });
    }

    return deviceStates.get(deviceId);
}

function generateTelemetry(deviceId) {
    const state = initializeDevice(deviceId);

    state.temperature = clamp(
        state.temperature + randomBetween(-0.5, 0.5),
        15,
        40
    );

    state.humidity = clamp(
        state.humidity + randomBetween(-1.5, 1.5),
        20,
        100
    );

    state.ticks++;

    // Slowly drain battery instead of generating random nonsense.
    if (state.ticks % 30 === 0 && state.battery > 0) {
        state.battery--;
    }

    return {
        temperature: round(state.temperature),
        humidity: round(state.humidity),
        battery: state.battery
    };
}

async function getDevices() {
    const response = await fetch(`${API_URL}/api/devices`);

    if (!response.ok) {
        throw new Error(
            `Could not load devices. HTTP ${response.status}`
        );
    }

    return response.json();
}

async function startSimulator() {
    console.log("====================================");
    console.log(" SmartPulse Telemetry Simulator");
    console.log("====================================");
    console.log(`API:  ${API_URL}`);
    console.log(`MQTT: ${MQTT_URL}`);
    console.log(`Rate: one reading/device every ${INTERVAL_MS}ms`);
    console.log("");

    const devices = await getDevices();

    if (devices.length === 0) {
        console.log("No registered devices found.");
        console.log("Create a device in SmartPulse first.");
        process.exit(0);
    }

    console.log(`Loaded ${devices.length} device(s):`);

    for (const device of devices) {
        console.log(`- ${device.name} (${device.id})`);
        initializeDevice(device.id);
    }

    console.log("");

    const client = mqtt.connect(MQTT_URL, {
        clientId: `smartpulse-simulator-${Date.now()}`,
        clean: true
    });

    client.on("connect", () => {
        console.log("Connected to Mosquitto MQTT broker.");
        console.log("Publishing telemetry...");
        console.log("");

        setInterval(() => {
            for (const device of devices) {
                const telemetry = generateTelemetry(device.id);

                const topic =
                    `smartpulse/devices/${device.id}/telemetry`;

                const payload = JSON.stringify(telemetry);

                client.publish(
                    topic,
                    payload,
                    { qos: 1 },
                    (error) => {
                        if (error) {
                            console.error(
                                `Publish failed for ${device.name}:`,
                                error.message
                            );

                            return;
                        }

                        console.log(
                            `[${new Date().toISOString()}] ` +
                            `${device.name} | ` +
                            `${telemetry.temperature}°C | ` +
                            `${telemetry.humidity}% | ` +
                            `${telemetry.battery}% battery`
                        );
                    }
                );
            }
        }, INTERVAL_MS);
    });

    client.on("error", (error) => {
        console.error("MQTT error:", error.message);
    });

    client.on("reconnect", () => {
        console.log("Reconnecting to MQTT...");
    });

    process.on("SIGINT", () => {
        console.log("");
        console.log("Stopping SmartPulse simulator...");

        client.end(false, () => {
            process.exit(0);
        });
    });
}

startSimulator().catch((error) => {
    console.error("Simulator failed:", error.message);
    process.exit(1);
});