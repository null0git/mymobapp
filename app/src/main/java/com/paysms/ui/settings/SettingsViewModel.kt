package com.paysms.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paysms.PaySmsApp
import com.paysms.data.model.AppSettings
import com.paysms.data.model.BankRule
import com.paysms.network.ApiClient
import com.paysms.network.EmailSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class SettingsState(
    val settings: AppSettings = AppSettings(),
    val bankRules: List<BankRule> = emptyList(),
    val isSaving: Boolean = false,
    val saveMessage: String = "",
    val apiTestLoading: Boolean = false,
    val apiTestResult: String? = null,
    val emailTestLoading: Boolean = false,
    val emailTestResult: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as PaySmsApp
    private val settingsManager = app.settingsManager
    private val repository = app.repository
    private val apiClient = ApiClient()
    private val emailSender = EmailSender()

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
            try {
                _state.value = _state.value.copy(isSaving = true)
                settingsManager.updateSettings(settings)
                _state.value = _state.value.copy(
                    settings = settings,
                    isSaving = false,
                    saveMessage = "Settings saved"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false, saveMessage = "Save failed: ${e.message}")
            }
        }
    }

    fun testApiConnection() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(apiTestLoading = true, apiTestResult = null)
                val settings = _state.value.settings

                if (settings.apiUrl.isBlank()) {
                    _state.value = _state.value.copy(apiTestLoading = false, apiTestResult = "Error: API URL is empty")
                    return@launch
                }

                val url = settings.apiUrl.trim()
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    _state.value = _state.value.copy(
                        apiTestLoading = false,
                        apiTestResult = "Error: URL must start with http:// or https://"
                    )
                    return@launch
                }

                val response = apiClient.testConnection(settings.copy(apiUrl = url))
                val result = if (response.success) {
                    "OK: ${response.statusCode} (${response.responseTimeMs}ms)\n${response.body.take(200)}"
                } else {
                    "Error: ${response.statusCode} - ${response.body.take(200)}"
                }
                _state.value = _state.value.copy(apiTestLoading = false, apiTestResult = result)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    apiTestLoading = false,
                    apiTestResult = "Crash prevented: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun testEmailConnection() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(emailTestLoading = true, emailTestResult = null)
                val settings = _state.value.settings

                if (settings.senderEmail.isBlank()) {
                    _state.value = _state.value.copy(emailTestLoading = false, emailTestResult = "Error: Sender email is empty")
                    return@launch
                }
                if (settings.senderPassword.isBlank()) {
                    _state.value = _state.value.copy(emailTestLoading = false, emailTestResult = "Error: Email password is empty")
                    return@launch
                }
                if (settings.receiverEmail.isBlank()) {
                    _state.value = _state.value.copy(emailTestLoading = false, emailTestResult = "Error: Receiver email is empty")
                    return@launch
                }
                if (!settings.senderEmail.contains("@") || !settings.receiverEmail.contains("@")) {
                    _state.value = _state.value.copy(emailTestLoading = false, emailTestResult = "Error: Invalid email format")
                    return@launch
                }

                val result = emailSender.testConnection(settings)
                _state.value = _state.value.copy(
                    emailTestLoading = false,
                    emailTestResult = result.message
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    emailTestLoading = false,
                    emailTestResult = "Crash prevented: ${e.message ?: "Unknown error"}"
                )
            }
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
