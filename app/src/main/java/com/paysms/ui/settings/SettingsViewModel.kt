package com.paysms.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paysms.PaySmsApp
import com.paysms.data.model.AppSettings
import com.paysms.data.model.BankRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SettingsState(
    val settings: AppSettings = AppSettings(),
    val bankRules: List<BankRule> = emptyList(),
    val isSaving: Boolean = false,
    val saveMessage: String = ""
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as PaySmsApp
    private val settingsManager = app.settingsManager
    private val repository = app.repository

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        loadSettings()
        loadBankRules()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsManager.settings.collectLatest { settings ->
                _state.value = _state.value.copy(settings = settings)
            }
        }
    }

    private fun loadBankRules() {
        viewModelScope.launch {
            repository.getAllBankRules().collectLatest { rules ->
                _state.value = _state.value.copy(bankRules = rules)
            }
        }
    }

    fun updateSettings(settings: AppSettings) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)
            settingsManager.updateSettings(settings)
            _state.value = _state.value.copy(
                settings = settings,
                isSaving = false,
                saveMessage = "Settings saved"
            )
        }
    }

    fun addBankRule(rule: BankRule) {
        viewModelScope.launch {
            repository.insertBankRule(rule)
        }
    }

    fun updateBankRule(rule: BankRule) {
        viewModelScope.launch {
            repository.updateBankRule(rule)
        }
    }

    fun deleteBankRule(rule: BankRule) {
        viewModelScope.launch {
            repository.deleteBankRule(rule)
        }
    }

    fun clearSaveMessage() {
        _state.value = _state.value.copy(saveMessage = "")
    }
}
