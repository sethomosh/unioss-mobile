package com.example.unioss_mobile.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unioss_mobile.ui.theme.*

@Composable
fun DashboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "UniOSS",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentCyan
                )
                Text(
                    text = "Network Monitor",
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
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(AccentGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Live",
                        color = AccentGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Device Health
        Text(
            text = "Device Health",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Total Devices",
                value = "12",
                icon = Icons.Default.Devices,
                accentColor = AccentCyan
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Online",
                value = "9",
                icon = Icons.Default.CheckCircle,
                accentColor = AccentGreen
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Offline",
                value = "3",
                icon = Icons.Default.Cancel,
                accentColor = AccentRed
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bandwidth Summary
        Text(
            text = "Bandwidth Usage",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Inbound",
                value = "24 Mbps",
                icon = Icons.Default.ArrowDownward,
                accentColor = AccentCyan
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Outbound",
                value = "18 Mbps",
                icon = Icons.Default.ArrowUpward,
                accentColor = AccentGreen
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Alerts Summary
        Text(
            text = "Active Alerts",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Critical",
                value = "1",
                icon = Icons.Default.Error,
                accentColor = AccentRed
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Warnings",
                value = "3",
                icon = Icons.Default.Warning,
                accentColor = AccentOrange
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        AlertRow(
            severity = "CRITICAL",
            message = "RSRP below threshold on Tower-01",
            time = "2 min ago",
            color = AccentRed
        )
        Spacer(modifier = Modifier.height(8.dp))
        AlertRow(
            severity = "WARNING",
            message = "High traffic on eth0",
            time = "15 min ago",
            color = AccentOrange
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Network Health
        Text(
            text = "Network Health",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))
        StatusRow("SNMP Poller", "Running", AccentGreen)
        Spacer(modifier = Modifier.height(8.dp))
        StatusRow("Database", "Connected", AccentGreen)
        Spacer(modifier = Modifier.height(8.dp))
        StatusRow("API Backend", "Online", AccentGreen)
        Spacer(modifier = Modifier.height(8.dp))
        StatusRow("Signal Health", "Nominal", AccentCyan)
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardDark)
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 11.sp, color = TextSecondary)
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
fun AlertRow(severity: String, message: String, time: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardDark)
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
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = status, fontSize = 13.sp, color = color)
        }
    }
}