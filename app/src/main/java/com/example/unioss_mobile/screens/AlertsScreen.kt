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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unioss_mobile.data.model.AlertResponse
import com.example.unioss_mobile.ui.theme.*
import com.example.unioss_mobile.utils.useAutoRefresh
import com.example.unioss_mobile.viewmodel.AlertsViewModel

@Composable
fun AlertsScreen(viewModel: AlertsViewModel = viewModel(), navController: NavController? = null) {
    val alerts by viewModel.alerts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val tabs = listOf("All", "Critical", "Warning", "Cleared")
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) { viewModel.fetchAlerts() }
    useAutoRefresh(intervalMs = 10_000L) { viewModel.fetchAlerts() }

    val filteredAlerts = when (selectedTab) {
        1 -> alerts.filter { it.severity?.uppercase() == "CRITICAL" }
        2 -> alerts.filter { it.severity?.uppercase() == "WARNING" }
        3 -> alerts.filter { it.acknowledged }
        else -> alerts
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text("Alerts", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
        Text(
            text = "${alerts.count { !it.acknowledged }} active · ${alerts.count { it.acknowledged }} cleared",
            fontSize = 13.sp,
            color = TextSecondary
        )
        if (error == "DEMO_MODE") {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AccentOrange.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AccentOrange, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Demo Mode — connect a backend to see live data", fontSize = 12.sp, color = AccentOrange)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            tabs.forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) AccentCyan.copy(alpha = 0.15f) else CardDark)
                        .clickable { selectedTab = index }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        color = if (isSelected) AccentCyan else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            isLoading && alerts.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            }
            error != null && error != "DEMO_MODE" && alerts.isEmpty() -> {
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
                    if (filteredAlerts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No alerts found", color = TextSecondary, fontSize = 14.sp)
                        }
                    } else {
                        filteredAlerts.forEach { alert ->
                            LiveAlertCard(
                                alert = alert,
                                onClick = { navController?.navigate(Screen.AlertDetail.createRoute(alert.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiveAlertCard(alert: AlertResponse, onClick: () -> Unit = {}) {
    val severityColor = when (alert.severity?.uppercase()) {
        "CRITICAL" -> AccentRed
        "WARNING" -> AccentOrange
        else -> AccentCyan
    }
    val isCleared = alert.acknowledged

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(60.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isCleared) TextSecondary else severityColor)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(severityColor.copy(alpha = if (isCleared) 0.08f else 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = alert.severity?.uppercase() ?: "INFO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCleared) severityColor.copy(alpha = 0.5f) else severityColor
                    )
                }
                Text(text = alert.timestamp ?: "", fontSize = 11.sp, color = TextSecondary)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = alert.device_ip ?: "Unknown",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCleared) TextSecondary else TextPrimary
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = alert.message ?: "", fontSize = 12.sp, color = TextSecondary)
            if (isCleared) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGreen.copy(alpha = 0.6f), modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Resolved", fontSize = 11.sp, color = AccentGreen.copy(alpha = 0.6f))
                }
            }
        }
    }
}