package com.smartpulse.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "alerts")
public class Alert {

    @Id
    private String id;

    private String deviceId;
    private String type;
    private String severity;
    private String message;
    private Instant timestamp;
    private boolean active;

    public Alert() {
    }

    public Alert(
            String deviceId,
            String type,
            String severity,
            String message,
            Instant timestamp,
            boolean active
    ) {
        this.deviceId = deviceId;
        this.type = type;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}