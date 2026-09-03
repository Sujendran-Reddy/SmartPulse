package com.smartpulse.backend.config;

import com.smartpulse.backend.dto.CreateTelemetryRequest;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.service.TelemetryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class MqttConfig {

    private final String mqttUrl;
    private final String clientId;
    private final String topic;

    private final JsonMapper jsonMapper;
    private final TelemetryService telemetryService;

    public MqttConfig(
            @Value("${smartpulse.mqtt.url}") String mqttUrl,
            @Value("${smartpulse.mqtt.client-id}") String clientId,
            @Value("${smartpulse.mqtt.topic}") String topic,
            JsonMapper jsonMapper,
            TelemetryService telemetryService
    ) {
        this.mqttUrl = mqttUrl;
        this.clientId = clientId;
        this.topic = topic;
        this.jsonMapper = jsonMapper;
        this.telemetryService = telemetryService;
    }

    @Bean
    public IntegrationFlow mqttInboundFlow() {

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        mqttUrl,
                        clientId,
                        topic
                );

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);

        return IntegrationFlow
                .from(adapter)
                .handle(message -> {

                    try {

                        String payload =
                                message.getPayload().toString();

                        String receivedTopic =
                                (String) message
                                        .getHeaders()
                                        .get("mqtt_receivedTopic");

                        String deviceId =
                                extractDeviceId(receivedTopic);

                        CreateTelemetryRequest request =
                                jsonMapper.readValue(
                                        payload,
                                        CreateTelemetryRequest.class
                                );

                        request.setDeviceId(deviceId);

                        TelemetryReading reading =
                                telemetryService.processTelemetry(request);

                        System.out.println(
                                "========== MQTT TELEMETRY =========="
                        );

                        System.out.println(
                                "Device: " + reading.getDeviceId()
                        );

                        System.out.println(
                                "Temperature: "
                                        + reading.getTemperature()
                        );

                        System.out.println(
                                "Humidity: "
                                        + reading.getHumidity()
                        );

                        System.out.println(
                                "Battery: "
                                        + reading.getBattery()
                        );

                        System.out.println(
                                "Saved to MongoDB"
                        );

                        System.out.println(
                                "===================================="
                        );

                    } catch (Exception exception) {

                        System.err.println(
                                "Invalid MQTT telemetry: "
                                        + exception.getMessage()
                        );
                    }

                })
                .get();
    }

    private String extractDeviceId(String topic) {

        if (topic == null) {
            throw new IllegalArgumentException(
                    "MQTT topic is missing"
            );
        }

        String[] parts = topic.split("/");

        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "Invalid MQTT topic: " + topic
            );
        }

        return parts[2];
    }
}