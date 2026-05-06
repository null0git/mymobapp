package com.jsonquizzz.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val jsonContent: String,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPlayedAt: Long? = null,
    val playCount: Int = 0,
    val isFavorite: Boolean = false,
    val source: String = "local",
    val shareId: String? = null,
)

@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val quizId: String,
    val quizTitle: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val totalPoints: Int,
    val earnedPoints: Int,
    val percentage: Double,
    val timeTakenSeconds: Long,
    val mode: String,
    val completedAt: Long = System.currentTimeMillis(),
    val resultJson: String = "",
)
