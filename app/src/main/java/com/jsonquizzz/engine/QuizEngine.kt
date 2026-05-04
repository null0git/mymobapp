package com.jsonquizzz.engine

import com.jsonquizzz.domain.model.*
import kotlin.math.abs

object QuizEngine {

    fun checkAnswer(question: Question, userAnswer: UserAnswer): Boolean {
        return when (question.type) {
            QuestionType.MULTIPLE_CHOICE,
            QuestionType.MULTIPLE_CHOICE_IMAGE -> checkMultipleChoice(question, userAnswer)
            QuestionType.TRUE_FALSE -> checkTrueFalse(question, userAnswer)
            QuestionType.NUMERIC_INPUT -> checkNumericInput(question, userAnswer)
            QuestionType.SHORT_ANSWER -> checkShortAnswer(question, userAnswer)
            QuestionType.MATCHING -> checkMatching(question, userAnswer)
            QuestionType.FILL_IN_THE_BLANK -> checkFillInTheBlank(question, userAnswer)
            QuestionType.GRAPH_BASED -> checkMultipleChoice(question, userAnswer)
            QuestionType.CODE_BASED -> checkShortAnswer(question, userAnswer)
            QuestionType.MATH_RENDERED -> checkMultipleChoice(question, userAnswer)
            QuestionType.CHEMICAL_EQUATION -> checkMultipleChoice(question, userAnswer)
        }
    }

    private fun checkMultipleChoice(question: Question, answer: UserAnswer): Boolean {
        val correctIds = question.options.filter { it.isCorrect }.map { it.id }.toSet()
        return answer.selectedOptions.toSet() == correctIds
    }

    private fun checkTrueFalse(question: Question, answer: UserAnswer): Boolean {
        val userSelection = answer.selectedOptions.firstOrNull() ?: answer.textInput ?: ""
        return question.correctAnswers.any { it.equals(userSelection, ignoreCase = true) }
    }

    private fun checkNumericInput(question: Question, answer: UserAnswer): Boolean {
        val userValue = answer.numericInput ?: return false
        val correctValue = question.correctAnswers.firstOrNull()?.toDoubleOrNull() ?: return false
        val tolerance = question.tolerance ?: 0.0
        return abs(userValue - correctValue) <= tolerance
    }

    private fun checkShortAnswer(question: Question, answer: UserAnswer): Boolean {
        val userText = answer.textInput?.trim() ?: return false
        return question.correctAnswers.any { it.trim().equals(userText, ignoreCase = true) }
    }

    private fun checkMatching(question: Question, answer: UserAnswer): Boolean {
        val correctPairs = question.matchingPairs.associate { it.left to it.right }
        return answer.matchingSelections == correctPairs
    }

    private fun checkFillInTheBlank(question: Question, answer: UserAnswer): Boolean {
        return question.blanks.all { blank ->
            val userInput = answer.blankInputs[blank.id] ?: return false
            blank.acceptedAnswers.any { accepted ->
                if (blank.caseSensitive) accepted.trim() == userInput.trim()
                else accepted.trim().equals(userInput.trim(), ignoreCase = true)
            }
        }
    }

    fun calculateResults(quiz: Quiz, attempt: QuizAttempt): QuizResult {
        val questionResults = quiz.allQuestions.map { question ->
            val answer = attempt.answers[question.id]
            val isCorrect = if (answer != null) checkAnswer(question, answer) else false
            QuestionResult(
                questionId = question.id,
                questionText = question.text,
                questionType = question.type,
                isCorrect = isCorrect,
                isAnswered = answer != null,
                userAnswer = answer,
                correctAnswers = question.correctAnswers,
                explanation = question.explanation,
                points = if (isCorrect) question.points else 0,
                maxPoints = question.points,
                tags = question.tags,
                sectionTitle = quiz.sections.find { s ->
                    s.questions.any { it.id == question.id }
                }?.title ?: ""
            )
        }

        val totalPoints = questionResults.sumOf { it.maxPoints }
        val earnedPoints = questionResults.sumOf { it.points }

        return QuizResult(
            attemptId = attempt.id,
            quizId = quiz.id,
            quizTitle = quiz.title,
            mode = attempt.mode,
            questionResults = questionResults,
            totalQuestions = questionResults.size,
            correctCount = questionResults.count { it.isCorrect },
            incorrectCount = questionResults.count { it.isAnswered && !it.isCorrect },
            unansweredCount = questionResults.count { !it.isAnswered },
            earnedPoints = earnedPoints,
            totalPoints = totalPoints,
            percentage = if (totalPoints > 0) (earnedPoints.toDouble() / totalPoints) * 100.0 else 0.0,
            duration = attempt.duration,
            completedAt = attempt.completedAt ?: System.currentTimeMillis(),
            weakTopics = detectWeakTopics(questionResults),
            sectionBreakdown = buildSectionBreakdown(questionResults)
        )
    }

