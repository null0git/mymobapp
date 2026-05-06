package com.jsonquizzz.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes ORDER BY lastPlayedAt DESC, createdAt DESC")
    fun getAllQuizzes(): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizById(id: String): QuizEntity?

    @Query("SELECT * FROM quizzes WHERE isFavorite = 1 ORDER BY lastPlayedAt DESC")
    fun getFavoriteQuizzes(): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchQuizzes(query: String): Flow<List<QuizEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity)

    @Update
    suspend fun updateQuiz(quiz: QuizEntity)

    @Delete
    suspend fun deleteQuiz(quiz: QuizEntity)

    @Query("DELETE FROM quizzes WHERE id = :id")
    suspend fun deleteQuizById(id: String)

    @Query("SELECT COUNT(*) FROM quizzes")
    fun getQuizCount(): Flow<Int>
}

@Dao
interface QuizResultDao {
    @Query("SELECT * FROM quiz_results ORDER BY completedAt DESC")
    fun getAllResults(): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results WHERE quizId = :quizId ORDER BY completedAt DESC")
    fun getResultsForQuiz(quizId: String): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results ORDER BY completedAt DESC LIMIT :limit")
    fun getRecentResults(limit: Int = 10): Flow<List<QuizResultEntity>>

    @Insert
    suspend fun insertResult(result: QuizResultEntity): Long

    @Query("DELETE FROM quiz_results WHERE quizId = :quizId")
    suspend fun deleteResultsForQuiz(quizId: String)

    @Query("SELECT AVG(percentage) FROM quiz_results WHERE quizId = :quizId")
    fun getAverageScore(quizId: String): Flow<Double?>

    @Query("SELECT COUNT(*) FROM quiz_results")
    fun getTotalAttempts(): Flow<Int>
}
