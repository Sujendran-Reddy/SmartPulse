package com.smartpulse.backend.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange telemetryExchange(
            @Value("${smartpulse.rabbitmq.exchange}")
            String exchangeName
    ) {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Queue telemetryQueue(
            @Value("${smartpulse.rabbitmq.queue}")
            String queueName
    ) {
        return QueueBuilder
                .durable(queueName)
                .build();
    }

    @Bean
    public Binding telemetryBinding(
            Queue telemetryQueue,
            DirectExchange telemetryExchange,
            @Value("${smartpulse.rabbitmq.routing-key}")
            String routingKey
    ) {
        return BindingBuilder
                .bind(telemetryQueue)
                .to(telemetryExchange)
                .with(routingKey);
    }
}