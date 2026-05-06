package com.jsonquizzz.app.navigation

import kotlinx.serialization.Serializable

sealed interface NavRoute {
    @Serializable data object Home : NavRoute
    @Serializable data object Library : NavRoute
    @Serializable data object Create : NavRoute
    @Serializable data object Analytics : NavRoute
    @Serializable data object Profile : NavRoute

    @Serializable data object JsonInput : NavRoute
    @Serializable data object FileImport : NavRoute
    @Serializable data object PromptBuilder : NavRoute

    @Serializable data class QuizSetup(val quizId: String) : NavRoute
    @Serializable data class QuizPlayer(val quizId: String, val mode: String = "practice") : NavRoute
    @Serializable data object QuizResults : NavRoute
    @Serializable data object QuizReview : NavRoute

    @Serializable data class ShareQuiz(val quizId: String) : NavRoute
    @Serializable data class ShareReceive(val shareId: String) : NavRoute
    @Serializable data class Leaderboard(val quizId: String) : NavRoute

    @Serializable data object Settings : NavRoute
    @Serializable data object HowToUse : NavRoute
    @Serializable data object FAQ : NavRoute
}
