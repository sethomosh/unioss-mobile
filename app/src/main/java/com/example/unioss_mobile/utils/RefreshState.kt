package com.example.unioss_mobile.utils

import androidx.compose.runtime.*
import kotlinx.coroutines.delay

@Composable
fun useAutoRefresh(intervalMs: Long = 10_000L, onRefresh: () -> Unit) {
    LaunchedEffect(Unit) {
        while (true) {
            delay(intervalMs)
            onRefresh()
        }
    }
}