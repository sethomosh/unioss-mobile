package com.example.unioss_mobile.data.network

import com.example.unioss_mobile.data.model.AlertResponse
import com.example.unioss_mobile.data.model.DeviceResponse
import com.example.unioss_mobile.data.model.SignalResponse
import com.example.unioss_mobile.data.model.TowerResponse
import com.example.unioss_mobile.data.model.DeviceDetailsResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/discovery/devices")
    suspend fun getDevices(): List<DeviceResponse>

    @GET("api/alerts/recent")
    suspend fun getAlerts(@Query("limit") limit: Int = 50): List<AlertResponse>

    @POST("api/alerts/{alertId}/acknowledge")
    suspend fun acknowledgeAlert(@Path("alertId") alertId: Int): Any

    @GET("api/devices/{deviceIp}/details")
    suspend fun getDeviceDetails(
        @Path("deviceIp") deviceIp: String,
        @Query("perf_limit") perfLimit: Int = 50,
        @Query("traffic_limit") trafficLimit: Int = 50
    ): DeviceDetailsResponse

    @GET("api/towers/list")
    suspend fun getTowers(): List<TowerResponse>

    @GET("api/signals/latest")
    suspend fun getSignals(): List<SignalResponse>
}