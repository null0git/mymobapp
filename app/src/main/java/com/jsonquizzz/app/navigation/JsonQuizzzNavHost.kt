package com.jsonquizzz.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jsonquizzz.app.ui.HomeScreen
import com.jsonquizzz.feature.analytics.AnalyticsScreen
import com.jsonquizzz.feature.auth.FAQScreen
import com.jsonquizzz.feature.auth.HowToUseScreen
import com.jsonquizzz.feature.auth.ProfileScreen
import com.jsonquizzz.feature.leaderboard.LeaderboardScreen
import com.jsonquizzz.feature.library.LibraryScreen
import com.jsonquizzz.feature.promptbuilder.PromptBuilderScreen
import com.jsonquizzz.feature.quizcreate.CreateScreen
import com.jsonquizzz.feature.quizcreate.FileImportScreen
import com.jsonquizzz.feature.quizcreate.JsonInputScreen
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
                onQuizSelected = { quizId ->
                    navController.navigate(NavRoute.QuizSetup(quizId))
                },
                onCreateQuiz = {
                    navController.navigate(NavRoute.Create)
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

        composable<NavRoute.JsonInput> {
            JsonInputScreen(
                onQuizCreated = { quizId ->
                    navController.navigate(NavRoute.QuizSetup(quizId)) {
                        popUpTo(NavRoute.Create)
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable<NavRoute.FileImport> {
            FileImportScreen(
                onQuizCreated = { quizId ->
                    navController.navigate(NavRoute.QuizSetup(quizId)) {
                        popUpTo(NavRoute.Create)
                    }
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable<NavRoute.Analytics> {
            AnalyticsScreen()
        }

        composable<NavRoute.Profile> {
            ProfileScreen(
                onNavigateToFAQ = {
                    navController.navigate(NavRoute.FAQ)
                },
                onNavigateToHowToUse = {
                    navController.navigate(NavRoute.HowToUse)
                },
            )
        }

        composable<NavRoute.FAQ> {
            FAQScreen(onBack = { navController.popBackStack() })
        }

        composable<NavRoute.HowToUse> {
            HowToUseScreen(onBack = { navController.popBackStack() })
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
                onStartQuiz = { id, mode ->
                    navController.navigate(NavRoute.QuizPlayer(id, mode))
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
                onGoHome = {
                    navController.navigate(NavRoute.Home) {
                        popUpTo(NavRoute.Home) { inclusive = true }
                    }
                },
                onTryAgain = { navController.popBackStack() },
            )
        }

        composable<NavRoute.ShareQuiz> { backStackEntry ->
            val route = backStackEntry.toRoute<NavRoute.ShareQuiz>()
            ShareQuizScreen(
                quizId = route.quizId,
                onBack = { navController.popBackStack() },
            )
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
