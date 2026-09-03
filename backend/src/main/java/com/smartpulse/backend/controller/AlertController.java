package com.smartpulse.backend.controller;

import com.smartpulse.backend.model.Alert;
import com.smartpulse.backend.repository.AlertRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertRepository alertRepository;

    public AlertController(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @GetMapping("/active")
    public List<Alert> getActiveAlerts() {
        return alertRepository
                .findByActiveTrueOrderByTimestampDesc();
    }
}
