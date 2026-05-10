package com.jsonquizzz.domain.model

import kotlin.math.abs

object ScoringEngine {

    fun scoreQuestion(question: Question, answer: UserAnswer?): QuestionScore {
        if (answer == null) return QuestionScore(0, question.points, false)

        return when (question) {
            is Question.MultipleChoiceSingle -> scoreMcqSingle(question, answer)
            is Question.MultipleChoiceMultiple -> scoreMcqMultiple(question, answer)
            is Question.MultipleChoiceImage -> scoreMcqImage(question, answer)
            is Question.TrueFalse -> scoreTrueFalse(question, answer)
            is Question.Numeric -> scoreNumeric(question, answer)
            is Question.ShortAnswer -> scoreShortAnswer(question, answer)
            is Question.Matching -> scoreMatching(question, answer)
            is Question.FillInBlank -> scoreFillInBlank(question, answer)
            is Question.WordBank -> scoreWordBank(question, answer)
            is Question.Ordering -> scoreOrdering(question, answer)
            is Question.ErrorIdentification -> scoreErrorId(question, answer)
            is Question.HighlightWord -> scoreHighlight(question, answer)
            is Question.Audio -> scoreAudio(question, answer)
            is Question.Picture -> scorePicture(question, answer)
            is Question.Video -> scoreVideo(question, answer)
            is Question.Crossword -> scoreCrossword(question, answer)
            is Question.Graph -> scoreGraph(question, answer)
            is Question.ReadingComprehension -> scoreReadingComp(question, answer)
            is Question.MultiPart -> scoreMultiPart(question, answer)
            is Question.RawJson -> QuestionScore(0, question.points, false)
        }
    }

    fun scoreQuiz(quiz: Quiz, answers: Map<String, UserAnswer>): QuizResult {
        val sectionResults = quiz.sections.map { section ->
            val qScores = section.questions.map { q ->
                scoreQuestion(q, answers[q.id])
            }
            SectionResult(
                sectionTitle = section.title,
                totalQuestions = section.questions.size,
                correctAnswers = qScores.count { it.isCorrect },
                totalPoints = qScores.sumOf { it.maxPoints },
                earnedPoints = qScores.sumOf { it.earnedPoints },
            )
        }

        val totalQuestions = sectionResults.sumOf { it.totalQuestions }
        val correctAnswers = sectionResults.sumOf { it.correctAnswers }
        val totalPoints = sectionResults.sumOf { it.totalPoints }
        val earnedPoints = sectionResults.sumOf { it.earnedPoints }
        val percentage = if (totalPoints > 0) (earnedPoints.toDouble() / totalPoints) * 100 else 0.0

        return QuizResult(
            quizTitle = quiz.title,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            totalPoints = totalPoints,
            earnedPoints = earnedPoints,
            percentage = percentage,
            sectionResults = sectionResults,
            passed = percentage >= quiz.settings.passingScore,
        )
    }

