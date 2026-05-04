package com.jsonquizzz.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jsonquizzz.data.local.dao.*
import com.jsonquizzz.data.local.entity.*

@Database(
    entities = [
        QuizEntity::class,
        QuizAttemptEntity::class,
        BookmarkEntity::class,
        QuizResultEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
    abstract fun attemptDao(): AttemptDao
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun resultDao(): ResultDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "jsonquizzz_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
