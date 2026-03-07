package com.example.unioss_mobile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unioss_mobile.data.model.SignalResponse
import com.example.unioss_mobile.data.model.TowerResponse
import com.example.unioss_mobile.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TowersViewModel : ViewModel() {

    private val _towers = MutableStateFlow<List<TowerResponse>>(emptyList())
    val towers: StateFlow<List<TowerResponse>> = _towers

    private val _signals = MutableStateFlow<List<SignalResponse>>(emptyList())
    val signals: StateFlow<List<SignalResponse>> = _signals

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchTowers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _towers.value = RetrofitClient.getInstance().getTowers()
                _signals.value = RetrofitClient.getInstance().getSignals()
            } catch (e: Exception) {
                _error.value = "Failed to load towers: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}