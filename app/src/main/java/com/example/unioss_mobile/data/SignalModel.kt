package com.example.unioss_mobile.data.model

data class SignalResponse(
    val device_ip: String,
    val interface_index: Int,
    val interface_name: String,
    val rssi_dbm: Double,
    val rssi_pct: Double,
    val snr_db: Double,
    val tx_rate_mbps: Double,
    val rx_rate_mbps: Double,
    val link_quality_pct: Double,
    val frequency_mhz: Int,
    val timestamp: String
)