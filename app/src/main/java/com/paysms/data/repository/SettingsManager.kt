package com.paysms.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paysms.data.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "paysms_settings")

class SettingsManager(private val context: Context) {

    private val gson = Gson()

    private object Keys {
        val API_URL = stringPreferencesKey("api_url")
        val API_METHOD = stringPreferencesKey("api_method")
        val API_HEADERS = stringPreferencesKey("api_headers")
        val API_KEY = stringPreferencesKey("api_key")
        val API_ENABLED = booleanPreferencesKey("api_enabled")
        val EMAIL_ENABLED = booleanPreferencesKey("email_enabled")
        val SENDER_EMAIL = stringPreferencesKey("sender_email")
        val SENDER_PASSWORD = stringPreferencesKey("sender_password")
        val RECEIVER_EMAIL = stringPreferencesKey("receiver_email")
        val SMTP_HOST = stringPreferencesKey("smtp_host")
        val SMTP_PORT = intPreferencesKey("smtp_port")
        val ALLOWED_SENDERS = stringPreferencesKey("allowed_senders")
        val KEYWORDS = stringPreferencesKey("keywords")
        val AI_ENABLED = booleanPreferencesKey("ai_enabled")
        val AI_CONFIDENCE = floatPreferencesKey("ai_confidence")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val AUTO_SEND_API = booleanPreferencesKey("auto_send_api")
        val AUTO_SEND_EMAIL = booleanPreferencesKey("auto_send_email")
        val DATA_RETENTION_DAYS = intPreferencesKey("data_retention_days")
        val EXPORT_FORMAT = stringPreferencesKey("export_format")
        val DARK_MODE = stringPreferencesKey("dark_mode")
        val WHITELIST_ENABLED = booleanPreferencesKey("whitelist_enabled")
        val WHITELISTED_SENDERS = stringPreferencesKey("whitelisted_senders")
        val BLACKLISTED_SENDERS = stringPreferencesKey("blacklisted_senders")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            apiUrl = prefs[Keys.API_URL] ?: "",
            apiMethod = prefs[Keys.API_METHOD] ?: "POST",
            apiHeaders = prefs[Keys.API_HEADERS]?.let {
                gson.fromJson<Map<String, String>>(it, object : TypeToken<Map<String, String>>() {}.type)
            } ?: emptyMap(),
            apiKey = prefs[Keys.API_KEY] ?: "",
            apiEnabled = prefs[Keys.API_ENABLED] ?: false,
            emailEnabled = prefs[Keys.EMAIL_ENABLED] ?: false,
            senderEmail = prefs[Keys.SENDER_EMAIL] ?: "",
            senderPassword = prefs[Keys.SENDER_PASSWORD] ?: "",
            receiverEmail = prefs[Keys.RECEIVER_EMAIL] ?: "",
            smtpHost = prefs[Keys.SMTP_HOST] ?: "smtp.gmail.com",
            smtpPort = prefs[Keys.SMTP_PORT] ?: 587,
            allowedSenders = prefs[Keys.ALLOWED_SENDERS]?.let {
                gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
            } ?: emptyList(),
            keywords = prefs[Keys.KEYWORDS]?.let {
                gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
            } ?: listOf("credited", "received", "transfer", "deposited"),
            aiEnabled = prefs[Keys.AI_ENABLED] ?: false,
            aiConfidenceThreshold = prefs[Keys.AI_CONFIDENCE] ?: 0.8f,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: true,
            autoSendApi = prefs[Keys.AUTO_SEND_API] ?: true,
            autoSendEmail = prefs[Keys.AUTO_SEND_EMAIL] ?: true,
            dataRetentionDays = prefs[Keys.DATA_RETENTION_DAYS] ?: 90,
            exportFormat = prefs[Keys.EXPORT_FORMAT] ?: "CSV",
            darkMode = prefs[Keys.DARK_MODE] ?: "system",
            whitelistEnabled = prefs[Keys.WHITELIST_ENABLED] ?: false,
            whitelistedSenders = prefs[Keys.WHITELISTED_SENDERS]?.let {
                gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
            } ?: emptyList(),
            blacklistedSenders = prefs[Keys.BLACKLISTED_SENDERS]?.let {
                gson.fromJson<List<String>>(it, object : TypeToken<List<String>>() {}.type)
            } ?: emptyList()
        )
    }

    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { prefs ->
            prefs[Keys.API_URL] = settings.apiUrl
            prefs[Keys.API_METHOD] = settings.apiMethod
            prefs[Keys.API_HEADERS] = gson.toJson(settings.apiHeaders)
            prefs[Keys.API_KEY] = settings.apiKey
            prefs[Keys.API_ENABLED] = settings.apiEnabled
            prefs[Keys.EMAIL_ENABLED] = settings.emailEnabled
            prefs[Keys.SENDER_EMAIL] = settings.senderEmail
            prefs[Keys.SENDER_PASSWORD] = settings.senderPassword
            prefs[Keys.RECEIVER_EMAIL] = settings.receiverEmail
            prefs[Keys.SMTP_HOST] = settings.smtpHost
            prefs[Keys.SMTP_PORT] = settings.smtpPort
            prefs[Keys.ALLOWED_SENDERS] = gson.toJson(settings.allowedSenders)
            prefs[Keys.KEYWORDS] = gson.toJson(settings.keywords)
            prefs[Keys.AI_ENABLED] = settings.aiEnabled
            prefs[Keys.AI_CONFIDENCE] = settings.aiConfidenceThreshold
            prefs[Keys.NOTIFICATIONS_ENABLED] = settings.notificationsEnabled
            prefs[Keys.AUTO_SEND_API] = settings.autoSendApi
            prefs[Keys.AUTO_SEND_EMAIL] = settings.autoSendEmail
            prefs[Keys.DATA_RETENTION_DAYS] = settings.dataRetentionDays
            prefs[Keys.EXPORT_FORMAT] = settings.exportFormat
            prefs[Keys.DARK_MODE] = settings.darkMode
            prefs[Keys.WHITELIST_ENABLED] = settings.whitelistEnabled
            prefs[Keys.WHITELISTED_SENDERS] = gson.toJson(settings.whitelistedSenders)
            prefs[Keys.BLACKLISTED_SENDERS] = gson.toJson(settings.blacklistedSenders)
        }
    }
}
