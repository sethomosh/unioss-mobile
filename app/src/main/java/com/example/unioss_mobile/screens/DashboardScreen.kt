package com.example.unioss_mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.unioss_mobile.navigation.Screen
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.navigation.NavController
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unioss_mobile.data.model.AlertResponse
import com.example.unioss_mobile.ui.theme.*
import com.example.unioss_mobile.utils.useAutoRefresh
import com.example.unioss_mobile.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel(), navController: NavController? = null) {    val devices by viewModel.devices.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchData() }
    useAutoRefresh(intervalMs = 10_000L) { viewModel.fetchData() }

    val onlineCount = devices.count { it.online }
    val offlineCount = devices.count { !it.online }
    val criticalAlerts = alerts.filter { it.severity.uppercase() == "CRITICAL" && !it.acknowledged }
    val warningAlerts = alerts.filter { it.severity.uppercase() == "WARNING" && !it.acknowledged }
    val totalInbound = devices.sumOf { d -> d.interfaces.sumOf { it.inbound_kbps } }
    val totalOutbound = devices.sumOf { d -> d.interfaces.sumOf { it.outbound_kbps } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("UniOSS", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                Text(
                    text = if (isLoading) "Refreshing..." else "Live data",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(AccentGreen.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(AccentGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Live", color = AccentGreen, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Device Health", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "Total", value = "${devices.size}", icon = Icons.Default.Devices, accentColor = AccentCyan, onClick = { navController?.navigate(Screen.Devices.route) })
            StatCard(modifier = Modifier.weight(1f), title = "Online", value = "$onlineCount", icon = Icons.Default.CheckCircle, accentColor = AccentGreen, onClick = { navController?.navigate(Screen.Devices.route) })
            StatCard(modifier = Modifier.weight(1f), title = "Offline", value = "$offlineCount", icon = Icons.Default.Cancel, accentColor = AccentRed, onClick = { navController?.navigate(Screen.Devices.route) })
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Bandwidth Usage", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "Inbound", value = "${"%.1f".format(totalInbound / 1000)} Mbps", icon = Icons.Default.ArrowDownward, accentColor = AccentCyan)
            StatCard(modifier = Modifier.weight(1f), title = "Outbound", value = "${"%.1f".format(totalOutbound / 1000)} Mbps", icon = Icons.Default.ArrowUpward, accentColor = AccentGreen)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Active Alerts", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(modifier = Modifier.weight(1f), title = "Critical", value = "${criticalAlerts.size}", icon = Icons.Default.Error, accentColor = AccentRed, onClick = { navController?.navigate(Screen.Alerts.route) })
            StatCard(modifier = Modifier.weight(1f), title = "Warnings", value = "${warningAlerts.size}", icon = Icons.Default.Warning, accentColor = AccentOrange, onClick = { navController?.navigate(Screen.Alerts.route) })
        }

        if (criticalAlerts.isNotEmpty() || warningAlerts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            (criticalAlerts.take(2) + warningAlerts.take(2)).forEach { alert ->
                AlertRow(
                    severity = alert.severity.uppercase(),
                    message = alert.message ?: "",
                    time = alert.timestamp ?: "",
                    color = if (alert.severity.uppercase() == "CRITICAL") AccentRed else AccentOrange,
                    onClick = { navController?.navigate(Screen.AlertDetail.createRoute(alert.id)) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Network Health", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(12.dp))
        StatusRow("SNMP Poller", "Running", AccentGreen)
        Spacer(modifier = Modifier.height(8.dp))
        StatusRow("Database", "Connected", AccentGreen)
        Spacer(modifier = Modifier.height(8.dp))
        StatusRow("API Backend", if (devices.isNotEmpty()) "Online" else "Checking...", if (devices.isNotEmpty()) AccentGreen else AccentOrange)
        Spacer(modifier = Modifier.height(8.dp))
        StatusRow("Signal Health", "Nominal", AccentCyan)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 11.sp, color = TextSecondary)
                Icon(imageVector = icon, contentDescription = title, tint = accentColor, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
fun AlertRow(severity: String, message: String, time: String, color: Color, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(color.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = severity, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = message, fontSize = 13.sp, color = TextPrimary)
        }
        Text(text = time, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.width(8.dp))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
    }
}
@Composable
fun StatusRow(service: String, status: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = service, fontSize = 14.sp, color = TextPrimary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(50)).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = status, fontSize = 13.sp, color = color)
        }
    }
}