package com.smartpulse.backend.controller;

import com.smartpulse.backend.dto.CreateDeviceRequest;
import com.smartpulse.backend.model.Device;
import com.smartpulse.backend.repository.DeviceRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceRepository deviceRepository;

    public DeviceController(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @PostMapping
    public Device createDevice(
            @Valid @RequestBody CreateDeviceRequest request
    ) {

        Device device = new Device(
                request.getName(),
                request.getType(),
                request.getLocation(),
                "OFFLINE",
                UUID.randomUUID().toString(),
                Instant.now()
        );

        return deviceRepository.save(device);
    }

    @GetMapping
    public List<Device> getDevices() {
        return deviceRepository.findAll();
    }

    @GetMapping("/{id}")
    public Device getDeviceById(@PathVariable String id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Device not found"
                ));
    }

    @DeleteMapping("/{id}")
    public void deleteDevice(@PathVariable String id) {

        if (!deviceRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Device not found"
            );
        }

        deviceRepository.deleteById(id);
    }
}