package com.example.unioss_mobile.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.unioss_mobile.data.model.DeviceDetailsResponse
import com.example.unioss_mobile.data.model.DeviceResponse
import com.example.unioss_mobile.data.model.PerformanceHistory
import com.example.unioss_mobile.data.model.TrafficHistory
import com.example.unioss_mobile.data.network.RetrofitClient
import com.example.unioss_mobile.ui.theme.*
import com.example.unioss_mobile.utils.AppPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun DeviceDetailScreen(deviceIp: String, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var details by remember { mutableStateOf<DeviceDetailsResponse?>(null) }
    var deviceInfo by remember { mutableStateOf<DeviceResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(deviceIp) {
        scope.launch {
            try {
                val url = AppPreferences.getBackendUrl(context).first()
                RetrofitClient.baseUrl = if (url.endsWith("/")) url else "$url/"
                details = RetrofitClient.getInstance().getDeviceDetails(deviceIp)
                val allDevices = RetrofitClient.getInstance().getDevices()
                deviceInfo = allDevices.find { it.device_ip == deviceIp }
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = AccentCyan)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = deviceInfo?.hostname ?: deviceIp,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(text = deviceIp, fontSize = 12.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.weight(1f))
            val online = deviceInfo?.online == true
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background((if (online) AccentGreen else AccentRed).copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (online) "ONLINE" else "OFFLINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (online) AccentGreen else AccentRed
                )
            }
        }

        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WifiOff, contentDescription = null, tint = AccentRed, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Failed to load device", color = AccentRed, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(error ?: "", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
            else -> {
                val snapshot = details?.snapshot
                val perfHistory = details?.performance_history ?: emptyList()
                val trafficHistory = details?.traffic_history ?: emptyList()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Device Info Card
                    SectionCard(title = "Device Info", icon = Icons.Default.Info) {
                        InfoRow("IP Address", deviceIp)
                        InfoRow("Hostname", deviceInfo?.hostname ?: "—")
                        InfoRow("Vendor", deviceInfo?.vendor ?: "—")
                        InfoRow("OS Version", deviceInfo?.os_version ?: "—")
                        InfoRow("Description", deviceInfo?.description ?: "—")
                        InfoRow("Status", deviceInfo?.status?.uppercase() ?: "—")
                        InfoRow("Uptime", formatUptime(snapshot?.uptime_seconds ?: 0.0))
                    }

                    // Current Performance
                    SectionCard(title = "Performance", icon = Icons.Default.Speed) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GaugeCard(
                                modifier = Modifier.weight(1f),
                                label = "CPU",
                                value = snapshot?.cpu_pct?.toInt() ?: 0,
                                color = when {
                                    (snapshot?.cpu_pct ?: 0.0) > 80 -> AccentRed
                                    (snapshot?.cpu_pct ?: 0.0) > 60 -> AccentOrange
                                    else -> AccentGreen
                                }
                            )
                            GaugeCard(
                                modifier = Modifier.weight(1f),
                                label = "Memory",
                                value = snapshot?.memory_pct?.toInt() ?: 0,
                                color = when {
                                    (snapshot?.memory_pct ?: 0.0) > 80 -> AccentRed
                                    (snapshot?.memory_pct ?: 0.0) > 60 -> AccentOrange
                                    else -> AccentCyan
                                }
                            )
                        }
                    }

                    // Signal Info
                    details?.signal?.let { signal ->
                        SectionCard(title = "Signal", icon = Icons.Default.SignalCellularAlt) {
                            InfoRow("RSSI", "${signal.rssi_dbm} dBm")
                            InfoRow("Signal Quality", "${signal.rssi_pct?.toInt()}%")
                            InfoRow("SNR", "${signal.snr_db} dB")
                            InfoRow("Last Updated", signal.timestamp ?: "—")
                            Spacer(modifier = Modifier.height(8.dp))
                            val signalPct = signal.rssi_pct?.toFloat() ?: 0f
                            val signalColor = when {
                                signalPct >= 70 -> AccentGreen
                                signalPct >= 40 -> AccentOrange
                                else -> AccentRed
                            }
                            Text(text = "Signal Strength", fontSize = 11.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(SurfaceDark)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(signalPct / 100f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(50))
                                        .background(signalColor)
                                )
                            }
                        }
                    }

                    // Interfaces
                    if (!deviceInfo?.interfaces.isNullOrEmpty()) {
                        SectionCard(title = "Interfaces", icon = Icons.Default.SettingsEthernet) {
                            deviceInfo?.interfaces?.forEach { iface ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceDark)
                                        .padding(12.dp)
                                ) {
                                    Text(text = iface.interface_name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AccentCyan)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        MiniStat(modifier = Modifier.weight(1f), label = "↓ Inbound", value = "${"%.1f".format(iface.inbound_kbps)} kbps", color = AccentCyan)
                                        MiniStat(modifier = Modifier.weight(1f), label = "↑ Outbound", value = "${"%.1f".format(iface.outbound_kbps)} kbps", color = AccentGreen)
                                        MiniStat(modifier = Modifier.weight(1f), label = "Errors", value = "${iface.errors}", color = if (iface.errors > 0) AccentOrange else TextSecondary)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    // CPU/Memory History Graph
                    if (perfHistory.isNotEmpty()) {
                        SectionCard(title = "CPU & Memory History", icon = Icons.Default.Timeline) {
                            HistoryChart(
                                data1 = perfHistory.reversed().map { it.cpu_pct?.toFloat() ?: 0f },
                                data2 = perfHistory.reversed().map { it.memory_pct?.toFloat() ?: 0f },
                                timestamps = perfHistory.reversed().map { formatChartTime(it.timestamp) },
                                label1 = "CPU",
                                label2 = "Memory",
                                color1 = AccentGreen,
                                color2 = AccentCyan,
                                yAxisLabel = "%",
                                maxY = 100f
                            )
                        }
                    }

                    // Traffic History Graph
                    if (trafficHistory.isNotEmpty()) {
                        SectionCard(title = "Traffic History", icon = Icons.Default.BarChart) {
                            HistoryChart(
                                data1 = trafficHistory.reversed().map { it.inbound_kbps?.toFloat() ?: 0f },
                                data2 = trafficHistory.reversed().map { it.outbound_kbps?.toFloat() ?: 0f },
                                timestamps = trafficHistory.reversed().map { formatChartTime(it.timestamp) },
                                label1 = "Inbound",
                                label2 = "Outbound",
                                color1 = AccentCyan,
                                color2 = AccentGreen,
                                yAxisLabel = "kbps",
                                maxY = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

fun formatChartTime(timestamp: String?): String {
    if (timestamp == null) return ""
    return try {
        // Extract HH:mm from ISO timestamp
        val timePart = timestamp.substringAfter("T").take(5)
        timePart
    } catch (e: Exception) {
        ""
    }
}

@Composable
fun HistoryChart(
    data1: List<Float>,
    data2: List<Float>,
    timestamps: List<String>,
    label1: String,
    label2: String,
    color1: Color,
    color2: Color,
    yAxisLabel: String,
    maxY: Float?
) {
    val effectiveMax = maxY ?: (data1 + data2).maxOrNull()?.coerceAtLeast(1f) ?: 1f
    val displayCount = minOf(data1.size, data2.size, timestamps.size, 20)
    val slice1 = data1.takeLast(displayCount)
    val slice2 = data2.takeLast(displayCount)
    val sliceTs = timestamps.takeLast(displayCount)

    Column {
        // Legend
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(color1))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label1, fontSize = 11.sp, color = TextSecondary)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(2.dp)).background(color2))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = label2, fontSize = 11.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = yAxisLabel, fontSize = 11.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Y axis labels + chart area
        Row(modifier = Modifier.fillMaxWidth()) {
            // Y axis
            Column(
                modifier = Modifier
                    .width(36.dp)
                    .height(120.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${"%.0f".format(effectiveMax)}", fontSize = 9.sp, color = TextSecondary)
                Text(text = "${"%.0f".format(effectiveMax / 2)}", fontSize = 9.sp, color = TextSecondary)
                Text(text = "0", fontSize = 9.sp, color = TextSecondary)
            }

            // Chart bars
            Box(modifier = Modifier.weight(1f).height(120.dp)) {
                // Grid lines
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    repeat(3) {
                        HorizontalDivider(color = SurfaceDark.copy(alpha = 0.5f), thickness = 0.5.dp)
                    }
                }

                // Bars
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    slice1.zip(slice2).forEach { (v1, v2) ->
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // data2 bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight((v2 / effectiveMax).coerceIn(0f, 1f))
                                    .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                                    .background(color2.copy(alpha = 0.5f))
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // data1 bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight((v1 / effectiveMax).coerceIn(0f, 1f))
                                    .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                                    .background(color1.copy(alpha = 0.8f))
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // X axis timestamps — show first, middle, last
        Row(modifier = Modifier.fillMaxWidth().padding(start = 36.dp)) {
            Text(text = sliceTs.firstOrNull() ?: "", fontSize = 9.sp, color = TextSecondary)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = sliceTs.getOrNull(sliceTs.size / 2) ?: "", fontSize = 9.sp, color = TextSecondary)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = sliceTs.lastOrNull() ?: "", fontSize = 9.sp, color = TextSecondary)
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
    HorizontalDivider(color = SurfaceDark.copy(alpha = 0.5f), thickness = 0.5.dp)
}

@Composable
fun GaugeCard(modifier: Modifier = Modifier, label: String, value: Int, color: Color) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$value%", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = color)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(BackgroundDark)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value / 100f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
    }
}

@Composable
fun MiniStat(modifier: Modifier = Modifier, label: String, value: String, color: Color) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
    }
}