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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.unioss_mobile.data.model.AlertResponse
import com.example.unioss_mobile.data.network.RetrofitClient
import com.example.unioss_mobile.navigation.Screen
import com.example.unioss_mobile.ui.theme.*
import com.example.unioss_mobile.utils.AppPreferences
import com.example.unioss_mobile.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AlertDetailScreen(alertId: Int, navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val role by SessionManager.getRole(context).collectAsState(initial = SessionManager.ROLE_NONE)
    val isAdmin = role == SessionManager.ROLE_ADMIN

    var alert by remember { mutableStateOf<AlertResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isAcknowledging by remember { mutableStateOf(false) }
    var acknowledgeSuccess by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(alertId) {
        scope.launch {
            try {
                val url = AppPreferences.getBackendUrl(context).first()
                RetrofitClient.baseUrl = if (url.endsWith("/")) url else "$url/"
                val alerts = RetrofitClient.getInstance().getAlerts(limit = 100)
                alert = alerts.find { it.id == alertId }
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
            Text("Alert Detail", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.weight(1f))
            // Role badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background((if (isAdmin) AccentCyan else AccentGreen).copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                        contentDescription = null,
                        tint = if (isAdmin) AccentCyan else AccentGreen,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAdmin) "Admin" else "Guest",
                        fontSize = 10.sp,
                        color = if (isAdmin) AccentCyan else AccentGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
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
                        Text("Failed to load alert", color = AccentRed, fontSize = 16.sp)
                    }
                }
            }
            alert == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Alert not found", color = TextSecondary, fontSize = 16.sp)
                }
            }
            else -> {
                val a = alert!!
                val severityColor = when (a.severity?.uppercase()) {
                    "CRITICAL" -> AccentRed
                    "WARNING" -> AccentOrange
                    else -> AccentCyan
                }
                val isAcknowledged = a.acknowledged || acknowledgeSuccess

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Severity Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(severityColor.copy(alpha = 0.1f))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when (a.severity?.uppercase()) {
                                    "CRITICAL" -> Icons.Default.Error
                                    "WARNING" -> Icons.Default.Warning
                                    else -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = severityColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = a.severity?.uppercase() ?: "INFO",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = severityColor
                                )
                                Text(
                                    text = if (isAcknowledged) "Resolved" else "Active",
                                    fontSize = 13.sp,
                                    color = if (isAcknowledged) AccentGreen else severityColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Alert Info
                    SectionCard(title = "Alert Info", icon = Icons.Default.Info) {
                        InfoRow("Alert ID", "#${a.id}")
                        InfoRow("Device", a.device_ip ?: "—")
                        InfoRow("Category", a.category ?: "—")
                        InfoRow("Severity", a.severity?.uppercase() ?: "INFO")
                        InfoRow("Time", a.timestamp ?: "—")
                        InfoRow("Status", if (isAcknowledged) "Acknowledged" else "Active")
                    }

                    // Message
                    SectionCard(title = "Message", icon = Icons.Default.Message) {
                        Text(
                            text = a.message ?: "No message",
                            fontSize = 14.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }

                    // Acknowledge Action
                    if (!isAcknowledged) {
                        Button(
                            onClick = {
                                if (isAdmin) {
                                    scope.launch {
                                        isAcknowledging = true
                                        try {
                                            val url = AppPreferences.getBackendUrl(context).first()
                                            RetrofitClient.baseUrl = if (url.endsWith("/")) url else "$url/"
                                            RetrofitClient.getInstance().acknowledgeAlert(a.id)
                                            acknowledgeSuccess = true
                                        } catch (e: Exception) {
                                            error = "Failed to acknowledge: ${e.message}"
                                        } finally {
                                            isAcknowledging = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdmin) AccentGreen.copy(alpha = 0.15f) else SurfaceDark,
                                contentColor = if (isAdmin) AccentGreen else TextSecondary
                            ),
                            enabled = isAdmin && !isAcknowledging
                        ) {
                            if (isAcknowledging) {
                                CircularProgressIndicator(color = AccentGreen, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(
                                    imageVector = if (isAdmin) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isAcknowledging -> "Acknowledging..."
                                    !isAdmin -> "Admin access required"
                                    else -> "Acknowledge Alert"
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AccentGreen.copy(alpha = 0.1f))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Alert Acknowledged", color = AccentGreen, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Navigate to device
                    a.device_ip?.let { ip ->
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.DeviceDetail.createRoute(ip)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentCyan),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(AccentCyan.copy(alpha = 0.5f))
                            )
                        ) {
                            Icon(Icons.Default.Router, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View Device → $ip", fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}