package com.example.unioss_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.unioss_mobile.screens.DashboardScreen
import com.example.unioss_mobile.screens.DevicesScreen
import com.example.unioss_mobile.screens.TowersScreen
import com.example.unioss_mobile.screens.AlertsScreen
import com.example.unioss_mobile.screens.SettingsScreen

sealed class Screen(val route: String, val label: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Devices : Screen("devices", "Devices")
    object Towers : Screen("towers", "Towers")
    object Alerts : Screen("alerts", "Alerts")
    object Settings : Screen("settings", "Settings")
}

@Composable
fun UniossNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) { DashboardScreen() }
        composable(Screen.Devices.route) { DevicesScreen() }
        composable(Screen.Towers.route) { TowersScreen() }
        composable(Screen.Alerts.route) { AlertsScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
    }
}