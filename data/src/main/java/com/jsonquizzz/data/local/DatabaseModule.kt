package com.jsonquizzz.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): JsonQuizzzDatabase {
        return Room.databaseBuilder(
            context,
            JsonQuizzzDatabase::class.java,
            JsonQuizzzDatabase.DATABASE_NAME,
        ).build()
    }

    @Provides
    fun provideQuizDao(database: JsonQuizzzDatabase): QuizDao = database.quizDao()

    @Provides
    fun provideQuizResultDao(database: JsonQuizzzDatabase): QuizResultDao = database.quizResultDao()
}
