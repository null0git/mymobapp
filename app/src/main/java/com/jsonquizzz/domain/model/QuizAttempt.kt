package com.jsonquizzz.domain.model

import java.util.UUID

data class QuizAttempt(
    val id: String = UUID.randomUUID().toString(),
    val quizId: String = "",
    val quizTitle: String = "",
    val mode: QuizMode = QuizMode.PRACTICE,
    val startedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val answers: Map<String, UserAnswer> = emptyMap(),
    val currentQuestionIndex: Int = 0,
    val isCompleted: Boolean = false
) {
    val score: Int
        get() = answers.values.count { it.isCorrect }

    val totalAnswered: Int
        get() = answers.size

    val percentage: Double
        get() = if (answers.isEmpty()) 0.0
        else (score.toDouble() / answers.size) * 100.0

    val earnedPoints: Int
        get() = answers.values.filter { it.isCorrect }.sumOf { it.points }

    val totalPoints: Int
        get() = answers.values.sumOf { it.points }

    val duration: Long
        get() = (completedAt ?: System.currentTimeMillis()) - startedAt
}

data class UserAnswer(
    val questionId: String = "",
    val selectedOptions: List<String> = emptyList(),
    val textInput: String? = null,
    val numericInput: Double? = null,
    val matchingSelections: Map<String, String> = emptyMap(),
    val blankInputs: Map<String, String> = emptyMap(),
    val isCorrect: Boolean = false,
    val points: Int = 0,
    val answeredAt: Long = System.currentTimeMillis()
)
