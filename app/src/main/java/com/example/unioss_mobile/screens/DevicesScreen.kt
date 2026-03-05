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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unioss_mobile.ui.theme.*

data class Device(
    val name: String,
    val ip: String,
    val type: String,
    val status: String,
    val cpu: Int,
    val memory: Int,
    val uptime: String
)

val mockDevices = listOf(
    Device("Ubiquiti AirMax-Tower-01", "192.168.1.10", "Tower AP", "up", 23, 41, "12d 4h"),
    Device("Ubiquiti AirMax-Tower-02", "192.168.1.11", "Tower AP", "up", 31, 55, "12d 4h"),
    Device("MikroTik SXT-Tower-03", "192.168.1.12", "Tower AP", "down", 0, 0, "0d 0h"),
    Device("Cisco Aironet-Tower-04", "192.168.1.13", "Tower AP", "up", 18, 39, "9d 2h"),
    Device("Ubiquiti LiteBeam-01", "192.168.2.10", "Remote Client", "up", 12, 28, "5d 2h"),
    Device("Ubiquiti LiteBeam-02", "192.168.2.11", "Remote Client", "up", 18, 33, "5d 2h"),
    Device("Ubiquiti PowerBeam-01", "192.168.2.12", "Remote Client", "up", 9, 22, "3d 6h"),
    Device("Ubiquiti PowerBeam-02", "192.168.2.13", "Remote Client", "down", 0, 0, "0d 0h"),
    Device("Ubiquiti NanoBeam-01", "192.168.2.14", "Remote Client", "up", 15, 37, "1d 8h"),
    Device("Ubiquiti NanoBeam-02", "192.168.2.15", "Remote Client", "up", 21, 44, "8d 1h"),
    Device("MikroTik LHG-01", "192.168.2.16", "Remote Client", "up", 7, 19, "2d 3h"),
    Device("MikroTik LHG-02", "192.168.2.17", "Remote Client", "down", 0, 0, "0d 0h"),
    Device("Cisco Aironet-Client-01", "192.168.2.18", "Remote Client", "up", 11, 31, "4d 7h")
)

@Composable
fun DevicesScreen() {
    val searchQuery = remember { mutableStateOf("") }
    val filteredDevices = mockDevices.filter {
        it.name.contains(searchQuery.value, ignoreCase = true) ||
                it.ip.contains(searchQuery.value, ignoreCase = true) ||
                it.type.contains(searchQuery.value, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Devices",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AccentCyan
        )
        Text(
            text = "${mockDevices.count { it.status == "up" }} online · ${mockDevices.count { it.status == "down" }} offline",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
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

        // Device List
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            filteredDevices.forEach { device ->
                DeviceCard(device = device)
            }
        }
    }
}

@Composable
fun DeviceCard(device: Device) {
    val statusColor = if (device.status == "up") AccentGreen else AccentRed
    val statusLabel = if (device.status == "up") "UP" else "DOWN"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
            .padding(14.dp)
    ) {
        // Top row - name and status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (device.type == "Tower AP") Icons.Default.CellTower else Icons.Default.Router,
                    contentDescription = null,
                    tint = AccentCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = device.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = device.ip,
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

        if (device.status == "up") {
            Spacer(modifier = Modifier.height(12.dp))

            // Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricBar(
                    modifier = Modifier.weight(1f),
                    label = "CPU",
                    value = device.cpu,
                    color = when {
                        device.cpu > 80 -> AccentRed
                        device.cpu > 60 -> AccentOrange
                        else -> AccentGreen
                    }
                )
                MetricBar(
                    modifier = Modifier.weight(1f),
                    label = "Memory",
                    value = device.memory,
                    color = when {
                        device.memory > 80 -> AccentRed
                        device.memory > 60 -> AccentOrange
                        else -> AccentCyan
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Uptime
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Timer,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Uptime: ${device.uptime}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = device.type,
                    fontSize = 11.sp,
                    color = AccentCyan.copy(alpha = 0.7f)
                )
            }
        }
    }
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