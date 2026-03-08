package com.example.unioss_mobile.utils

import com.example.unioss_mobile.data.model.*

object MockDataProvider {

    val devices = listOf(
        DeviceResponse(ip = "10.1.10.100", hostname = "silas-ubnt", vendor = "Ubiquiti", os_version = "v7.3.1", status = "up", cpu_pct = 14.0, memory_pct = 32.0, uptime_seconds = 295200.0, last_seen = "2026-03-08T14:20:01Z", offline_reason = null, description = "Sector antenna A", error = null, sessions = 2),
        DeviceResponse(ip = "10.1.10.101", hostname = "george-mktk", vendor = "Mikrotik", os_version = "v5.5", status = "up", cpu_pct = 22.0, memory_pct = 51.0, uptime_seconds = 79200.0, last_seen = "2026-03-08T14:20:01Z", offline_reason = null, description = "Core router", error = null, sessions = 5),
        DeviceResponse(ip = "10.2.10.100", hostname = "leo-mktk", vendor = "Mikrotik", os_version = "v3.1", status = "up", cpu_pct = 19.0, memory_pct = 68.0, uptime_seconds = 140400.0, last_seen = "2026-03-08T14:20:01Z", offline_reason = null, description = "Sector antenna B", error = null, sessions = 3),
        DeviceResponse(ip = "10.2.10.101", hostname = "sarah-cisco", vendor = "Cisco", os_version = "v2.3.1.138", status = "up", cpu_pct = 16.0, memory_pct = 65.0, uptime_seconds = 273600.0, last_seen = "2026-03-08T14:20:01Z", offline_reason = null, description = "Distribution switch", error = null, sessions = 8),
        DeviceResponse(ip = "10.3.10.100", hostname = "victor-cisco", vendor = "Cisco", os_version = "v4.3.1", status = "up", cpu_pct = 17.0, memory_pct = 26.0, uptime_seconds = 205200.0, last_seen = "2026-03-08T14:20:01Z", offline_reason = null, description = "Access point", error = null, sessions = 1),
        DeviceResponse(ip = "10.3.10.101", hostname = "luna-ubnt", vendor = "Ubiquiti", os_version = "v7.3.1", status = "up", cpu_pct = 14.0, memory_pct = 83.0, uptime_seconds = 122400.0, last_seen = "2026-03-08T14:20:01Z", offline_reason = null, description = "Sector antenna C", error = null, sessions = 4),
        DeviceResponse(ip = "10.1.20.100", hostname = "dave-mktk", vendor = "Mikrotik", os_version = "v5.1", status = "down", cpu_pct = 44.1, memory_pct = 63.9, uptime_seconds = 90597.0, last_seen = null, offline_reason = "High CPU / Overloaded", description = "Backhaul link", error = null, sessions = 0),
        DeviceResponse(ip = "10.2.20.100", hostname = "felix-ubnt", vendor = "Ubiquiti", os_version = "v8.1", status = "down", cpu_pct = 65.1, memory_pct = 59.9, uptime_seconds = 37601.0, last_seen = null, offline_reason = "SNMP Timeout / Unreachable", description = "Sector antenna D", error = null, sessions = 0),
        DeviceResponse(ip = "10.4.20.100", hostname = "dave-cisco", vendor = "Cisco", os_version = "v4.1.1", status = "down", cpu_pct = 69.8, memory_pct = 59.1, uptime_seconds = 81438.0, last_seen = null, offline_reason = "Tower Power Failure", description = "Tower 4 device", error = null, sessions = 0),
        DeviceResponse(ip = "10.6.10.100", hostname = "victor-ubnt", vendor = "Ubiquiti", os_version = "v7.3.1", status = "up", cpu_pct = 16.0, memory_pct = 25.0, uptime_seconds = 345600.0, last_seen = "2026-03-08T14:20:01Z", offline_reason = null, description = "Main sector", error = null, sessions = 6)
    )

    val alerts = listOf(
        AlertResponse(id = 1, device_ip = "10.4.20.100", severity = "critical", category = "power", message = "Tower Power Failure: 10.4.20.100 is unreachable", timestamp = "2026-03-08T14:15:30Z", acknowledged = false),
        AlertResponse(id = 2, device_ip = "10.2.20.100", severity = "critical", category = "system", message = "High CPU usage detected: 65.1% on felix-ubnt", timestamp = "2026-03-08T13:45:00Z", acknowledged = false),
        AlertResponse(id = 3, device_ip = "10.1.20.100", severity = "warning", category = "performance", message = "CPU threshold exceeded: 44.1% on dave-mktk", timestamp = "2026-03-08T13:30:00Z", acknowledged = false),
        AlertResponse(id = 4, device_ip = "10.3.10.101", severity = "warning", category = "memory", message = "High memory usage: 83% on luna-ubnt", timestamp = "2026-03-08T12:00:00Z", acknowledged = false),
        AlertResponse(id = 5, device_ip = "10.1.10.100", severity = "warning", category = "signal", message = "Weak signal detected on silas-ubnt", timestamp = "2026-03-08T11:00:00Z", acknowledged = true),
        AlertResponse(id = 6, device_ip = "10.2.10.101", severity = "critical", category = "connectivity", message = "Packet loss detected on sarah-cisco", timestamp = "2026-03-08T10:30:00Z", acknowledged = true)
    )

    val towers = listOf(
        com.example.unioss_mobile.data.model.TowerResponse(
            name = "Tower 1",
            devices = listOf(
                TowerDevice(device_ip = "10.1.10.100", hostname = "silas-ubnt", description = "Sector antenna A"),
                TowerDevice(device_ip = "10.1.10.101", hostname = "george-mktk", description = "Core router"),
                TowerDevice(device_ip = "10.1.20.100", hostname = "dave-mktk", description = "Backhaul link")
            )
        ),
        com.example.unioss_mobile.data.model.TowerResponse(
            name = "Tower 2",
            devices = listOf(
                TowerDevice(device_ip = "10.2.10.100", hostname = "leo-mktk", description = "Sector antenna B"),
                TowerDevice(device_ip = "10.2.10.101", hostname = "sarah-cisco", description = "Distribution switch"),
                TowerDevice(device_ip = "10.2.20.100", hostname = "felix-ubnt", description = "Sector antenna D")
            )
        ),
        com.example.unioss_mobile.data.model.TowerResponse(
            name = "Tower 3",
            devices = listOf(
                TowerDevice(device_ip = "10.3.10.100", hostname = "victor-cisco", description = "Access point"),
                TowerDevice(device_ip = "10.3.10.101", hostname = "luna-ubnt", description = "Sector antenna C")
            )
        ),
        com.example.unioss_mobile.data.model.TowerResponse(
            name = "Tower 4",
            devices = listOf(
                TowerDevice(device_ip = "10.4.20.100", hostname = "dave-cisco", description = "Tower 4 device")
            )
        ),
        com.example.unioss_mobile.data.model.TowerResponse(
            name = "Tower 6",
            devices = listOf(
                TowerDevice(device_ip = "10.6.10.100", hostname = "victor-ubnt", description = "Main sector")
            )
        )
    )
}