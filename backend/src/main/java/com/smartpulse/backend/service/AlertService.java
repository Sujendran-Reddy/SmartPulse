package com.smartpulse.backend.service;

import com.smartpulse.backend.model.Alert;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> evaluate(TelemetryReading reading) {

        List<Alert> newAlerts = new ArrayList<>();

        boolean highTemperature =
                reading.getTemperature() != null
                        && reading.getTemperature() > 35;

        boolean lowBattery =
                reading.getBattery() != null
                        && reading.getBattery() < 15;

        updateAlert(
                reading.getDeviceId(),
                "HIGH_TEMPERATURE",
                highTemperature,
                "WARNING",
                "Temperature exceeded 35°C",
                newAlerts
        );

        updateAlert(
                reading.getDeviceId(),
                "LOW_BATTERY",
                lowBattery,
                "WARNING",
                "Battery level dropped below 15%",
                newAlerts
        );

        return newAlerts;
    }

    private void updateAlert(
            String deviceId,
            String type,
            boolean conditionActive,
            String severity,
            String message,
            List<Alert> newAlerts
    ) {

        var existing =
                alertRepository.findByDeviceIdAndTypeAndActiveTrue(
                        deviceId,
                        type
                );

        if (conditionActive) {

            if (existing.isEmpty()) {

                Alert alert = new Alert(
                        deviceId,
                        type,
                        severity,
                        message,
                        Instant.now(),
                        true
                );

                newAlerts.add(
                        alertRepository.save(alert)
                );
            }

        } else {

            existing.ifPresent(alert -> {
                alert.setActive(false);
                alertRepository.save(alert);
            });
        }
    }
}