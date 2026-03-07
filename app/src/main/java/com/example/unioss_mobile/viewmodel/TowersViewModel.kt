package com.example.unioss_mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.unioss_mobile.data.model.DeviceResponse
import com.example.unioss_mobile.data.model.TowerResponse
import com.example.unioss_mobile.data.model.TowerDevice
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
                val devices = RetrofitClient.getInstance().getDevices()
                _allDevices.value = devices

                // Group devices by tower using hostname pattern
                val buckets = mutableMapOf<String, MutableList<DeviceResponse>>()
                devices.forEach { device ->
                    val hostname = device.hostname ?: ""
                    val towerName = when {
                        hostname.startsWith("Tower", ignoreCase = true) -> hostname
                        device.description?.contains("Tower", ignoreCase = true) == true -> {
                            Regex("Tower \\d+", RegexOption.IGNORE_CASE)
                                .find(device.description)?.value ?: "Ungrouped"
                        }
                        else -> null // skip tower devices themselves from client list
                    }
                    if (towerName != null && !hostname.startsWith("Tower", ignoreCase = true)) {
                        buckets.getOrPut(towerName) { mutableListOf() }.add(device)
                    }
                }

                // Find tower head devices
                val towerHeads = devices.filter {
                    it.hostname?.startsWith("Tower", ignoreCase = true) == true
                }

                _towers.value = towerHeads.map { head ->
                    val towerName = head.hostname ?: "Unknown"
                    val clients = buckets[towerName] ?: emptyList()
                    TowerResponse(
                        name = towerName,
                        devices = clients.map { d ->
                            TowerDevice(
                                device_ip = d.device_ip ?: "",
                                hostname = d.hostname ?: d.device_ip ?: "",
                                description = d.description ?: ""
                            )
                        }
                    )
                }.sortedBy { it.name }

            } catch (e: Exception) {
                _error.value = "Failed to load towers: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}