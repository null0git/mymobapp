package com.jsonquizzz.domain.model

data class QuizState(
    val quiz: Quiz = Quiz(),
    val mode: QuizMode = QuizMode.PRACTICE,
    val currentSectionIndex: Int = 0,
    val currentQuestionIndex: Int = 0,
    val answers: Map<String, UserAnswer> = emptyMap(),
    val flaggedQuestions: Set<String> = emptySet(),
    val startTimeMs: Long = System.currentTimeMillis(),
    val remainingTimeMs: Long? = null,
    val isFinished: Boolean = false,
    val showingFeedback: Boolean = false,
    val shuffleSeed: Long = System.currentTimeMillis(),
) {
    val allQuestions: List<Question>
        get() = quiz.sections.flatMap { it.questions }

    val totalQuestions: Int
        get() = allQuestions.size

    val currentQuestion: Question?
        get() = allQuestions.getOrNull(flatQuestionIndex)

    val flatQuestionIndex: Int
        get() {
            var idx = 0
            for (i in 0 until currentSectionIndex) {
                idx += quiz.sections.getOrNull(i)?.questions?.size ?: 0
            }
            return idx + currentQuestionIndex
        }

    val progress: Float
        get() = if (totalQuestions == 0) 0f else (flatQuestionIndex + 1).toFloat() / totalQuestions

    val answeredCount: Int
        get() = answers.size

    val currentSection: Section?
        get() = quiz.sections.getOrNull(currentSectionIndex)
}

sealed class UserAnswer {
    data class SingleChoice(val index: Int) : UserAnswer()
    data class MultipleChoice(val indices: Set<Int>) : UserAnswer()
    data class TextInput(val text: String) : UserAnswer()
    data class NumericInput(val value: Double) : UserAnswer()
    data class BooleanInput(val value: Boolean) : UserAnswer()
    data class OrderingAnswer(val order: List<Int>) : UserAnswer()
    data class MatchingAnswer(val pairs: Map<Int, Int>) : UserAnswer()
    data class FillBlanksAnswer(val answers: Map<Int, String>) : UserAnswer()
    data class WordBankAnswer(val placements: Map<String, String>) : UserAnswer()
    data class HighlightAnswer(val words: Set<String>) : UserAnswer()
    data class ErrorIdAnswer(val indices: Set<Int>) : UserAnswer()
    data class CrosswordAnswer(val grid: Map<String, String>) : UserAnswer()
    data class SubQuestionsAnswer(val answers: Map<String, UserAnswer>) : UserAnswer()
}
