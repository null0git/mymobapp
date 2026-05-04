package com.jsonquizzz.domain.model

import java.util.UUID

data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val displayName: String = "Guest",
    val email: String? = null,
    val isGuest: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val stats: UserStats = UserStats()
)

data class UserStats(
    val totalQuizzesTaken: Int = 0,
    val totalQuestionsAnswered: Int = 0,
    val totalCorrectAnswers: Int = 0,
    val averageScore: Double = 0.0,
    val streakDays: Int = 0,
    val lastActiveAt: Long = System.currentTimeMillis(),
    val topicPerformance: Map<String, TopicScore> = emptyMap()
)

data class TopicScore(
    val topic: String = "",
    val attempted: Int = 0,
    val correct: Int = 0,
    val averagePercentage: Double = 0.0
)

data class Bookmark(
    val id: String = UUID.randomUUID().toString(),
    val questionId: String = "",
    val quizId: String = "",
    val quizTitle: String = "",
    val questionText: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val note: String? = null
)
