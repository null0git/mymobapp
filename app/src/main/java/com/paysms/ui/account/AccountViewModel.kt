package com.paysms.ui.account

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paysms.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    val prefs = UserPreferences(application)

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _accountNumber = MutableStateFlow("")
    val accountNumber: StateFlow<String> = _accountNumber

    private val _balance = MutableStateFlow("")
    val balance: StateFlow<String> = _balance

    private val _endekiseBalance = MutableStateFlow("")
    val endekiseBalance: StateFlow<String> = _endekiseBalance

    private val _rewardBalance = MutableStateFlow("")
    val rewardBalance: StateFlow<String> = _rewardBalance

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender

    private val _dateOfBirth = MutableStateFlow("")
    val dateOfBirth: StateFlow<String> = _dateOfBirth

    private val _region = MutableStateFlow("")
    val region: StateFlow<String> = _region

    private val _city = MutableStateFlow("")
    val city: StateFlow<String> = _city

    private val _hiddenSettingsEnabled = MutableStateFlow(false)
    val hiddenSettingsEnabled: StateFlow<Boolean> = _hiddenSettingsEnabled

    private val _tapCount = MutableStateFlow(0)

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _phoneNumber.value = prefs.phoneNumber.first()
            _userName.value = prefs.userName.first()
            _fullName.value = prefs.fullName.first()
            _email.value = prefs.email.first()
            _accountNumber.value = prefs.accountNumber.first()
            _balance.value = prefs.balance.first()
            _endekiseBalance.value = prefs.endekiseBalance.first()
            _rewardBalance.value = prefs.rewardBalance.first()
            _gender.value = prefs.gender.first()
            _dateOfBirth.value = prefs.dateOfBirth.first()
            _region.value = prefs.region.first()
            _city.value = prefs.city.first()
        }
    }

    fun onVersionTap() {
        _tapCount.value++
        if (_tapCount.value >= 7) {
            _hiddenSettingsEnabled.value = !_hiddenSettingsEnabled.value
            _tapCount.value = 0
        }
    }

    fun updateUserName(value: String) {
        _userName.value = value
        viewModelScope.launch { prefs.saveUserName(value) }
    }

    fun updateFullName(value: String) {
        _fullName.value = value
        viewModelScope.launch { prefs.saveFullName(value) }
    }

    fun updateEmail(value: String) {
        _email.value = value
        viewModelScope.launch { prefs.saveEmail(value) }
    }

    fun updateAccountNumber(value: String) {
        _accountNumber.value = value
        viewModelScope.launch { prefs.saveAccountNumber(value) }
    }

    fun updateBalance(value: String) {
        _balance.value = value
        viewModelScope.launch { prefs.saveBalance(value) }
    }

    fun updateEndekiseBalance(value: String) {
        _endekiseBalance.value = value
        viewModelScope.launch { prefs.saveEndekiseBalance(value) }
    }

    fun updateRewardBalance(value: String) {
        _rewardBalance.value = value
        viewModelScope.launch { prefs.saveRewardBalance(value) }
    }

    fun updatePhoneNumber(value: String) {
        _phoneNumber.value = value
        viewModelScope.launch { prefs.savePhoneNumber(value) }
    }

    fun updateGender(value: String) {
        _gender.value = value
        viewModelScope.launch { prefs.saveGender(value) }
    }

    fun updateDateOfBirth(value: String) {
        _dateOfBirth.value = value
        viewModelScope.launch { prefs.saveDateOfBirth(value) }
    }

    fun updateRegion(value: String) {
        _region.value = value
        viewModelScope.launch { prefs.saveRegion(value) }
    }

    fun updateCity(value: String) {
        _city.value = value
        viewModelScope.launch { prefs.saveCity(value) }
    }

    fun logout() {
        viewModelScope.launch {
            prefs.logout()
        }
    }
}
