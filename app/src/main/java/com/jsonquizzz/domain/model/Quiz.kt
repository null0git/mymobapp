package com.jsonquizzz.domain.model

import java.util.UUID

data class Quiz(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val category: QuizCategory = QuizCategory.GENERAL,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val sections: List<QuizSection> = emptyList(),
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val version: String = "1.0",
    val isPublic: Boolean = false,
    val timeLimit: Int? = null,
    val shuffleQuestions: Boolean = false,
    val shuffleOptions: Boolean = false,
    val imageUrl: String? = null
) {
    val totalQuestions: Int
        get() = sections.sumOf { it.questions.size }

    val totalPoints: Int
        get() = sections.sumOf { section ->
            section.questions.sumOf { it.points }
        }

    val allQuestions: List<Question>
        get() = sections.flatMap { it.questions }
}

data class QuizSection(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String? = null,
    val questions: List<Question> = emptyList(),
    val order: Int = 0
)

enum class QuizCategory(val displayName: String) {
    MATH("Mathematics"),
    PHYSICS("Physics"),
    CHEMISTRY("Chemistry"),
    BIOLOGY("Biology"),
    COMPUTER_SCIENCE("Computer Science"),
    PROGRAMMING("Programming"),
    ENGLISH("English"),
    HISTORY("History"),
    GEOGRAPHY("Geography"),
    GENERAL("General Knowledge"),
    SCIENCE("Science"),
    LANGUAGE("Languages"),
    ART("Art & Music"),
    CUSTOM("Custom");

    companion object {
        fun fromString(value: String): QuizCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: GENERAL
        }
    }
}
