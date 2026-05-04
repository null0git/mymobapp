package com.jsonquizzz.data.local.dao

import androidx.room.*
import com.jsonquizzz.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE quizId = :quizId")
    fun getBookmarksForQuiz(quizId: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE questionId = :questionId LIMIT 1")
    suspend fun getBookmarkForQuestion(questionId: String): BookmarkEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE questionId = :questionId)")
    suspend fun isQuestionBookmarked(questionId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE questionId = :questionId")
    suspend fun deleteBookmarkByQuestionId(questionId: String)

    @Query("SELECT COUNT(*) FROM bookmarks")
    suspend fun getBookmarkCount(): Int
}
