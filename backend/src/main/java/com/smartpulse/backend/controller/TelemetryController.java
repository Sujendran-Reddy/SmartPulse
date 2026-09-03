package com.smartpulse.backend.controller;

import com.smartpulse.backend.dto.CreateTelemetryRequest;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.repository.TelemetryRepository;
import com.smartpulse.backend.service.TelemetryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryService telemetryService;
    private final TelemetryRepository telemetryRepository;

    public TelemetryController(
            TelemetryService telemetryService,
            TelemetryRepository telemetryRepository
    ) {
        this.telemetryService = telemetryService;
        this.telemetryRepository = telemetryRepository;
    }

    @PostMapping
    public TelemetryReading createTelemetry(
            @Valid @RequestBody CreateTelemetryRequest request
    ) {
        return telemetryService.processTelemetry(request);
    }

    @GetMapping
    public List<TelemetryReading> getAllTelemetry() {
        return telemetryRepository.findAll();
    }

    @GetMapping("/device/{deviceId}")
    public List<TelemetryReading> getTelemetryByDevice(
            @PathVariable String deviceId
    ) {
        return telemetryRepository.findByDeviceId(deviceId);
    }
}