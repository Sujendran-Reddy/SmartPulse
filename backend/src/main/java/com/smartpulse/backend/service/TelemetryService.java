package com.smartpulse.backend.service;

import com.smartpulse.backend.dto.CreateTelemetryRequest;
import com.smartpulse.backend.model.Device;
import com.smartpulse.backend.model.TelemetryReading;
import com.smartpulse.backend.repository.DeviceRepository;
import com.smartpulse.backend.repository.TelemetryRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Set;

@Service
public class TelemetryService {

    private final TelemetryRepository telemetryRepository;
    private final DeviceRepository deviceRepository;
    private final Validator validator;

    public TelemetryService(
            TelemetryRepository telemetryRepository,
            DeviceRepository deviceRepository,
            Validator validator
    ) {
        this.telemetryRepository = telemetryRepository;
        this.deviceRepository = deviceRepository;
        this.validator = validator;
    }

    public TelemetryReading processTelemetry(
            CreateTelemetryRequest request
    ) {

        Set<ConstraintViolation<CreateTelemetryRequest>> violations =
                validator.validate(request);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

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
}