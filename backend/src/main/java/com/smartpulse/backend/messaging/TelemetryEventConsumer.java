package com.smartpulse.backend.messaging;

import com.smartpulse.backend.dto.CreateTelemetryRequest;
import com.smartpulse.backend.model.Alert;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.service.AlertService;
import com.smartpulse.backend.service.TelemetryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Component
public class TelemetryEventConsumer {

    private final JsonMapper jsonMapper;
    private final TelemetryService telemetryService;
    private final AlertService alertService;
    private final SimpMessagingTemplate messagingTemplate;

    public TelemetryEventConsumer(
            JsonMapper jsonMapper,
            TelemetryService telemetryService,
            AlertService alertService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.jsonMapper = jsonMapper;
        this.telemetryService = telemetryService;
        this.alertService = alertService;
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

            List<Alert> alerts =
                    alertService.evaluate(reading);

            messagingTemplate.convertAndSend(
                    "/topic/telemetry",
                    reading
            );

            messagingTemplate.convertAndSend(
                    "/topic/telemetry/" + reading.getDeviceId(),
                    reading
            );

            for (Alert alert : alerts) {
                messagingTemplate.convertAndSend(
                        "/topic/alerts",
                        alert
                );
            }

            System.out.println(
                    "LIVE | Device: " + reading.getDeviceId()
                            + " | Temp: " + reading.getTemperature()
                            + " | Humidity: " + reading.getHumidity()
                            + " | Battery: " + reading.getBattery()
                            + " | New Alerts: " + alerts.size()
            );

        } catch (Exception exception) {

            System.err.println(
                    "Telemetry processing failed: "
                            + exception.getMessage()
            );
        }
    }
}