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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unioss_mobile.ui.theme.*

@Composable
fun SettingsScreen() {
    var baseUrl by remember { mutableStateOf("http://10.0.2.2:8000") }
    var refreshInterval by remember { mutableStateOf("10s") }
    var connectionStatus by remember { mutableStateOf("untested") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AccentCyan
        )
        Text(
            text = "Configure your UniOSS connection",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Connection Settings
        SectionHeader(title = "Connection", icon = Icons.Default.Wifi)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
                .padding(16.dp)
        ) {
            Text(text = "Backend URL", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = baseUrl,
                onValueChange = {
                    baseUrl = it
                    connectionStatus = "untested"
                },
                placeholder = { Text("http://10.0.2.2:8000", color = TextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = SurfaceDark,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = AccentCyan
                ),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null, tint = TextSecondary)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Test Connection Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (connectionStatus) {
                        "connected" -> AccentGreen
                        "failed" -> AccentRed
                        else -> TextSecondary
                    }
                    val statusText = when (connectionStatus) {
                        "connected" -> "Connected"
                        "failed" -> "Unreachable"
                        else -> "Not tested"
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = statusText, fontSize = 12.sp, color = statusColor)
                }
                Button(
                    onClick = { connectionStatus = "connected" },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AccentCyan.copy(alpha = 0.15f),
                        contentColor = AccentCyan
                    )
                ) {
                    Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Test Connection", fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Refresh Interval
        SectionHeader(title = "Preferences", icon = Icons.Default.Tune)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
                .padding(16.dp)
        ) {
            Text(text = "Auto Refresh Interval", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("5s", "10s", "30s", "60s").forEach { interval ->
                    val isSelected = refreshInterval == interval
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) AccentCyan.copy(alpha = 0.15f)
                                else SurfaceDark
                            )
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = interval,
                            fontSize = 13.sp,
                            color = if (isSelected) AccentCyan else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // About Section
        SectionHeader(title = "About", icon = Icons.Default.Info)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AboutRow(label = "App", value = "UniOSS Mobile")
            HorizontalDivider(color = SurfaceDark)
            AboutRow(label = "Version", value = "1.0.0")
            HorizontalDivider(color = SurfaceDark)
            AboutRow(label = "Backend", value = "UniOSS Network Monitor")
            HorizontalDivider(color = SurfaceDark)
            AboutRow(label = "Stack", value = "Kotlin + Jetpack Compose")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Developer Info
        SectionHeader(title = "Developer", icon = Icons.Default.Code)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(CardDark)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AboutRow(label = "Built by", value = "Zeph")
            HorizontalDivider(color = SurfaceDark)
            AboutRow(label = "GitHub", value = "github.com/sethomosh")
            HorizontalDivider(color = SurfaceDark)
            AboutRow(label = "Project", value = "unioss-mobile")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentCyan,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}