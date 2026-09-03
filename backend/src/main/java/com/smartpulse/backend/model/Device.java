package com.smartpulse.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "devices")
public class Device {

    @Id
    private String id;

    private String name;
    private String type;
    private String location;
    private String status;
    private String apiKey;
    private Instant lastCommunication;

    public Device() {
    }

    public Device(
            String name,
            String type,
            String location,
            String status,
            String apiKey,
            Instant lastCommunication
    ) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.status = status;
        this.apiKey = apiKey;
        this.lastCommunication = lastCommunication;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Instant getLastCommunication() {
        return lastCommunication;
    }

    public void setLastCommunication(Instant lastCommunication) {
        this.lastCommunication = lastCommunication;
    }
}