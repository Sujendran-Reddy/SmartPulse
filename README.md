# SmartPulse

Real-time IoT telemetry monitoring platform built with Spring Boot, MQTT, RabbitMQ, MongoDB, WebSockets, React and TypeScript.

## Overview

SmartPulse simulates a real IoT monitoring environment where devices continuously publish telemetry such as temperature, humidity and battery level.

Telemetry enters the platform through MQTT, is forwarded through RabbitMQ for asynchronous processing, persisted to MongoDB and broadcast to a React dashboard in real time through WebSockets.

## Architecture

```text
Device Simulator
       |
       | MQTT
       v
Eclipse Mosquitto
       |
       v
Spring Boot MQTT Ingestion
       |
       v
RabbitMQ
       |
       v
Telemetry Processor
       |
       +-------------------+
       |                   |
       v                   v
    MongoDB            WebSocket
                           |
                           v
                   React Dashboard
```

## Features

* Device registration and management
* Unique device API keys
* MQTT telemetry ingestion
* Asynchronous processing with RabbitMQ
* MongoDB telemetry persistence
* Real-time WebSocket broadcasting
* React + TypeScript monitoring dashboard
* Live temperature, humidity and battery metrics
* Device online/offline state tracking
* Events-per-second monitoring
* Automatic telemetry simulator
* High-temperature alerts
* Low-battery alerts
* Automatic alert resolution
* Request validation and API error handling

## Technology Stack

### Backend

* Java 22
* Spring Boot
* Spring Integration
* Spring AMQP
* Spring WebSocket
* Maven

### Messaging

* Eclipse Mosquitto
* MQTT
* RabbitMQ

### Database

* MongoDB

### Frontend

* React
* TypeScript
* Vite
* STOMP WebSockets
* Recharts

### Infrastructure

* Docker
* Docker Compose

## Telemetry Flow

A simulated device publishes telemetry to:

```text
smartpulse/devices/{deviceId}/telemetry
```

Example payload:

```json
{
  "temperature": 27.4,
  "humidity": 61.2,
  "battery": 82
}
```

Spring Boot subscribes to the MQTT topic and forwards the event to RabbitMQ.

A RabbitMQ consumer validates and processes the event, updates the device state, persists the telemetry to MongoDB and broadcasts the new reading through WebSockets.

## REST API

### Devices

```text
POST   /api/devices
GET    /api/devices
GET    /api/devices/{id}
DELETE /api/devices/{id}
```

### Telemetry

```text
POST /api/telemetry
GET  /api/telemetry
GET  /api/telemetry/device/{deviceId}
```

### Alerts

```text
GET /api/alerts
GET /api/alerts/active
```

### Health

```text
GET /api/health
```

## Running SmartPulse

### 1. Start infrastructure

```powershell
docker compose up -d
```

This starts:

* Eclipse Mosquitto
* RabbitMQ

### 2. Start backend

From the `backend` directory:

```powershell
mvn spring-boot:run
```

Backend:

```text
http://localhost:8080
```

### 3. Start simulator

From the `simulator` directory:

```powershell
npm install
npm start
```

### 4. Start frontend

From the `frontend` directory:

```powershell
npm install
npm run dev
```

Dashboard:

```text
http://localhost:5173
```

## RabbitMQ Management

RabbitMQ management UI:

```text
http://localhost:15672
```

Development credentials are configured through Docker Compose.

## Load Testing

The telemetry ingestion pipeline was tested using the SmartPulse simulator at an accelerated publish interval.

Measured throughput on the development machine:

```text
YOUR_EPS telemetry events/second
```

The test exercised the complete processing path:

```text
Simulator
→ MQTT
→ Mosquitto
→ Spring Boot
→ RabbitMQ
→ MongoDB
→ WebSocket broadcast
```

This is a development-machine benchmark rather than a production capacity claim.

## Project Structure

```text
SmartPulse/
├── backend/
│   └── Spring Boot backend
│
├── frontend/
│   └── React + TypeScript dashboard
│
├── simulator/
│   └── MQTT telemetry simulator
│
├── infrastructure/
│   └── Mosquitto configuration
│
├── docker-compose.yml
└── README.md
```

## Alerts

SmartPulse currently supports:

```text
Temperature > 35°C
→ HIGH_TEMPERATURE warning

Battery < 15%
→ LOW_BATTERY warning
```

Active alerts automatically resolve when telemetry returns to normal.

## Future Improvements

Potential extensions include:

* configurable alert rules
* authentication and authorization
* per-device dashboards
* device provisioning
* historical analytics
* larger distributed load tests
* anomaly detection
* deployment to cloud infrastructure

## Purpose

SmartPulse was built as a portfolio project demonstrating real-time data ingestion, IoT messaging, asynchronous event processing, persistence, WebSockets, frontend visualization, Docker infrastructure and load testing.
