package com.example.unioss_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.unioss_mobile.screens.AlertDetailScreen
import com.example.unioss_mobile.screens.AlertsScreen
import com.example.unioss_mobile.screens.DashboardScreen
import com.example.unioss_mobile.screens.DeviceDetailScreen
import com.example.unioss_mobile.screens.DevicesScreen
import com.example.unioss_mobile.screens.LoginScreen
import com.example.unioss_mobile.screens.SettingsScreen
import com.example.unioss_mobile.screens.TowersScreen
import com.example.unioss_mobile.utils.SessionManager

sealed class Screen(val route: String, val label: String) {
    object Login : Screen("login", "Login")
    object Dashboard : Screen("dashboard", "Dashboard")
    object Devices : Screen("devices", "Devices")
    object Towers : Screen("towers", "Towers")
    object Alerts : Screen("alerts", "Alerts")
    object Settings : Screen("settings", "Settings")
    object DeviceDetail : Screen("device_detail/{deviceIp}", "Device Detail") {
        fun createRoute(deviceIp: String) = "device_detail/$deviceIp"
    }
    object AlertDetail : Screen("alert_detail/{alertId}", "Alert Detail") {
        fun createRoute(alertId: Int) = "alert_detail/$alertId"
    }
}

@Composable
fun UniossNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val role by SessionManager.getRole(context).collectAsState(initial = SessionManager.ROLE_NONE)

    val startDestination = if (role == SessionManager.ROLE_NONE) {
        Screen.Login.route
    } else {
        Screen.Dashboard.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Dashboard.route) { DashboardScreen(navController = navController) }
        composable(Screen.Devices.route) { DevicesScreen(navController = navController) }
        composable(Screen.Towers.route) { TowersScreen(navController = navController) }
        composable(Screen.Alerts.route) { AlertsScreen(navController = navController) }
        composable(Screen.Settings.route) { SettingsScreen(navController = navController) }
        composable(Screen.DeviceDetail.route) { backStackEntry ->
            val deviceIp = backStackEntry.arguments?.getString("deviceIp") ?: ""
            DeviceDetailScreen(deviceIp = deviceIp, navController = navController)
        }
        composable(Screen.AlertDetail.route) { backStackEntry ->
            val alertId = backStackEntry.arguments?.getString("alertId")?.toIntOrNull() ?: 0
            AlertDetailScreen(alertId = alertId, navController = navController)
        }
    }
}