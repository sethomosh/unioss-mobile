package com.example.unioss_mobile.data.model

data class DeviceResponse(
    val ip: String?,
    val hostname: String?,
    val vendor: String?,
    val os_version: String?,
    val status: String?,
    val cpu_pct: Double,
    val memory_pct: Double,
    val uptime_seconds: Double,
    val last_seen: String?,
    val offline_reason: String?,
    val description: String?,
    val error: String?,
    val sessions: Int?
) {
    val device_ip: String? get() = ip
    val online: Boolean get() = status == "up"
    val interfaces: List<InterfaceData> get() = emptyList()
    val signal: SignalData? get() = null
}

data class InterfaceData(
    val interface_name: String,
    val inbound_kbps: Double,
    val outbound_kbps: Double,
    val errors: Int
)

data class SignalData(
    val rssi_dbm: Double?,
    val rssi_pct: Double?,
    val snr_db: Double?,
    val timestamp: String?
)