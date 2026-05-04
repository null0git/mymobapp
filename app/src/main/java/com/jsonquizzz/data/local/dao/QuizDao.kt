package com.jsonquizzz.data.local.dao

import androidx.room.*
import com.jsonquizzz.data.local.entity.QuizEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes ORDER BY updatedAt DESC")
    fun getAllQuizzes(): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizById(id: String): QuizEntity?

    @Query("SELECT * FROM quizzes WHERE category = :category ORDER BY updatedAt DESC")
    fun getQuizzesByCategory(category: String): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchQuizzes(query: String): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE difficulty = :difficulty")
    fun getQuizzesByDifficulty(difficulty: String): Flow<List<QuizEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity)

    @Update
    suspend fun updateQuiz(quiz: QuizEntity)

    @Delete
    suspend fun deleteQuiz(quiz: QuizEntity)

    @Query("DELETE FROM quizzes WHERE id = :id")
    suspend fun deleteQuizById(id: String)

    @Query("SELECT COUNT(*) FROM quizzes")
    suspend fun getQuizCount(): Int
}
