package com.smartpulse.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

@Configuration
public class MqttConfig {

    @Value("${smartpulse.mqtt.url}")
    private String mqttUrl;

    @Value("${smartpulse.mqtt.client-id}")
    private String clientId;

    @Value("${smartpulse.mqtt.topic}")
    private String topic;

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

                    System.out.println("========== MQTT MESSAGE ==========");
                    System.out.println("Payload: " + message.getPayload());
                    System.out.println("Headers: " + message.getHeaders());
                    System.out.println("==================================");

                })
                .get();
    }
}