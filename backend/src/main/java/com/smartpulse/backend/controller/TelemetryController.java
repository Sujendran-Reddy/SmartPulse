package com.smartpulse.backend.controller;

import com.smartpulse.backend.model.Device;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.repository.DeviceRepository;
import com.smartpulse.backend.repository.TelemetryRepository;
import org.springframework.web.bind.annotation.*;
import com.smartpulse.backend.dto.CreateTelemetryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/telemetry")
public class TelemetryController {

    private final TelemetryRepository telemetryRepository;
    private final DeviceRepository deviceRepository;

    public TelemetryController(
            TelemetryRepository telemetryRepository,
            DeviceRepository deviceRepository
    ) {
        this.telemetryRepository = telemetryRepository;
        this.deviceRepository = deviceRepository;
    }

    @PostMapping
    public TelemetryReading createTelemetry(
            @Valid @RequestBody CreateTelemetryRequest request
    ) {

        Device device = deviceRepository
                .findById(request.getDeviceId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Device not found"
                ));

        Instant now = Instant.now();

        TelemetryReading reading = new TelemetryReading(
                device.getId(),
                now,
                request.getTemperature(),
                request.getHumidity(),
                request.getBattery()
        );

        device.setStatus("ONLINE");
        device.setLastCommunication(now);

        deviceRepository.save(device);

        return telemetryRepository.save(reading);
    }

    @GetMapping
    public List<TelemetryReading> getAllTelemetry() {
        return telemetryRepository.findAll();
    }

    @GetMapping("/device/{deviceId}")
    public List<TelemetryReading> getByDevice(
            @PathVariable String deviceId
    ) {
        return telemetryRepository.findByDeviceId(deviceId);
    }
}