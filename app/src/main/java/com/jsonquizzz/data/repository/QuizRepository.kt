package com.jsonquizzz.data.repository

import com.jsonquizzz.data.local.AppDatabase
import com.jsonquizzz.data.local.converter.EntityConverters.toDomainAttempt
import com.jsonquizzz.data.local.converter.EntityConverters.toDomainBookmark
import com.jsonquizzz.data.local.converter.EntityConverters.toDomainQuiz
import com.jsonquizzz.data.local.converter.EntityConverters.toDomainResult
import com.jsonquizzz.data.local.converter.EntityConverters.toEntity
import com.jsonquizzz.domain.model.*
import com.jsonquizzz.engine.QuizResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuizRepository(private val database: AppDatabase) {

    // Quiz operations
    fun getAllQuizzes(): Flow<List<Quiz>> =
        database.quizDao().getAllQuizzes().map { entities ->
            entities.map { it.toDomainQuiz() }
        }

    suspend fun getQuizById(id: String): Quiz? =
        database.quizDao().getQuizById(id)?.toDomainQuiz()

    fun searchQuizzes(query: String): Flow<List<Quiz>> =
        database.quizDao().searchQuizzes(query).map { entities ->
            entities.map { it.toDomainQuiz() }
        }

    fun getQuizzesByCategory(category: QuizCategory): Flow<List<Quiz>> =
        database.quizDao().getQuizzesByCategory(category.name).map { entities ->
            entities.map { it.toDomainQuiz() }
        }

    fun getQuizzesByDifficulty(difficulty: Difficulty): Flow<List<Quiz>> =
        database.quizDao().getQuizzesByDifficulty(difficulty.name).map { entities ->
            entities.map { it.toDomainQuiz() }
        }

    suspend fun saveQuiz(quiz: Quiz) =
        database.quizDao().insertQuiz(quiz.toEntity())

    suspend fun updateQuiz(quiz: Quiz) =
        database.quizDao().updateQuiz(quiz.toEntity())

    suspend fun deleteQuiz(quizId: String) =
        database.quizDao().deleteQuizById(quizId)

    suspend fun getQuizCount(): Int =
        database.quizDao().getQuizCount()

    // Attempt operations
    fun getAllAttempts(): Flow<List<QuizAttempt>> =
        database.attemptDao().getAllAttempts().map { entities ->
            entities.map { it.toDomainAttempt() }
        }

    suspend fun getAttemptById(id: String): QuizAttempt? =
        database.attemptDao().getAttemptById(id)?.toDomainAttempt()

    fun getAttemptsForQuiz(quizId: String): Flow<List<QuizAttempt>> =
        database.attemptDao().getAttemptsForQuiz(quizId).map { entities ->
            entities.map { it.toDomainAttempt() }
        }

    suspend fun getUnfinishedAttempt(quizId: String): QuizAttempt? =
        database.attemptDao().getUnfinishedAttemptForQuiz(quizId)?.toDomainAttempt()

    suspend fun saveAttempt(attempt: QuizAttempt) =
        database.attemptDao().insertAttempt(attempt.toEntity())

    fun getRecentAttempts(limit: Int = 10): Flow<List<QuizAttempt>> =
        database.attemptDao().getRecentAttempts(limit).map { entities ->
            entities.map { it.toDomainAttempt() }
        }

    // Bookmark operations
    fun getAllBookmarks(): Flow<List<Bookmark>> =
        database.bookmarkDao().getAllBookmarks().map { entities ->
            entities.map { it.toDomainBookmark() }
        }

    fun getBookmarksForQuiz(quizId: String): Flow<List<Bookmark>> =
        database.bookmarkDao().getBookmarksForQuiz(quizId).map { entities ->
            entities.map { it.toDomainBookmark() }
        }

    suspend fun isQuestionBookmarked(questionId: String): Boolean =
        database.bookmarkDao().isQuestionBookmarked(questionId)

    suspend fun toggleBookmark(bookmark: Bookmark) {
        if (database.bookmarkDao().isQuestionBookmarked(bookmark.questionId)) {
            database.bookmarkDao().deleteBookmarkByQuestionId(bookmark.questionId)
        } else {
            database.bookmarkDao().insertBookmark(bookmark.toEntity())
        }
    }

    suspend fun getBookmarkCount(): Int =
        database.bookmarkDao().getBookmarkCount()

    // Result operations
    fun getAllResults(): Flow<List<QuizResult>> =
        database.resultDao().getAllResults().map { entities ->
            entities.map { it.toDomainResult() }
        }

    suspend fun saveResult(result: QuizResult) =
        database.resultDao().insertResult(result.toEntity())

    fun getResultsForQuiz(quizId: String): Flow<List<QuizResult>> =
        database.resultDao().getResultsForQuiz(quizId).map { entities ->
            entities.map { it.toDomainResult() }
        }

    fun getRecentResults(limit: Int = 10): Flow<List<QuizResult>> =
        database.resultDao().getRecentResults(limit).map { entities ->
            entities.map { it.toDomainResult() }
        }

    suspend fun getAverageScore(): Double =
        database.resultDao().getAverageScore() ?: 0.0

    suspend fun getResultCount(): Int =
        database.resultDao().getResultCount()
}
