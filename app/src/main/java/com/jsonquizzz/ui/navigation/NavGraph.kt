package com.jsonquizzz.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jsonquizzz.ui.screens.bookmarks.BookmarksScreen
import com.jsonquizzz.ui.screens.builder.QuizBuilderScreen
import com.jsonquizzz.ui.screens.dashboard.DashboardScreen
import com.jsonquizzz.ui.screens.home.HomeScreen
import com.jsonquizzz.ui.screens.library.LibraryScreen
import com.jsonquizzz.ui.screens.quiz.QuizPlayerScreen
import com.jsonquizzz.ui.screens.result.ResultScreen
import com.jsonquizzz.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToQuiz = { quizId, mode ->
                    navController.navigate(Screen.QuizPlayer.createRoute(quizId, mode))
                },
                onNavigateToLibrary = {
                    navController.navigate(Screen.Library.route)
                },
                onNavigateToBuilder = {
                    navController.navigate(Screen.QuizBuilder.createRoute())
                },
                onNavigateToBookmarks = {
                    navController.navigate(Screen.Bookmarks.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route)
                }
            )
        }

        composable(Screen.Library.route) {
            LibraryScreen(
                onNavigateToQuiz = { quizId, mode ->
                    navController.navigate(Screen.QuizPlayer.createRoute(quizId, mode))
                },
                onNavigateToBuilder = { quizId ->
                    navController.navigate(Screen.QuizBuilder.createRoute(quizId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.QuizPlayer.route,
            arguments = listOf(
                navArgument("quizId") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: return@composable
            val mode = backStackEntry.arguments?.getString("mode") ?: "PRACTICE"
            QuizPlayerScreen(
                quizId = quizId,
                mode = mode,
                onNavigateToResult = { attemptId ->
                    navController.navigate(Screen.QuizResult.createRoute(attemptId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.QuizResult.route,
            arguments = listOf(
                navArgument("attemptId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val attemptId = backStackEntry.arguments?.getString("attemptId") ?: return@composable
            ResultScreen(
                attemptId = attemptId,
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onRetryQuiz = { quizId, mode ->
                    navController.navigate(Screen.QuizPlayer.createRoute(quizId, mode)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(
            route = Screen.QuizBuilder.route,
            arguments = listOf(
                navArgument("quizId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId")
            QuizBuilderScreen(
                editQuizId = quizId,
                onNavigateBack = { navController.popBackStack() },
                onQuizSaved = { savedQuizId ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Bookmarks.route) {
            BookmarksScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToQuiz = { quizId, mode ->
                    navController.navigate(Screen.QuizPlayer.createRoute(quizId, mode))
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
