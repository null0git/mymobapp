package com.jsonquizzz.data.local.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jsonquizzz.data.local.entity.*
import com.jsonquizzz.domain.model.*
import com.jsonquizzz.engine.QuizResult as EngineQuizResult

object EntityConverters {
    private val gson = Gson()

    // Quiz conversions
    fun QuizEntity.toDomainQuiz(): Quiz {
        val sectionsType = object : TypeToken<List<QuizSection>>() {}.type
        val tagsType = object : TypeToken<List<String>>() {}.type
        return Quiz(
            id = id,
            title = title,
            description = description,
            author = author,
            category = QuizCategory.fromString(category),
            difficulty = Difficulty.fromString(difficulty),
            sections = gson.fromJson(sectionsJson, sectionsType) ?: emptyList(),
            tags = gson.fromJson(tags, tagsType) ?: emptyList(),
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version,
            isPublic = isPublic,
            timeLimit = timeLimit,
            shuffleQuestions = shuffleQuestions,
            shuffleOptions = shuffleOptions,
            imageUrl = imageUrl
        )
    }

    fun Quiz.toEntity(): QuizEntity {
        return QuizEntity(
            id = id,
            title = title,
            description = description,
            author = author,
            category = category.name,
            difficulty = difficulty.name,
            tags = gson.toJson(tags),
            createdAt = createdAt,
            updatedAt = updatedAt,
            version = version,
            isPublic = isPublic,
            timeLimit = timeLimit,
            shuffleQuestions = shuffleQuestions,
            shuffleOptions = shuffleOptions,
            imageUrl = imageUrl,
            sectionsJson = gson.toJson(sections)
        )
    }

    // Attempt conversions
    fun QuizAttemptEntity.toDomainAttempt(): QuizAttempt {
        val answersType = object : TypeToken<Map<String, UserAnswer>>() {}.type
        return QuizAttempt(
            id = id,
            quizId = quizId,
            quizTitle = quizTitle,
            mode = QuizMode.valueOf(mode),
            startedAt = startedAt,
            completedAt = completedAt,
            answers = gson.fromJson(answersJson, answersType) ?: emptyMap(),
            currentQuestionIndex = currentQuestionIndex,
            isCompleted = isCompleted
        )
    }

    fun QuizAttempt.toEntity(): QuizAttemptEntity {
        return QuizAttemptEntity(
            id = id,
            quizId = quizId,
            quizTitle = quizTitle,
            mode = mode.name,
            startedAt = startedAt,
            completedAt = completedAt,
            answersJson = gson.toJson(answers),
            currentQuestionIndex = currentQuestionIndex,
            isCompleted = isCompleted
        )
    }

    // Bookmark conversions
    fun BookmarkEntity.toDomainBookmark(): Bookmark {
        return Bookmark(
            id = id,
            questionId = questionId,
            quizId = quizId,
            quizTitle = quizTitle,
            questionText = questionText,
            createdAt = createdAt,
            note = note
        )
    }

    fun Bookmark.toEntity(): BookmarkEntity {
        return BookmarkEntity(
            id = id,
            questionId = questionId,
            quizId = quizId,
            quizTitle = quizTitle,
            questionText = questionText,
            createdAt = createdAt,
            note = note
        )
    }

    // Result conversions
    fun EngineQuizResult.toEntity(): QuizResultEntity {
        return QuizResultEntity(
            attemptId = attemptId,
            quizId = quizId,
            quizTitle = quizTitle,
            mode = mode.name,
            totalQuestions = totalQuestions,
            correctCount = correctCount,
            incorrectCount = incorrectCount,
            unansweredCount = unansweredCount,
            earnedPoints = earnedPoints,
            totalPoints = totalPoints,
            percentage = percentage,
            duration = duration,
            completedAt = completedAt,
            resultsJson = gson.toJson(this)
        )
    }

    fun QuizResultEntity.toDomainResult(): EngineQuizResult {
        return gson.fromJson(resultsJson, EngineQuizResult::class.java)
    }
}
