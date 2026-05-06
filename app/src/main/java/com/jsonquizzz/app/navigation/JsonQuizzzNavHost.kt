package com.jsonquizzz.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jsonquizzz.app.ui.HomeScreen
import com.jsonquizzz.feature.analytics.AnalyticsScreen
import com.jsonquizzz.feature.auth.ProfileScreen
import com.jsonquizzz.feature.leaderboard.LeaderboardScreen
import com.jsonquizzz.feature.library.LibraryScreen
import com.jsonquizzz.feature.promptbuilder.PromptBuilderScreen
import com.jsonquizzz.feature.quizcreate.CreateScreen
import com.jsonquizzz.feature.quizplayer.QuizPlayerScreen
import com.jsonquizzz.feature.quizplayer.QuizResultsScreen
import com.jsonquizzz.feature.quizplayer.QuizSetupScreen
import com.jsonquizzz.feature.sharing.ShareQuizScreen
import com.jsonquizzz.feature.sharing.ShareReceiveScreen

@Composable
fun JsonQuizzzNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = NavRoute.Home,
        modifier = modifier,
    ) {
        composable<NavRoute.Home> {
            HomeScreen(
                onNavigateToQuiz = { quizId ->
                    navController.navigate(NavRoute.QuizSetup(quizId))
                },
            )
        }

        composable<NavRoute.Library> {
            LibraryScreen(
                onNavigateToCreate = {
                    navController.navigate(NavRoute.Create)
                },
                onNavigateToQuiz = { quizId ->
                    navController.navigate(NavRoute.QuizSetup(quizId))
                },
            )
        }

        composable<NavRoute.Create> {
            CreateScreen(
                onNavigateToJsonInput = {
                    navController.navigate(NavRoute.JsonInput)
                },
                onNavigateToFileImport = {
                    navController.navigate(NavRoute.FileImport)
                },
                onNavigateToPromptBuilder = {
                    navController.navigate(NavRoute.PromptBuilder)
                },
            )
        }

        composable<NavRoute.Analytics> {
            AnalyticsScreen()
        }

        composable<NavRoute.Profile> {
            ProfileScreen(
                onNavigateToSettings = {
                    navController.navigate(NavRoute.Settings)
                },
            )
        }

        composable<NavRoute.PromptBuilder> {
            PromptBuilderScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable<NavRoute.QuizSetup> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.QuizSetup>()
            QuizSetupScreen(
                quizId = route.quizId,
                onStartPractice = {
                    navController.navigate(NavRoute.QuizPlayer(route.quizId, "practice"))
                },
                onStartTest = {
                    navController.navigate(NavRoute.QuizPlayer(route.quizId, "test"))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable<NavRoute.QuizPlayer> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.QuizPlayer>()
            QuizPlayerScreen(
                quizId = route.quizId,
                mode = route.mode,
                onFinish = {
                    navController.navigate(NavRoute.QuizResults) {
                        popUpTo(NavRoute.Home)
                    }
                },
            )
        }

        composable<NavRoute.QuizResults> {
            QuizResultsScreen(
                onReview = { navController.navigate(NavRoute.QuizReview) },
                onRetry = { navController.popBackStack() },
                onHome = {
                    navController.navigate(NavRoute.Home) {
                        popUpTo(NavRoute.Home) { inclusive = true }
                    }
                },
            )
        }

        composable<NavRoute.ShareQuiz> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.ShareQuiz>()
            ShareQuizScreen(quizId = route.quizId)
        }

        composable<NavRoute.ShareReceive> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.ShareReceive>()
            ShareReceiveScreen(shareId = route.shareId)
        }

        composable<NavRoute.Leaderboard> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.Leaderboard>()
            LeaderboardScreen(quizId = route.quizId)
        }
    }
}