    private fun scoreMcqSingle(q: Question.MultipleChoiceSingle, a: UserAnswer): QuestionScore {
        val selected = (a as? UserAnswer.SingleChoice)?.index ?: return QuestionScore(0, q.points, false)
        val correct = selected == q.correctAnswer
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreMcqMultiple(q: Question.MultipleChoiceMultiple, a: UserAnswer): QuestionScore {
        val selected = (a as? UserAnswer.MultipleChoice)?.indices ?: return QuestionScore(0, q.points, false)
        val correctSet = q.correctAnswers.toSet()
        val correct = selected == correctSet
        val partial = if (!correct && selected.isNotEmpty()) {
            val hits = selected.intersect(correctSet).size
            val misses = selected.subtract(correctSet).size
            val score = ((hits - misses).coerceAtLeast(0).toDouble() / correctSet.size * q.points).toInt()
            score
        } else if (correct) q.points else 0
        return QuestionScore(partial, q.points, correct)
    }

    private fun scoreMcqImage(q: Question.MultipleChoiceImage, a: UserAnswer): QuestionScore {
        val selected = (a as? UserAnswer.SingleChoice)?.index ?: return QuestionScore(0, q.points, false)
        val correct = selected == q.correctAnswer
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreTrueFalse(q: Question.TrueFalse, a: UserAnswer): QuestionScore {
        val selected = (a as? UserAnswer.BooleanInput)?.value ?: return QuestionScore(0, q.points, false)
        val correct = selected == q.correctAnswer
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreNumeric(q: Question.Numeric, a: UserAnswer): QuestionScore {
        val value = (a as? UserAnswer.NumericInput)?.value ?: return QuestionScore(0, q.points, false)
        val correct = abs(value - q.correctAnswer) <= q.tolerance
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreShortAnswer(q: Question.ShortAnswer, a: UserAnswer): QuestionScore {
        val text = (a as? UserAnswer.TextInput)?.text?.trim() ?: return QuestionScore(0, q.points, false)
        val allAcceptable = listOf(q.correctAnswer) + q.acceptableAnswers
        val correct = allAcceptable.any { acceptable ->
            if (q.caseSensitive) text == acceptable.trim()
            else text.equals(acceptable.trim(), ignoreCase = true)
        }
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreMatching(q: Question.Matching, a: UserAnswer): QuestionScore {
        val pairs = (a as? UserAnswer.MatchingAnswer)?.pairs ?: return QuestionScore(0, q.points, false)
        val correctCount = pairs.count { (left, right) -> left == right }
        val total = q.pairs.size
        val allCorrect = correctCount == total
        val earned = if (total > 0) (correctCount.toDouble() / total * q.points).toInt() else 0
        return QuestionScore(earned, q.points, allCorrect)
    }

    private fun scoreFillInBlank(q: Question.FillInBlank, a: UserAnswer): QuestionScore {
        val answers = (a as? UserAnswer.FillBlanksAnswer)?.answers ?: return QuestionScore(0, q.points, false)
        val correctCount = q.blanks.count { blank ->
            val userText = answers[blank.index]?.trim() ?: ""
            val acceptable = listOf(blank.answer) + blank.acceptableAnswers
            acceptable.any { it.trim().equals(userText, ignoreCase = true) }
        }
        val total = q.blanks.size
        val earned = if (total > 0) (correctCount.toDouble() / total * q.points).toInt() else 0
        return QuestionScore(earned, q.points, correctCount == total)
    }

    private fun scoreWordBank(q: Question.WordBank, a: UserAnswer): QuestionScore {
        val placements = (a as? UserAnswer.WordBankAnswer)?.placements ?: return QuestionScore(0, q.points, false)
        val correctCount = q.correctPlacements.count { (slot, word) ->
            placements[slot]?.equals(word, ignoreCase = true) == true
        }
        val total = q.correctPlacements.size
        val earned = if (total > 0) (correctCount.toDouble() / total * q.points).toInt() else 0
        return QuestionScore(earned, q.points, correctCount == total)
    }

    private fun scoreOrdering(q: Question.Ordering, a: UserAnswer): QuestionScore {
        val order = (a as? UserAnswer.OrderingAnswer)?.order ?: return QuestionScore(0, q.points, false)
        val correct = order == q.correctOrder
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreErrorId(q: Question.ErrorIdentification, a: UserAnswer): QuestionScore {
        val indices = (a as? UserAnswer.ErrorIdAnswer)?.indices ?: return QuestionScore(0, q.points, false)
        val correctSet = q.errorIndices.toSet()
        val correct = indices == correctSet
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreHighlight(q: Question.HighlightWord, a: UserAnswer): QuestionScore {
        val words = (a as? UserAnswer.HighlightAnswer)?.words ?: return QuestionScore(0, q.points, false)
        val correctSet = q.correctHighlights.map { it.lowercase() }.toSet()
        val selectedSet = words.map { it.lowercase() }.toSet()
        val correct = selectedSet == correctSet
        val hits = selectedSet.intersect(correctSet).size
        val total = correctSet.size
        val earned = if (total > 0) (hits.toDouble() / total * q.points).toInt() else 0
        return QuestionScore(earned, q.points, correct)
    }

    private fun scoreAudio(q: Question.Audio, a: UserAnswer): QuestionScore {
        val selected = (a as? UserAnswer.SingleChoice)?.index ?: return QuestionScore(0, q.points, false)
        val correct = selected == q.correctAnswer
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scorePicture(q: Question.Picture, a: UserAnswer): QuestionScore {
        val selected = (a as? UserAnswer.SingleChoice)?.index ?: return QuestionScore(0, q.points, false)
        val correct = selected == q.correctAnswer
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreVideo(q: Question.Video, a: UserAnswer): QuestionScore {
        val selected = (a as? UserAnswer.SingleChoice)?.index ?: return QuestionScore(0, q.points, false)
        val correct = selected == q.correctAnswer
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreCrossword(q: Question.Crossword, a: UserAnswer): QuestionScore {
        val grid = (a as? UserAnswer.CrosswordAnswer)?.grid ?: return QuestionScore(0, q.points, false)
        val allClues = q.clues.across + q.clues.down
        val correctCount = allClues.count { clue ->
            clue.answer.uppercase() == (grid[clue.number.toString()] ?: "").uppercase()
        }
        val total = allClues.size
        val earned = if (total > 0) (correctCount.toDouble() / total * q.points).toInt() else 0
        return QuestionScore(earned, q.points, correctCount == total)
    }

    private fun scoreGraph(q: Question.Graph, a: UserAnswer): QuestionScore {
        val selected = (a as? UserAnswer.SingleChoice)?.index ?: return QuestionScore(0, q.points, false)
        val correct = selected == q.correctAnswer
        return QuestionScore(if (correct) q.points else 0, q.points, correct)
    }

    private fun scoreReadingComp(q: Question.ReadingComprehension, a: UserAnswer): QuestionScore {
        val subAnswers = (a as? UserAnswer.SubQuestionsAnswer)?.answers ?: return QuestionScore(0, q.points, false)
        val correctCount = q.subQuestions.count { sub ->
            val subAnswer = subAnswers[sub.id]
            when (subAnswer) {
                is UserAnswer.SingleChoice -> {
                    val correctIdx = sub.correctAnswer?.toString()?.toIntOrNull()
                    subAnswer.index == correctIdx
                }
                is UserAnswer.TextInput -> {
                    sub.correctAnswer?.toString()?.equals(subAnswer.text.trim(), ignoreCase = true) == true
                }
                else -> false
            }
        }
        val total = q.subQuestions.size
        val earned = if (total > 0) (correctCount.toDouble() / total * q.points).toInt() else 0
        return QuestionScore(earned, q.points, correctCount == total)
    }

    private fun scoreMultiPart(q: Question.MultiPart, a: UserAnswer): QuestionScore {
        val subAnswers = (a as? UserAnswer.SubQuestionsAnswer)?.answers ?: return QuestionScore(0, q.points, false)
        val correctCount = q.parts.count { part ->
            val partAnswer = subAnswers[part.id]
            when (partAnswer) {
                is UserAnswer.SingleChoice -> {
                    val correctIdx = part.correctAnswer?.toString()?.toIntOrNull()
                    partAnswer.index == correctIdx
                }
                is UserAnswer.TextInput -> {
                    part.correctAnswer?.toString()?.equals(partAnswer.text.trim(), ignoreCase = true) == true
                }
                else -> false
            }
        }
        val total = q.parts.size
        val earned = if (total > 0) (correctCount.toDouble() / total * q.points).toInt() else 0
        return QuestionScore(earned, q.points, correctCount == total)
    }
}

data class QuestionScore(
    val earnedPoints: Int,
    val maxPoints: Int,
    val isCorrect: Boolean,
)
