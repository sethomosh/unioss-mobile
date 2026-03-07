package com.example.unioss_mobile.data.model

data class TowerDevice(
    val device_ip: String,
    val hostname: String,
    val description: String
)

data class TowerResponse(
    val name: String,
    val devices: List<TowerDevice>
)