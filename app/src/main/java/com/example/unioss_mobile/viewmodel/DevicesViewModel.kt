package com.example.unioss_mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unioss_mobile.data.model.DeviceResponse
import com.example.unioss_mobile.data.network.RetrofitClient
import com.example.unioss_mobile.utils.AppPreferences
import com.example.unioss_mobile.utils.MockDataProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DevicesViewModel(application: Application) : AndroidViewModel(application) {

    private val _devices = MutableStateFlow<List<DeviceResponse>>(emptyList())
    val devices: StateFlow<List<DeviceResponse>> = _devices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchDevices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val url = AppPreferences.getBackendUrl(getApplication()).first()
                android.util.Log.d("DEBUG_URL", "Raw URL from prefs: '$url'")
                android.util.Log.d("DEBUG_URL", "Base URL set to: '${RetrofitClient.baseUrl}'")
                RetrofitClient.baseUrl = if (url.endsWith("/")) url else "$url/"
                _devices.value = RetrofitClient.getInstance().getDevices()
            } catch (e: Exception) {
                android.util.Log.e("DEBUG_URL", "Error: ${e.javaClass.name} - ${e.message}")
                if (_devices.value.isEmpty()) {
                    _devices.value = MockDataProvider.devices
                    _error.value = "DEMO_MODE"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}