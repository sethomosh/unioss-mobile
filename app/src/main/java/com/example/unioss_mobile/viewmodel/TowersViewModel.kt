package com.example.unioss_mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unioss_mobile.data.model.DeviceResponse
import com.example.unioss_mobile.utils.MockDataProvider
import com.example.unioss_mobile.data.model.TowerResponse
import com.example.unioss_mobile.data.network.RetrofitClient
import com.example.unioss_mobile.utils.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class TowersViewModel(application: Application) : AndroidViewModel(application) {

    private val _towers = MutableStateFlow<List<TowerResponse>>(emptyList())
    val towers: StateFlow<List<TowerResponse>> = _towers

    private val _allDevices = MutableStateFlow<List<DeviceResponse>>(emptyList())
    val allDevices: StateFlow<List<DeviceResponse>> = _allDevices

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchTowers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val url = AppPreferences.getBackendUrl(getApplication()).first()
                RetrofitClient.baseUrl = if (url.endsWith("/")) url else "$url/"
                val api = RetrofitClient.getInstance()
                _towers.value = api.getTowers()
                _allDevices.value = api.getDevices()
            } catch (e: Exception) {
                if (_towers.value.isEmpty()) {
                    _towers.value = MockDataProvider.towers
                    _allDevices.value = MockDataProvider.devices
                    _error.value = "DEMO_MODE"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}