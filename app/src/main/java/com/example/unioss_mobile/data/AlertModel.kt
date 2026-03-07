package com.example.unioss_mobile.data.model

data class AlertResponse(
    val id: Int,
    val device_ip: String,
    val severity: String,
    val message: String,
    val timestamp: String,
    val acknowledged: Boolean,
    val category: String
)