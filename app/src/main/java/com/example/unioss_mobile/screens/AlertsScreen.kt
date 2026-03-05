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
import com.example.unioss_mobile.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.unioss_mobile.utils.useAutoRefresh



data class AlertItem(
    val id: Int,
    val device: String,
    val message: String,
    val severity: String,
    val status: String,
    val time: String
)

val mockAlerts = listOf(
    AlertItem(1, "AirMax-Tower-01", "RSSI dropped below -85 dBm on LiteBeam-02", "CRITICAL", "active", "2 min ago"),
    AlertItem(2, "SXT-Tower-03", "Tower device unreachable - connection lost", "CRITICAL", "active", "18 min ago"),
    AlertItem(3, "AirMax-Tower-02", "Signal degradation on MikroTik LHG-01", "WARNING", "active", "45 min ago"),
    AlertItem(4, "LiteBeam-01", "High memory usage at 87%", "WARNING", "active", "1 hr ago"),
    AlertItem(5, "Aironet-Tower-04", "CPU spike detected at 91%", "WARNING", "active", "2 hr ago"),
    AlertItem(6, "PowerBeam-01", "Signal restored - RSSI nominal", "INFO", "cleared", "3 hr ago"),
    AlertItem(7, "MikroTik LHG-02", "Device back online after outage", "INFO", "cleared", "5 hr ago"),
    AlertItem(8, "NanoBeam-01", "Intermittent packet loss detected", "WARNING", "cleared", "8 hr ago")
)

@Composable
fun AlertsScreen() {
    val tabs = listOf("All", "Critical", "Warning", "Cleared")
    var selectedTab by remember { mutableStateOf(0) }

    val filteredAlerts = when (selectedTab) {
        1 -> mockAlerts.filter { it.severity == "CRITICAL" }
        2 -> mockAlerts.filter { it.severity == "WARNING" }
        3 -> mockAlerts.filter { it.status == "cleared" }
        else -> mockAlerts
    }

    var lastRefreshed by remember { mutableStateOf("Just now") }
    var refreshCount by remember { mutableStateOf(0) }

    useAutoRefresh(intervalMs = 10_000L) {
        refreshCount++
        lastRefreshed = "Updated ${refreshCount * 10}s ago"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Alerts",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AccentCyan
        )
        Text(
            text = "${mockAlerts.count { it.status == "active" }} active · ${mockAlerts.count { it.status == "cleared" }} cleared · $lastRefreshed",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) AccentCyan.copy(alpha = 0.15f)
                            else CardDark
                        )
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

        // Alerts List
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
                    AlertCard(alert = alert)
                }
            }
        }
    }
}

@Composable
fun AlertCard(alert: AlertItem) {
    val severityColor = when (alert.severity) {
        "CRITICAL" -> AccentRed
        "WARNING" -> AccentOrange
        else -> AccentCyan
    }
    val isCleared = alert.status == "cleared"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Severity indicator bar
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
                        text = alert.severity,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCleared) severityColor.copy(alpha = 0.5f) else severityColor
                    )
                }
                Text(
                    text = alert.time,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = alert.device,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isCleared) TextSecondary else TextPrimary
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = alert.message,
                fontSize = 12.sp,
                color = TextSecondary
            )

            if (isCleared) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AccentGreen.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Resolved",
                        fontSize = 11.sp,
                        color = AccentGreen.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}