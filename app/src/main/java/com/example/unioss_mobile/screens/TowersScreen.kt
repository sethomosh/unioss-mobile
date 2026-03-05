package com.example.unioss_mobile.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unioss_mobile.ui.theme.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.unioss_mobile.utils.useAutoRefresh

data class ClientDevice(
    val name: String,
    val ip: String,
    val status: String,
    val rssi: Int,
    val signalPercent: Int
)

data class TowerDevice(
    val name: String,
    val ip: String,
    val brand: String,
    val status: String,
    val clients: List<ClientDevice>
)

val mockTowers = listOf(
    TowerDevice(
        name = "AirMax-Tower-01",
        ip = "192.168.1.10",
        brand = "Ubiquiti",
        status = "up",
        clients = listOf(
            ClientDevice("LiteBeam-01", "192.168.2.10", "up", -68, 82),
            ClientDevice("PowerBeam-01", "192.168.2.12", "up", -72, 71),
            ClientDevice("NanoBeam-01", "192.168.2.14", "up", -79, 58),
            ClientDevice("LiteBeam-02", "192.168.2.11", "down", -95, 12)
        )
    ),
    TowerDevice(
        name = "AirMax-Tower-02",
        ip = "192.168.1.11",
        brand = "Ubiquiti",
        status = "up",
        clients = listOf(
            ClientDevice("NanoBeam-02", "192.168.2.15", "up", -65, 88),
            ClientDevice("PowerBeam-02", "192.168.2.13", "up", -70, 75),
            ClientDevice("MikroTik LHG-01", "192.168.2.16", "up", -83, 52)
        )
    ),
    TowerDevice(
        name = "SXT-Tower-03",
        ip = "192.168.1.12",
        brand = "MikroTik",
        status = "down",
        clients = listOf(
            ClientDevice("MikroTik LHG-02", "192.168.2.17", "down", -98, 5),
            ClientDevice("Cisco Aironet-Client-01", "192.168.2.18", "down", -99, 3)
        )
    ),
    TowerDevice(
        name = "Aironet-Tower-04",
        ip = "192.168.1.13",
        brand = "Cisco",
        status = "up",
        clients = listOf(
            ClientDevice("Cisco Aironet-Client-02", "192.168.2.19", "up", -71, 74),
            ClientDevice("Cisco Aironet-Client-03", "192.168.2.20", "up", -66, 86)
        )
    )
)

@Composable
fun TowersScreen() {
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
            text = "Towers",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AccentCyan
        )
        Text(
            text = "${mockTowers.count { it.status == "up" }} active · ${mockTowers.sumOf { it.clients.size }} total clients · $lastRefreshed",
            fontSize = 13.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            mockTowers.forEach { tower ->
                TowerCard(tower = tower)
            }
        }
    }
}

@Composable
fun TowerCard(tower: TowerDevice) {
    var expanded by remember { mutableStateOf(tower.status == "up") }
    val statusColor = if (tower.status == "up") AccentGreen else AccentRed
    val onlineClients = tower.clients.count { it.status == "up" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
    ) {
        // Tower Header - tappable
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CellTower,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = tower.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${tower.brand} · ${tower.ip}",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tower.status.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$onlineClients/${tower.clients.size} clients",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Expandable client list
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(color = SurfaceDark, thickness = 1.dp)
                Spacer(modifier = Modifier.height(4.dp))
                tower.clients.forEach { client ->
                    ClientRow(client = client)
                }
            }
        }
    }
}

@Composable
fun ClientRow(client: ClientDevice) {
    val statusColor = if (client.status == "up") AccentGreen else AccentRed
    val signalColor = when {
        client.signalPercent >= 70 -> AccentGreen
        client.signalPercent >= 40 -> AccentOrange
        else -> AccentRed
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceDark)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Router,
            contentDescription = null,
            tint = statusColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = client.name, fontSize = 13.sp, color = TextPrimary)
            Text(text = client.ip, fontSize = 11.sp, color = TextSecondary)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${client.rssi} dBm",
                fontSize = 12.sp,
                color = signalColor,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${client.signalPercent}%",
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}