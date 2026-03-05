package com.example.unioss_mobile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.unioss_mobile.screens.AlertsScreen
import com.example.unioss_mobile.screens.DashboardScreen
import com.example.unioss_mobile.screens.SignalsScreen
import com.example.unioss_mobile.screens.TowersScreen
import com.example.unioss_mobile.screens.TrafficScreen

sealed class Screen(val route: String, val label: String) {
    object Dashboard : Screen("dashboard", "Dashboard")
    object Traffic : Screen("traffic", "Traffic")
    object Signals : Screen("signals", "Signals")
    object Towers : Screen("towers", "Towers")
    object Alerts : Screen("alerts", "Alerts")
}

@Composable
fun UniossNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) { DashboardScreen() }
        composable(Screen.Traffic.route) { TrafficScreen() }
        composable(Screen.Signals.route) { SignalsScreen() }
        composable(Screen.Towers.route) { TowersScreen() }
        composable(Screen.Alerts.route) { AlertsScreen() }
    }
}