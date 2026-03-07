package com.example.unioss_mobile.data.network

import com.example.unioss_mobile.data.model.AlertResponse
import com.example.unioss_mobile.data.model.DeviceResponse
import com.example.unioss_mobile.data.model.SignalResponse
import com.example.unioss_mobile.data.model.TowerResponse
import retrofit2.http.GET

interface ApiService {

    @GET("api/discovery/devices")
    suspend fun getDevices(): List<DeviceResponse>

    @GET("api/alerts/recent")
    suspend fun getAlerts(): List<AlertResponse>

    @GET("api/towers/list")
    suspend fun getTowers(): List<TowerResponse>

    @GET("api/signals/latest")
    suspend fun getSignals(): List<SignalResponse>
}