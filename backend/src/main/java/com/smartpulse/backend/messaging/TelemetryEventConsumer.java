package com.smartpulse.backend.messaging;

import com.smartpulse.backend.dto.CreateTelemetryRequest;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.service.TelemetryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class TelemetryEventConsumer {

    private final JsonMapper jsonMapper;
    private final TelemetryService telemetryService;

    public TelemetryEventConsumer(
            JsonMapper jsonMapper,
            TelemetryService telemetryService
    ) {
        this.jsonMapper = jsonMapper;
        this.telemetryService = telemetryService;
    }

    @RabbitListener(
            queues = "${smartpulse.rabbitmq.queue}"
    )
    public void consume(String eventJson) {

        try {

            CreateTelemetryRequest request =
                    jsonMapper.readValue(
                            eventJson,
                            CreateTelemetryRequest.class
                    );

            TelemetryReading reading =
                    telemetryService.processTelemetry(request);

            System.out.println(
                    "========== RABBITMQ TELEMETRY =========="
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
                    "Persisted to MongoDB"
            );

            System.out.println(
                    "========================================"
            );

        } catch (Exception exception) {

            System.err.println(
                    "RabbitMQ telemetry processing failed: "
                            + exception.getMessage()
            );
        }
    }
}