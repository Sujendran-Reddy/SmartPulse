package com.smartpulse.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateDeviceRequest {

    @NotBlank(message = "Device name is required")
    private String name;

    @NotBlank(message = "Device type is required")
    private String type;

    @NotBlank(message = "Location is required")
    private String location;

    public CreateDeviceRequest() {
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
}