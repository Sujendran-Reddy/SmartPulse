package com.smartpulse.backend.repository;

import com.smartpulse.backend.model.TelemetryReading;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TelemetryRepository
        extends MongoRepository<TelemetryReading, String> {

    List<TelemetryReading> findByDeviceId(String deviceId);
}