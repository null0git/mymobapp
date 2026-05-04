package com.jsonquizzz.data.local.entity

import androidx.room.*

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val author: String,
    val category: String,
    val difficulty: String,
    val tags: String, // JSON array
    val createdAt: Long,
    val updatedAt: Long,
    val version: String,
    val isPublic: Boolean,
    val timeLimit: Int?,
    val shuffleQuestions: Boolean,
    val shuffleOptions: Boolean,
    val imageUrl: String?,
    val sectionsJson: String // Full sections as JSON
)

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey val id: String,
    val quizId: String,
    val quizTitle: String,
    val mode: String,
    val startedAt: Long,
    val completedAt: Long?,
    val answersJson: String, // JSON map
    val currentQuestionIndex: Int,
    val isCompleted: Boolean
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String,
    val questionId: String,
    val quizId: String,
    val quizTitle: String,
    val questionText: String,
    val createdAt: Long,
    val note: String?
)

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey val attemptId: String,
    val quizId: String,
    val quizTitle: String,
    val mode: String,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val unansweredCount: Int,
    val earnedPoints: Int,
    val totalPoints: Int,
    val percentage: Double,
    val duration: Long,
    val completedAt: Long,
    val resultsJson: String // Full results as JSON
)
