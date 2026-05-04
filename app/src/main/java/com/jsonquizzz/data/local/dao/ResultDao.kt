package com.jsonquizzz.data.local.dao

import androidx.room.*
import com.jsonquizzz.data.local.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResultDao {
    @Query("SELECT * FROM quiz_results ORDER BY completedAt DESC")
    fun getAllResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results WHERE attemptId = :attemptId")
    suspend fun getResultByAttemptId(attemptId: String): QuizResultEntity?

    @Query("SELECT * FROM quiz_results WHERE quizId = :quizId ORDER BY completedAt DESC")
    fun getResultsForQuiz(quizId: String): Flow<List<QuizResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: QuizResultEntity)

    @Query("SELECT * FROM quiz_results ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentResults(limit: Int = 10): Flow<List<QuizResultEntity>>

    @Query("SELECT AVG(percentage) FROM quiz_results")
    suspend fun getAverageScore(): Double?

    @Query("SELECT COUNT(*) FROM quiz_results")
    suspend fun getResultCount(): Int
}
