package com.jsonquizzz.domain.model

data class QuizResult(
    val quizId: String = "",
    val quizTitle: String = "",
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val totalPoints: Int = 0,
    val earnedPoints: Int = 0,
    val percentage: Double = 0.0,
    val timeTakenSeconds: Long = 0,
    val sectionResults: List<SectionResult> = emptyList(),
    val passed: Boolean = false,
    val completedAt: Long = System.currentTimeMillis(),
)

data class SectionResult(
    val sectionTitle: String = "",
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val totalPoints: Int = 0,
    val earnedPoints: Int = 0,
)

data class QuestionAnswer(
    val questionId: String = "",
    val userAnswer: Any? = null,
    val isCorrect: Boolean = false,
    val pointsEarned: Int = 0,
    val timeTakenMs: Long = 0,
)

enum class QuizMode {
    PRACTICE,
    TEST,
}
