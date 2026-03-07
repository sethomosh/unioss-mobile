package com.example.unioss_mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.unioss_mobile.data.model.DeviceResponse
import com.example.unioss_mobile.navigation.Screen
import com.example.unioss_mobile.ui.theme.*
import com.example.unioss_mobile.utils.useAutoRefresh
import com.example.unioss_mobile.viewmodel.DevicesViewModel

@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel = viewModel(),
    navController: NavController? = null
) {
    val devices by viewModel.devices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.fetchDevices() }
    useAutoRefresh(intervalMs = 10_000L) { viewModel.fetchDevices() }

    val filteredDevices = devices.filter {
        (it.hostname?.contains(searchQuery.value, ignoreCase = true) == true) ||
                (it.device_ip?.contains(searchQuery.value, ignoreCase = true) == true) ||
                (it.vendor?.contains(searchQuery.value, ignoreCase = true) == true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text("Devices", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
        Text(
            text = "${devices.count { it.online }} online · ${devices.count { !it.online }} offline",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            placeholder = { Text("Search devices...", color = TextSecondary) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentCyan,
                unfocusedBorderColor = CardDark,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = AccentCyan
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading && devices.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            }
            error != null && devices.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, tint = AccentRed, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Backend unreachable", color = AccentRed, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Check Settings → Backend URL", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }
            else -> {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    filteredDevices.forEach { device ->
                        LiveDeviceCard(
                            device = device,
                            onClick = {
                                device.device_ip?.let { ip ->
                                    navController?.navigate(Screen.DeviceDetail.createRoute(ip))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiveDeviceCard(device: DeviceResponse, onClick: () -> Unit = {}) {
    val statusColor = if (device.online) AccentGreen else AccentRed
    val statusLabel = if (device.online) "UP" else "DOWN"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Router,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = device.hostname ?: "Unknown",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${device.vendor ?: "Unknown"} · ${device.device_ip ?: "N/A"}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
        }

        if (device.online) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBar(
                    modifier = Modifier.weight(1f),
                    label = "CPU",
                    value = device.cpu_pct.toInt(),
                    color = when {
                        device.cpu_pct > 80 -> AccentRed
                        device.cpu_pct > 60 -> AccentOrange
                        else -> AccentGreen
                    }
                )
                MetricBar(
                    modifier = Modifier.weight(1f),
                    label = "Memory",
                    value = device.memory_pct.toInt(),
                    color = when {
                        device.memory_pct > 80 -> AccentRed
                        device.memory_pct > 60 -> AccentOrange
                        else -> AccentCyan
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Uptime: ${formatUptime(device.uptime_seconds)}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                device.signal?.let {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "RSSI: ${it.rssi_dbm} dBm",
                        fontSize = 11.sp,
                        color = AccentCyan.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

fun formatUptime(seconds: Double): String {
    val totalSeconds = seconds.toLong()
    val days = totalSeconds / 86400
    val hours = (totalSeconds % 86400) / 3600
    return "${days}d ${hours}h"
}

@Composable
fun MetricBar(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    color: Color
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary)
            Text(text = "$value%", fontSize = 11.sp, color = color, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(50))
                .background(SurfaceDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value / 100f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}