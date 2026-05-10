package com.jsonquizzz.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
sealed class Question {
    abstract val id: String
    abstract val question: String
    abstract val points: Int
    abstract val hint: String?
    abstract val explanation: String?

    @Serializable
    @SerialName("multiple_choice_single")
    data class MultipleChoiceSingle(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val options: List<String> = emptyList(),
        @SerialName("correctAnswer")
        val correctAnswer: Int = 0,
    ) : Question()

    @Serializable
    @SerialName("multiple_choice_multiple")
    data class MultipleChoiceMultiple(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val options: List<String> = emptyList(),
        @SerialName("correctAnswers")
        val correctAnswers: List<Int> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("multiple_choice_image")
    data class MultipleChoiceImage(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val options: List<ImageOption> = emptyList(),
        @SerialName("correctAnswer")
        val correctAnswer: Int = 0,
    ) : Question()

    @Serializable
    @SerialName("true_false")
    data class TrueFalse(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        @SerialName("correctAnswer")
        val correctAnswer: Boolean = true,
    ) : Question()

    @Serializable
    @SerialName("numeric")
    data class Numeric(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        @SerialName("correctAnswer")
        val correctAnswer: Double = 0.0,
        val tolerance: Double = 0.0,
    ) : Question()

    @Serializable
    @SerialName("short_answer")
    data class ShortAnswer(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        @SerialName("correctAnswer")
        val correctAnswer: String = "",
        @SerialName("caseSensitive")
        val caseSensitive: Boolean = false,
        @SerialName("acceptableAnswers")
        val acceptableAnswers: List<String> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("matching")
    data class Matching(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val pairs: List<MatchPair> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("fill_in_blank")
    data class FillInBlank(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val text: String = "",
        val blanks: List<BlankAnswer> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("word_bank")
    data class WordBank(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val text: String = "",
        val words: List<String> = emptyList(),
        @SerialName("correctPlacements")
        val correctPlacements: Map<String, String> = emptyMap(),
    ) : Question()

    @Serializable
    @SerialName("ordering")
    data class Ordering(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val items: List<String> = emptyList(),
        @SerialName("correctOrder")
        val correctOrder: List<Int> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("error_identification")
    data class ErrorIdentification(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val text: String = "",
        @SerialName("errorIndices")
        val errorIndices: List<Int> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("reading_comprehension")
    data class ReadingComprehension(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val passage: String = "",
        @SerialName("subQuestions")
        val subQuestions: List<SubQuestion> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("highlight_word")
    data class HighlightWord(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val text: String = "",
        @SerialName("correctHighlights")
        val correctHighlights: List<String> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("audio")
    data class Audio(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        @SerialName("audioUrl")
        val audioUrl: String = "",
        val options: List<String> = emptyList(),
        @SerialName("correctAnswer")
        val correctAnswer: Int = 0,
    ) : Question()

    @Serializable
    @SerialName("picture")
    data class Picture(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        @SerialName("imageUrl")
        val imageUrl: String = "",
        val options: List<String> = emptyList(),
        @SerialName("correctAnswer")
        val correctAnswer: Int = 0,
    ) : Question()

    @Serializable
    @SerialName("video")
    data class Video(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        @SerialName("videoUrl")
        val videoUrl: String = "",
        val options: List<String> = emptyList(),
        @SerialName("correctAnswer")
        val correctAnswer: Int = 0,
    ) : Question()

    @Serializable
    @SerialName("crossword")
    data class Crossword(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val grid: List<List<CrosswordCell>> = emptyList(),
        val clues: CrosswordClues = CrosswordClues(),
    ) : Question()

    @Serializable
    @SerialName("multi_part")
    data class MultiPart(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val parts: List<SubQuestion> = emptyList(),
    ) : Question()

    @Serializable
    @SerialName("graph")
    data class Graph(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val functions: List<GraphFunction> = emptyList(),
        @SerialName("xMin")
        val xMin: Double = -10.0,
        @SerialName("xMax")
        val xMax: Double = 10.0,
        @SerialName("yMin")
        val yMin: Double = -10.0,
        @SerialName("yMax")
        val yMax: Double = 10.0,
        val options: List<String> = emptyList(),
        @SerialName("correctAnswer")
        val correctAnswer: Int = 0,
    ) : Question()

    @Serializable
    @SerialName("unknown")
    data class RawJson(
        override val id: String = "",
        override val question: String = "",
        override val points: Int = 1,
        override val hint: String? = null,
        override val explanation: String? = null,
        val rawData: JsonObject? = null,
    ) : Question()
}

@Serializable
data class ImageOption(
    val label: String = "",
    @SerialName("imageUrl")
    val imageUrl: String = "",
)

@Serializable
data class MatchPair(
    val left: String = "",
    val right: String = "",
)

@Serializable
data class BlankAnswer(
    val index: Int = 0,
    val answer: String = "",
    @SerialName("acceptableAnswers")
    val acceptableAnswers: List<String> = emptyList(),
)

@Serializable
data class SubQuestion(
    val id: String = "",
    val question: String = "",
    val type: String = "multiple_choice_single",
    val options: List<String> = emptyList(),
    @SerialName("correctAnswer")
    val correctAnswer: JsonElement? = null,
    val points: Int = 1,
)

@Serializable
data class CrosswordCell(
    val letter: String? = null,
    @SerialName("isBlocked")
    val isBlocked: Boolean = false,
    val number: Int? = null,
)

@Serializable
data class CrosswordClues(
    val across: List<CrosswordClue> = emptyList(),
    val down: List<CrosswordClue> = emptyList(),
)

@Serializable
data class CrosswordClue(
    val number: Int = 0,
    val clue: String = "",
    val answer: String = "",
)

@Serializable
data class GraphFunction(
    val expression: String = "",
    val color: String? = null,
    val label: String? = null,
)
