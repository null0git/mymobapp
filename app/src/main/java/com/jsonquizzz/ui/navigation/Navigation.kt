package com.jsonquizzz.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Library : Screen("library")
    data object QuizDetail : Screen("quiz_detail/{quizId}") {
        fun createRoute(quizId: String) = "quiz_detail/$quizId"
    }
    data object QuizPlayer : Screen("quiz_player/{quizId}/{mode}") {
        fun createRoute(quizId: String, mode: String) = "quiz_player/$quizId/$mode"
    }
    data object QuizResult : Screen("quiz_result/{attemptId}") {
        fun createRoute(attemptId: String) = "quiz_result/$attemptId"
    }
    data object QuizBuilder : Screen("quiz_builder?quizId={quizId}") {
        fun createRoute(quizId: String? = null) =
            if (quizId != null) "quiz_builder?quizId=$quizId" else "quiz_builder"
    }
    data object Settings : Screen("settings")
    data object Bookmarks : Screen("bookmarks")
    data object Dashboard : Screen("dashboard")
}

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, Screen.Home.route),
    BottomNavItem("Library", Icons.Filled.LibraryBooks, Icons.Outlined.LibraryBooks, Screen.Library.route),
    BottomNavItem("Create", Icons.Filled.Add, Icons.Outlined.Add, Screen.QuizBuilder.route),
    BottomNavItem("Stats", Icons.Filled.BarChart, Icons.Outlined.BarChart, Screen.Dashboard.route),
    BottomNavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, Screen.Settings.route)
)
