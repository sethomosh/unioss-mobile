package com.example.unioss_mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unioss_mobile.data.model.AlertResponse
import com.example.unioss_mobile.data.model.DeviceResponse
import com.example.unioss_mobile.data.network.RetrofitClient
import com.example.unioss_mobile.utils.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _devices = MutableStateFlow<List<DeviceResponse>>(emptyList())
    val devices: StateFlow<List<DeviceResponse>> = _devices

    private val _alerts = MutableStateFlow<List<AlertResponse>>(emptyList())
    val alerts: StateFlow<List<AlertResponse>> = _alerts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val url = AppPreferences.getBackendUrl(getApplication()).first()
                RetrofitClient.baseUrl = if (url.endsWith("/")) url else "$url/"
                _devices.value = RetrofitClient.getInstance().getDevices()
                _alerts.value = RetrofitClient.getInstance().getAlerts()
            } catch (e: Exception) {
                _error.value = "Failed to load dashboard: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}