package com.example.unioss_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.unioss_mobile.navigation.Screen
import com.example.unioss_mobile.navigation.UniossNavGraph
import com.example.unioss_mobile.ui.theme.UniossTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniossTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface
                        ) {
                            val items = listOf(
                                Triple(Screen.Dashboard, Icons.Default.Home, "Dashboard"),
                                Triple(Screen.Devices, Icons.Default.Devices, "Devices"),
                                Triple(Screen.Towers, Icons.Default.CellTower, "Towers"),
                                Triple(Screen.Alerts, Icons.Default.Notifications, "Alerts"),
                                Triple(Screen.Settings, Icons.Default.Settings, "Settings")
                            )
                            items.forEach { (screen, icon, label) ->
                                NavigationBarItem(
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Dashboard.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                ) { padding ->
                    UniossNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(padding)
                    )
                }
            }
        }
    }
}