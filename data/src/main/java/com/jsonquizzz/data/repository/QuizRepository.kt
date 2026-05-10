package com.jsonquizzz.data.repository

import com.jsonquizzz.data.local.QuizDao
import com.jsonquizzz.data.local.QuizEntity
import com.jsonquizzz.data.local.QuizResultDao
import com.jsonquizzz.data.local.QuizResultEntity
import com.jsonquizzz.domain.model.Quiz
import com.jsonquizzz.domain.parser.QuizParser
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val quizDao: QuizDao,
    private val quizResultDao: QuizResultDao,
) {
    fun getAllQuizzes(): Flow<List<QuizEntity>> = quizDao.getAllQuizzes()

    fun getFavorites(): Flow<List<QuizEntity>> = quizDao.getFavoriteQuizzes()

    fun searchQuizzes(query: String): Flow<List<QuizEntity>> = quizDao.searchQuizzes(query)

    suspend fun getQuizById(id: String): QuizEntity? = quizDao.getQuizById(id)

    suspend fun saveQuiz(jsonContent: String): Result<String> {
        val parseResult = QuizParser.parse(jsonContent)
        return parseResult.fold(
            onSuccess = { quiz ->
                val id = UUID.randomUUID().toString()
                val entity = QuizEntity(
                    id = id,
                    title = quiz.title,
                    description = quiz.description,
                    jsonContent = jsonContent,
                )
                quizDao.insertQuiz(entity)
                Result.success(id)
            },
            onFailure = { Result.failure(it) },
        )
    }

    suspend fun deleteQuiz(id: String) = quizDao.deleteQuizById(id)

    suspend fun toggleFavorite(id: String) {
        val quiz = quizDao.getQuizById(id) ?: return
        quizDao.updateQuiz(quiz.copy(isFavorite = !quiz.isFavorite))
    }

    suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        val quiz = quizDao.getQuizById(id) ?: return
        quizDao.updateQuiz(quiz.copy(isFavorite = isFavorite))
    }

    suspend fun recordPlay(id: String) {
        val quiz = quizDao.getQuizById(id) ?: return
        quizDao.updateQuiz(
            quiz.copy(
                lastPlayedAt = System.currentTimeMillis(),
                playCount = quiz.playCount + 1,
            )
        )
    }

    fun getAllResults(): Flow<List<QuizResultEntity>> = quizResultDao.getAllResults()

    fun getResultsForQuiz(quizId: String): Flow<List<QuizResultEntity>> =
        quizResultDao.getResultsForQuiz(quizId)

    fun getRecentResults(limit: Int = 10): Flow<List<QuizResultEntity>> =
        quizResultDao.getRecentResults(limit)

    suspend fun saveResult(result: QuizResultEntity): Long =
        quizResultDao.insertResult(result)

    fun getQuizCount(): Flow<Int> = quizDao.getQuizCount()

    fun getTotalAttempts(): Flow<Int> = quizResultDao.getTotalAttempts()

    fun getAverageScore(quizId: String): Flow<Double?> =
        quizResultDao.getAverageScore(quizId)
}
