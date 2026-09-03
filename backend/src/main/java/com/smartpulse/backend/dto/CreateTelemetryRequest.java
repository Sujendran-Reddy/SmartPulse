package com.smartpulse.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTelemetryRequest {

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    @NotNull(message = "Temperature is required")
    private Double temperature;

    @NotNull(message = "Humidity is required")
    @Min(value = 0, message = "Humidity cannot be below 0")
    @Max(value = 100, message = "Humidity cannot exceed 100")
    private Double humidity;

    @NotNull(message = "Battery is required")
    @Min(value = 0, message = "Battery cannot be below 0")
    @Max(value = 100, message = "Battery cannot exceed 100")
    private Integer battery;

    public CreateTelemetryRequest() {
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Integer getBattery() {
        return battery;
    }

    public void setBattery(Integer battery) {
        this.battery = battery;
    }
}