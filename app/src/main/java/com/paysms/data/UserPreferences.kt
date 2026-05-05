package com.paysms.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "telebirr_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val PHONE_NUMBER = stringPreferencesKey("phone_number")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val BALANCE = stringPreferencesKey("balance")
        private val ENDEKISE_BALANCE = stringPreferencesKey("endekise_balance")
        private val REWARD_BALANCE = stringPreferencesKey("reward_balance")
        private val ACCOUNT_NUMBER = stringPreferencesKey("account_number")
        private val EMAIL = stringPreferencesKey("email")
        private val FULL_NAME = stringPreferencesKey("full_name")
        private val GENDER = stringPreferencesKey("gender")
        private val DATE_OF_BIRTH = stringPreferencesKey("date_of_birth")
        private val REGION = stringPreferencesKey("region")
        private val CITY = stringPreferencesKey("city")
    }

    val phoneNumber: Flow<String> = context.dataStore.data.map { it[PHONE_NUMBER] ?: "" }
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { it[IS_LOGGED_IN] ?: false }
    val userName: Flow<String> = context.dataStore.data.map { it[USER_NAME] ?: "User" }
    val balance: Flow<String> = context.dataStore.data.map { it[BALANCE] ?: "******" }
    val endekiseBalance: Flow<String> = context.dataStore.data.map { it[ENDEKISE_BALANCE] ?: "******" }
    val rewardBalance: Flow<String> = context.dataStore.data.map { it[REWARD_BALANCE] ?: "******" }
    val accountNumber: Flow<String> = context.dataStore.data.map { it[ACCOUNT_NUMBER] ?: "" }
    val email: Flow<String> = context.dataStore.data.map { it[EMAIL] ?: "" }
    val fullName: Flow<String> = context.dataStore.data.map { it[FULL_NAME] ?: "" }
    val gender: Flow<String> = context.dataStore.data.map { it[GENDER] ?: "" }
    val dateOfBirth: Flow<String> = context.dataStore.data.map { it[DATE_OF_BIRTH] ?: "" }
    val region: Flow<String> = context.dataStore.data.map { it[REGION] ?: "" }
    val city: Flow<String> = context.dataStore.data.map { it[CITY] ?: "" }

    suspend fun savePhoneNumber(phone: String) {
        context.dataStore.edit { it[PHONE_NUMBER] = phone }
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { it[IS_LOGGED_IN] = loggedIn }
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { it[USER_NAME] = name }
    }

    suspend fun saveBalance(balance: String) {
        context.dataStore.edit { it[BALANCE] = balance }
    }

    suspend fun saveEndekiseBalance(balance: String) {
        context.dataStore.edit { it[ENDEKISE_BALANCE] = balance }
    }

    suspend fun saveRewardBalance(balance: String) {
        context.dataStore.edit { it[REWARD_BALANCE] = balance }
    }

    suspend fun saveAccountNumber(account: String) {
        context.dataStore.edit { it[ACCOUNT_NUMBER] = account }
    }

    suspend fun saveEmail(email: String) {
        context.dataStore.edit { it[EMAIL] = email }
    }

    suspend fun saveFullName(name: String) {
        context.dataStore.edit { it[FULL_NAME] = name }
    }

    suspend fun saveGender(gender: String) {
        context.dataStore.edit { it[GENDER] = gender }
    }

    suspend fun saveDateOfBirth(dob: String) {
        context.dataStore.edit { it[DATE_OF_BIRTH] = dob }
    }

    suspend fun saveRegion(region: String) {
        context.dataStore.edit { it[REGION] = region }
    }

    suspend fun saveCity(city: String) {
        context.dataStore.edit { it[CITY] = city }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = false
        }
    }
}
