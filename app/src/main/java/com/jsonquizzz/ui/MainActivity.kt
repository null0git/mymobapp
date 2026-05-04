package com.jsonquizzz.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jsonquizzz.JsonQuizzzApp
import com.jsonquizzz.domain.model.AppSettings
import com.jsonquizzz.ui.navigation.NavGraph
import com.jsonquizzz.ui.navigation.Screen
import com.jsonquizzz.ui.navigation.bottomNavItems
import com.jsonquizzz.ui.screens.settings.SettingsViewModel
import com.jsonquizzz.ui.theme.JsonQuizzzTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()

            JsonQuizzzTheme(
                darkTheme = settings.isDarkMode,
                dynamicColor = settings.dynamicColors,
                themeColor = settings.selectedThemeColor
            ) {
                MainApp(settings = settings)
            }
        }
    }
}

@Composable
private fun MainApp(settings: AppSettings) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if bottom bar should be shown
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.Library.route,
        Screen.QuizBuilder.route,
        Screen.Dashboard.route,
        Screen.Settings.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == item.route)
                                        item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController
        )
    }
}
