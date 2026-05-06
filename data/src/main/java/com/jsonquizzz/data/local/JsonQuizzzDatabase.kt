package com.jsonquizzz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        QuizEntity::class,
        QuizResultEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class JsonQuizzzDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
    abstract fun quizResultDao(): QuizResultDao

    companion object {
        const val DATABASE_NAME = "jsonquizzz_db"
    }
}
