package com.smartpulse.backend.config;

import com.smartpulse.backend.dto.CreateTelemetryRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private final String rabbitExchange;
    private final String rabbitRoutingKey;

    private final JsonMapper jsonMapper;
    private final RabbitTemplate rabbitTemplate;

    public MqttConfig(
            @Value("${smartpulse.mqtt.url}")
            String mqttUrl,

            @Value("${smartpulse.mqtt.client-id}")
            String clientId,

            @Value("${smartpulse.mqtt.topic}")
            String topic,

            @Value("${smartpulse.rabbitmq.exchange}")
            String rabbitExchange,

            @Value("${smartpulse.rabbitmq.routing-key}")
            String rabbitRoutingKey,

            JsonMapper jsonMapper,
            RabbitTemplate rabbitTemplate
    ) {
        this.mqttUrl = mqttUrl;
        this.clientId = clientId;
        this.topic = topic;
        this.rabbitExchange = rabbitExchange;
        this.rabbitRoutingKey = rabbitRoutingKey;
        this.jsonMapper = jsonMapper;
        this.rabbitTemplate = rabbitTemplate;
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
        adapter.setConverter(
                new DefaultPahoMessageConverter()
        );
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

                        String eventJson =
                                jsonMapper.writeValueAsString(request);

                        rabbitTemplate.convertAndSend(
                                rabbitExchange,
                                rabbitRoutingKey,
                                eventJson
                        );

                        System.out.println(
                                "MQTT -> RabbitMQ | Device: "
                                        + deviceId
                        );

                    } catch (Exception exception) {

                        System.err.println(
                                "MQTT ingestion failed: "
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