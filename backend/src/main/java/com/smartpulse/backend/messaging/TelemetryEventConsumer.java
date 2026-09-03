package com.smartpulse.backend.messaging;

import com.smartpulse.backend.dto.CreateTelemetryRequest;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.service.TelemetryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class TelemetryEventConsumer {

    private final JsonMapper jsonMapper;
    private final TelemetryService telemetryService;
    private final SimpMessagingTemplate messagingTemplate;

    public TelemetryEventConsumer(
            JsonMapper jsonMapper,
            TelemetryService telemetryService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.jsonMapper = jsonMapper;
        this.telemetryService = telemetryService;
        this.messagingTemplate = messagingTemplate;
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

            // Broadcast every reading to the main dashboard.
            messagingTemplate.convertAndSend(
                    "/topic/telemetry",
                    reading
            );

            // Also broadcast to subscribers interested
            // in one specific device.
            messagingTemplate.convertAndSend(
                    "/topic/telemetry/" + reading.getDeviceId(),
                    reading
            );

            System.out.println(
                    "========== LIVE TELEMETRY =========="
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
                    "MongoDB: SAVED"
            );

            System.out.println(
                    "WebSocket: BROADCAST"
            );

            System.out.println(
                    "===================================="
            );

        } catch (Exception exception) {

            System.err.println(
                    "Telemetry processing failed: "
                            + exception.getMessage()
            );
        }
    }
}