    private fun detectWeakTopics(results: List<QuestionResult>): List<WeakTopic> {
        val topicMap = mutableMapOf<String, MutableList<QuestionResult>>()
        results.forEach { result ->
            result.tags.forEach { tag ->
                topicMap.getOrPut(tag) { mutableListOf() }.add(result)
            }
        }
        return topicMap.mapNotNull { (topic, topicResults) ->
            val correctRate = topicResults.count { it.isCorrect }.toDouble() / topicResults.size
            if (correctRate < 0.6) {
                WeakTopic(
                    topic = topic,
                    correctRate = correctRate,
                    totalQuestions = topicResults.size,
                    incorrectQuestionIds = topicResults.filter { !it.isCorrect }.map { it.questionId }
                )
            } else null
        }.sortedBy { it.correctRate }
    }

    private fun buildSectionBreakdown(results: List<QuestionResult>): List<SectionResult> {
        return results.groupBy { it.sectionTitle }.map { (title, sectionResults) ->
            SectionResult(
                sectionTitle = title,
                totalQuestions = sectionResults.size,
                correctCount = sectionResults.count { it.isCorrect },
                percentage = if (sectionResults.isNotEmpty())
                    (sectionResults.count { it.isCorrect }.toDouble() / sectionResults.size) * 100.0
                else 0.0
            )
        }
    }

    fun prepareQuiz(quiz: Quiz, settings: AppSettings): Quiz {
        var prepared = quiz
        if (settings.shuffleQuestions || quiz.shuffleQuestions) {
            prepared = prepared.copy(
                sections = prepared.sections.map { section ->
                    section.copy(questions = section.questions.shuffled())
                }
            )
        }
        if (settings.shuffleOptions || quiz.shuffleOptions) {
            prepared = prepared.copy(
                sections = prepared.sections.map { section ->
                    section.copy(
                        questions = section.questions.map { question ->
                            if (question.type == QuestionType.MULTIPLE_CHOICE ||
                                question.type == QuestionType.MULTIPLE_CHOICE_IMAGE
                            ) {
                                question.copy(options = question.options.shuffled())
                            } else question
                        }
                    )
                }
            )
        }
        return prepared
    }
}

data class QuizResult(
    val attemptId: String = "",
    val quizId: String = "",
    val quizTitle: String = "",
    val mode: QuizMode = QuizMode.PRACTICE,
    val questionResults: List<QuestionResult> = emptyList(),
    val totalQuestions: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val unansweredCount: Int = 0,
    val earnedPoints: Int = 0,
    val totalPoints: Int = 0,
    val percentage: Double = 0.0,
    val duration: Long = 0L,
    val completedAt: Long = 0L,
    val weakTopics: List<WeakTopic> = emptyList(),
    val sectionBreakdown: List<SectionResult> = emptyList()
)

data class QuestionResult(
    val questionId: String = "",
    val questionText: String = "",
    val questionType: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val isCorrect: Boolean = false,
    val isAnswered: Boolean = false,
    val userAnswer: UserAnswer? = null,
    val correctAnswers: List<String> = emptyList(),
    val explanation: String? = null,
    val points: Int = 0,
    val maxPoints: Int = 0,
    val tags: List<String> = emptyList(),
    val sectionTitle: String = ""
)

data class WeakTopic(
    val topic: String = "",
    val correctRate: Double = 0.0,
    val totalQuestions: Int = 0,
    val incorrectQuestionIds: List<String> = emptyList()
)

data class SectionResult(
    val sectionTitle: String = "",
    val totalQuestions: Int = 0,
    val correctCount: Int = 0,
    val percentage: Double = 0.0
)
