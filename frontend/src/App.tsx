import { useEffect, useMemo, useRef, useState } from 'react'
import { Client } from '@stomp/stompjs'
import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts'

import './App.css'

interface Device {
  id: string
  name: string
  type: string
  location: string
  status: string
  lastCommunication: string
}

interface TelemetryReading {
  id: string
  deviceId: string
  timestamp: string
  temperature: number
  humidity: number
  battery: number
}

function App() {
  const [devices, setDevices] = useState<Device[]>([])
  const [readings, setReadings] = useState<TelemetryReading[]>([])
  const [latest, setLatest] = useState<TelemetryReading | null>(null)
  const [connected, setConnected] = useState(false)
  const [eventsPerSecond, setEventsPerSecond] = useState(0)

  const eventCounter = useRef(0)

  useEffect(() => {
    async function loadInitialData() {
      try {
        const [deviceResponse, telemetryResponse] = await Promise.all([
          fetch('/api/devices'),
          fetch('/api/telemetry'),
        ])

        const deviceData: Device[] = await deviceResponse.json()
        const telemetryData: TelemetryReading[] =
          await telemetryResponse.json()

        const recent = telemetryData.slice(-30)

        setDevices(deviceData)
        setReadings(recent)

        if (recent.length > 0) {
          setLatest(recent[recent.length - 1])
        }
      } catch (error) {
        console.error('Initial data load failed:', error)
      }
    }

    loadInitialData()
  }, [])

  useEffect(() => {
    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws',
      reconnectDelay: 3000,
      debug: () => {},
    })

    client.onConnect = () => {
      setConnected(true)

      client.subscribe('/topic/telemetry', message => {
        const reading: TelemetryReading =
          JSON.parse(message.body)

        eventCounter.current += 1

        setLatest(reading)

        setReadings(previous => [
          ...previous.slice(-29),
          reading,
        ])

        setDevices(previous =>
          previous.map(device =>
            device.id === reading.deviceId
              ? {
                  ...device,
                  status: 'ONLINE',
                  lastCommunication: reading.timestamp,
                }
              : device
          )
        )
      })
    }

    client.onWebSocketClose = () => {
      setConnected(false)
    }

    client.onStompError = () => {
      setConnected(false)
    }

    client.activate()

    return () => {
      client.deactivate()
    }
  }, [])

  useEffect(() => {
    const interval = setInterval(() => {
      setEventsPerSecond(eventCounter.current)
      eventCounter.current = 0
    }, 1000)

    return () => clearInterval(interval)
  }, [])

  const onlineDevices = useMemo(
    () =>
      devices.filter(device => device.status === 'ONLINE').length,
    [devices]
  )

  const offlineDevices = devices.length - onlineDevices

  const chartData = readings.map(reading => ({
    time: new Date(reading.timestamp).toLocaleTimeString(),
    temperature: reading.temperature,
    humidity: reading.humidity,
  }))

  return (
    <main className="dashboard">

      <header className="header">
        <div>
          <h1>SmartPulse</h1>
          <p>Real-Time IoT Monitoring Platform</p>
        </div>

        <div className={connected ? 'connection live' : 'connection'}>
          <span />
          {connected ? 'LIVE' : 'DISCONNECTED'}
        </div>
      </header>

      <section className="stats">

        <article className="card">
          <span className="label">Devices Online</span>
          <strong>{onlineDevices}</strong>
        </article>

        <article className="card">
          <span className="label">Devices Offline</span>
          <strong>{offlineDevices}</strong>
        </article>

        <article className="card">
          <span className="label">Events / Second</span>
          <strong>{eventsPerSecond}</strong>
        </article>

        <article className="card">
          <span className="label">Total Devices</span>
          <strong>{devices.length}</strong>
        </article>

      </section>

      <section className="telemetry-grid">

        <article className="metric-card">
          <span>Temperature</span>
          <strong>
            {latest
              ? `${latest.temperature.toFixed(1)} °C`
              : '--'}
          </strong>
        </article>

        <article className="metric-card">
          <span>Humidity</span>
          <strong>
            {latest
              ? `${latest.humidity.toFixed(1)} %`
              : '--'}
          </strong>
        </article>

        <article className="metric-card">
          <span>Battery</span>
          <strong>
            {latest
              ? `${latest.battery} %`
              : '--'}
          </strong>
        </article>

      </section>

      <section className="panel">

        <div className="panel-header">
          <div>
            <h2>Live Telemetry</h2>
            <p>Latest 30 readings</p>
          </div>
        </div>

        <div className="chart">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />

              <XAxis
                dataKey="time"
                minTickGap={35}
              />

              <YAxis />

              <Tooltip />

              <Line
                type="monotone"
                dataKey="temperature"
                stroke="#4f8cff"
                strokeWidth={2}
                dot={false}
              />

              <Line
                type="monotone"
                dataKey="humidity"
                stroke="#29c78e"
                strokeWidth={2}
                dot={false}
              />

            </LineChart>
          </ResponsiveContainer>
        </div>

      </section>

      <section className="panel">

        <div className="panel-header">
          <div>
            <h2>Devices</h2>
            <p>Registered SmartPulse devices</p>
          </div>
        </div>

        <div className="device-table">

          <div className="device-row device-heading">
            <span>Name</span>
            <span>Type</span>
            <span>Location</span>
            <span>Status</span>
          </div>

          {devices.map(device => (
            <div
              className="device-row"
              key={device.id}
            >
              <strong>{device.name}</strong>
              <span>{device.type}</span>
              <span>{device.location}</span>

              <span
                className={
                  device.status === 'ONLINE'
                    ? 'status online'
                    : 'status'
                }
              >
                {device.status}
              </span>
            </div>
          ))}

        </div>

      </section>

    </main>
  )
}

export default App