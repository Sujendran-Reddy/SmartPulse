package com.smartpulse.backend.repository;

import com.smartpulse.backend.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepository extends MongoRepository<Device, String> {
}