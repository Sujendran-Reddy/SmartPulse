package com.smartpulse.backend.repository;

import com.smartpulse.backend.model.Alert;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository
        extends MongoRepository<Alert, String> {

    Optional<Alert> findByDeviceIdAndTypeAndActiveTrue(
            String deviceId,
            String type
    );

    List<Alert> findByActiveTrueOrderByTimestampDesc();
}