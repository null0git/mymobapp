package com.jsonquizzz.domain.model

import java.util.UUID

data class Question(
    val id: String = UUID.randomUUID().toString(),
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val text: String = "",
    val options: List<QuestionOption> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val explanation: String? = null,
    val hint: String? = null,
    val imageUrl: String? = null,
    val points: Int = 1,
    val tags: List<String> = emptyList(),
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val tolerance: Double? = null,
    val matchingPairs: List<MatchingPair> = emptyList(),
    val blanks: List<BlankSlot> = emptyList(),
    val codeSnippet: CodeSnippet? = null,
    val mathExpression: String? = null,
    val chemicalData: ChemicalData? = null,
    val graphData: GraphData? = null
)

data class QuestionOption(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "",
    val imageUrl: String? = null,
    val isCorrect: Boolean = false
)

data class MatchingPair(
    val id: String = UUID.randomUUID().toString(),
    val left: String = "",
    val right: String = ""
)

data class BlankSlot(
    val id: String = UUID.randomUUID().toString(),
    val position: Int = 0,
    val acceptedAnswers: List<String> = emptyList(),
    val caseSensitive: Boolean = false
)

data class CodeSnippet(
    val code: String = "",
    val language: String = "kotlin",
    val highlightLines: List<Int> = emptyList()
)

data class ChemicalData(
    val equation: String? = null,
    val smiles: String? = null,
    val formulaType: String = "equation"
)

data class GraphData(
    val functions: List<GraphFunction> = emptyList(),
    val xRange: Pair<Double, Double> = -10.0 to 10.0,
    val yRange: Pair<Double, Double> = -10.0 to 10.0,
    val gridEnabled: Boolean = true
)

data class GraphFunction(
    val expression: String = "",
    val color: String = "#2196F3",
    val label: String = ""
)

enum class Difficulty {
    EASY, MEDIUM, HARD, EXPERT;

    companion object {
        fun fromString(value: String): Difficulty {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}
