package com.example.unioss_mobile.data.model

data class DeviceSnapshot(
    val cpu_pct: Double?,
    val memory_pct: Double?,
    val uptime_seconds: Double?,
    val timestamp: String?
)

data class PerformanceHistory(
    val timestamp: String?,
    val cpu_pct: Double?,
    val memory_pct: Double?
)

data class TrafficHistory(
    val timestamp: String?,
    val interface_name: String?,
    val inbound_kbps: Double?,
    val outbound_kbps: Double?,
    val errors: Int?
)

data class DeviceDetailsResponse(
    val device_ip: String?,
    val signal: SignalData?,
    val snapshot: DeviceSnapshot?,
    val performance_history: List<PerformanceHistory>?,
    val traffic_history: List<TrafficHistory>?,
    val latest_per_interface: List<TrafficHistory>?
)