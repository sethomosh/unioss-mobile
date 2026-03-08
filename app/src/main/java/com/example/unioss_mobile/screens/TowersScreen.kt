package com.example.unioss_mobile.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.navigation.NavController
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unioss_mobile.data.model.TowerResponse
import com.example.unioss_mobile.ui.theme.*
import com.example.unioss_mobile.utils.useAutoRefresh
import com.example.unioss_mobile.viewmodel.TowersViewModel
import com.example.unioss_mobile.data.model.DeviceResponse

@Composable
fun TowersScreen(viewModel: TowersViewModel = viewModel(), navController: NavController? = null) {
    val towers by viewModel.towers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val allDevices by viewModel.allDevices.collectAsState()

    LaunchedEffect(Unit) { viewModel.fetchTowers() }
    useAutoRefresh(intervalMs = 10_000L) { viewModel.fetchTowers() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text("Towers", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
        Text(
            text = "${towers.size} towers · ${towers.sumOf { it.devices.size }} total devices",
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

        when {
            isLoading && towers.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentCyan)
                }
            }
            error != null && error != "DEMO_MODE" && towers.isEmpty() -> {                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    towers.forEach { tower ->
                        LiveTowerCard(tower = tower, allDevices = allDevices)
                    }
                }
            }
        }
    }
}

@Composable
fun LiveTowerCard(tower: TowerResponse, allDevices: List<DeviceResponse>) {
    var expanded by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CellTower, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = tower.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(text = "${tower.devices.size} devices", fontSize = 11.sp, color = TextSecondary)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HorizontalDivider(color = SurfaceDark, thickness = 1.dp)
                Spacer(modifier = Modifier.height(4.dp))
                tower.devices.forEach { device ->
                    val deviceData = allDevices.find { it.ip == device.device_ip }
                    val isOnline = deviceData?.online ?: false
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SurfaceDark)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Router,
                            contentDescription = null,
                            tint = if (isOnline) AccentCyan else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = device.hostname ?: device.device_ip, fontSize = 13.sp, color = TextPrimary)
                            Text(text = device.device_ip, fontSize = 11.sp, color = TextSecondary)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isOnline) AccentGreen.copy(alpha = 0.15f) else AccentRed.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isOnline) "Online" else "Offline",
                                fontSize = 10.sp,
                                color = if (isOnline) AccentGreen else AccentRed,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}