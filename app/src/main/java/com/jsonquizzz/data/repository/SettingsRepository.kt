package com.jsonquizzz.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.jsonquizzz.domain.model.AppSettings
import com.jsonquizzz.domain.model.FontSize
import com.jsonquizzz.domain.model.ThemeColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        val SHUFFLE_QUESTIONS = booleanPreferencesKey("shuffle_questions")
        val SHUFFLE_OPTIONS = booleanPreferencesKey("shuffle_options")
        val ENABLE_HINTS = booleanPreferencesKey("enable_hints")
        val INSTANT_FEEDBACK = booleanPreferencesKey("instant_feedback")
        val THEME_COLOR = stringPreferencesKey("theme_color")
        val FONT_SIZE = stringPreferencesKey("font_size")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            isDarkMode = prefs[Keys.DARK_MODE] ?: false,
            dynamicColors = prefs[Keys.DYNAMIC_COLORS] ?: true,
            shuffleQuestions = prefs[Keys.SHUFFLE_QUESTIONS] ?: false,
            shuffleOptions = prefs[Keys.SHUFFLE_OPTIONS] ?: false,
            enableHints = prefs[Keys.ENABLE_HINTS] ?: true,
            instantFeedback = prefs[Keys.INSTANT_FEEDBACK] ?: true,
            selectedThemeColor = try {
                ThemeColor.valueOf(prefs[Keys.THEME_COLOR] ?: ThemeColor.DEFAULT.name)
            } catch (_: Exception) { ThemeColor.DEFAULT },
            fontSize = try {
                FontSize.valueOf(prefs[Keys.FONT_SIZE] ?: FontSize.MEDIUM.name)
            } catch (_: Exception) { FontSize.MEDIUM }
        )
    }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = settings.isDarkMode
            prefs[Keys.DYNAMIC_COLORS] = settings.dynamicColors
            prefs[Keys.SHUFFLE_QUESTIONS] = settings.shuffleQuestions
            prefs[Keys.SHUFFLE_OPTIONS] = settings.shuffleOptions
            prefs[Keys.ENABLE_HINTS] = settings.enableHints
            prefs[Keys.INSTANT_FEEDBACK] = settings.instantFeedback
            prefs[Keys.THEME_COLOR] = settings.selectedThemeColor.name
            prefs[Keys.FONT_SIZE] = settings.fontSize.name
        }
    }

    suspend fun toggleDarkMode() {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = !(prefs[Keys.DARK_MODE] ?: false)
        }
    }
}
