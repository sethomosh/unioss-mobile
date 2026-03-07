package com.example.unioss_mobile.data.model

data class InterfaceData(
    val interface_name: String,
    val inbound_kbps: Double,
    val outbound_kbps: Double,
    val errors: Int
)

data class SignalData(
    val rssi_dbm: Double,
    val rssi_pct: Double,
    val snr_db: Double,
    val timestamp: String
)

data class DeviceResponse(
    val device_ip: String?,
    val hostname: String?,
    val description: String?,
    val vendor: String?,
    val os_version: String?,
    val status: String?,
    val online: Boolean,
    val cpu_pct: Double,
    val memory_pct: Double,
    val uptime_seconds: Double,
    val last_seen: String?,
    val error: String?,
    val interfaces: List<InterfaceData>,
    val signal: SignalData?
)