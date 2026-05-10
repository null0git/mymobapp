package com.jsonquizzz.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val title: String = "",
    val description: String = "",
    val sections: List<Section> = emptyList(),
    val settings: QuizSettings = QuizSettings(),
    val theme: QuizTheme = QuizTheme(),
    val metadata: QuizMetadata = QuizMetadata(),
)

@Serializable
data class Section(
    val title: String = "",
    val description: String = "",
    val questions: List<Question> = emptyList(),
)

@Serializable
data class QuizSettings(
    @SerialName("shuffleQuestions")
    val shuffleQuestions: Boolean = false,
    @SerialName("shuffleOptions")
    val shuffleOptions: Boolean = false,
    @SerialName("showFeedback")
    val showFeedback: Boolean = true,
    @SerialName("allowSkip")
    val allowSkip: Boolean = true,
    @SerialName("allowReview")
    val allowReview: Boolean = true,
    @SerialName("timeLimit")
    val timeLimit: Int? = null,
    @SerialName("passingScore")
    val passingScore: Int = 70,
    @SerialName("adaptiveDifficulty")
    val adaptiveDifficulty: Boolean = false,
    @SerialName("confidenceTracking")
    val confidenceTracking: Boolean = false,
)

@Serializable
data class QuizTheme(
    @SerialName("primaryColor")
    val primaryColor: String? = null,
    val font: String? = null,
    @SerialName("darkMode")
    val darkMode: Boolean? = null,
)

@Serializable
data class QuizMetadata(
    val author: String = "",
    val version: String = "1.0",
    val tags: List<String> = emptyList(),
    val difficulty: String = "medium",
    @SerialName("createdAt")
    val createdAt: String? = null,
)
