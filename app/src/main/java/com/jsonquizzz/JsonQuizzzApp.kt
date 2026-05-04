package com.jsonquizzz

import android.app.Application
import com.jsonquizzz.data.local.AppDatabase
import com.jsonquizzz.data.repository.QuizRepository
import com.jsonquizzz.data.repository.SettingsRepository

class JsonQuizzzApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var quizRepository: QuizRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        quizRepository = QuizRepository(database)
        settingsRepository = SettingsRepository(this)
    }

    companion object {
        lateinit var instance: JsonQuizzzApp
            private set
    }
}
