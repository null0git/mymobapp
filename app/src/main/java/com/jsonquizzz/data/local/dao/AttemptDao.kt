package com.jsonquizzz.data.local.dao

import androidx.room.*
import com.jsonquizzz.data.local.entity.QuizAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY startedAt DESC")
    fun getAllAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE id = :id")
    suspend fun getAttemptById(id: String): QuizAttemptEntity?

    @Query("SELECT * FROM quiz_attempts WHERE quizId = :quizId ORDER BY startedAt DESC")
    fun getAttemptsForQuiz(quizId: String): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE isCompleted = 0 ORDER BY startedAt DESC LIMIT 1")
    suspend fun getUnfinishedAttempt(): QuizAttemptEntity?

    @Query("SELECT * FROM quiz_attempts WHERE quizId = :quizId AND isCompleted = 0 ORDER BY startedAt DESC LIMIT 1")
    suspend fun getUnfinishedAttemptForQuiz(quizId: String): QuizAttemptEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity)

    @Update
    suspend fun updateAttempt(attempt: QuizAttemptEntity)

    @Delete
    suspend fun deleteAttempt(attempt: QuizAttemptEntity)

    @Query("SELECT * FROM quiz_attempts ORDER BY startedAt DESC LIMIT :limit")
    fun getRecentAttempts(limit: Int = 10): Flow<List<QuizAttemptEntity>>
}
