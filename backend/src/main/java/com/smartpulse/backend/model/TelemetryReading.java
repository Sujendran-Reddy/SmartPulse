package com.smartpulse.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "telemetry")
public class TelemetryReading {

    @Id
    private String id;

    private String deviceId;
    private Instant timestamp;
    private Double temperature;
    private Double humidity;
    private Integer battery;

    public TelemetryReading() {
    }

    public TelemetryReading(
            String deviceId,
            Instant timestamp,
            Double temperature,
            Double humidity,
            Integer battery
    ) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.battery = battery;
    }

    public String getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public Integer getBattery() {
        return battery;
    }
}