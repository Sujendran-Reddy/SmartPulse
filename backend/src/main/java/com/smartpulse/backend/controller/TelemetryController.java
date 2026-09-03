package com.smartpulse.backend.controller;

import com.smartpulse.backend.model.Device;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.repository.DeviceRepository;
import com.smartpulse.backend.repository.TelemetryRepository;
import org.springframework.web.bind.annotation.*;

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
            @RequestBody Map<String, Object> request
    ) {

        String deviceId = (String) request.get("deviceId");

        Device device = deviceRepository
                .findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        Instant now = Instant.now();

        TelemetryReading reading = new TelemetryReading(
                deviceId,
                now,
                ((Number) request.get("temperature")).doubleValue(),
                ((Number) request.get("humidity")).doubleValue(),
                ((Number) request.get("battery")).intValue()
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