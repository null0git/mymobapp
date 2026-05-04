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
    val emailTestResult: String? = null,
    val errorDialogMessage: String? = null
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
            try {
                settingsManager.settings.collectLatest { settings ->
                    _state.value = _state.value.copy(settings = settings)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorDialogMessage = "Failed to load settings: ${e.message}"
                )
            }
        }
    }

    private fun loadBankRules() {
        viewModelScope.launch {
            try {
                repository.getAllBankRules().collectLatest { rules ->
                    _state.value = _state.value.copy(bankRules = rules)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorDialogMessage = "Failed to load bank rules: ${e.message}"
                )
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
                _state.value = _state.value.copy(
                    isSaving = false,
                    saveMessage = "Save failed: ${e.message}",
                    errorDialogMessage = "Failed to save settings: ${e.message}"
                )
            }
        }
    }

    fun testApiConnection() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(apiTestLoading = true, apiTestResult = null)
                val settings = _state.value.settings

                if (settings.apiUrl.isBlank()) {
                    _state.value = _state.value.copy(
                        apiTestLoading = false,
                        apiTestResult = "Error: API URL is empty",
                        errorDialogMessage = "API URL is empty. Please enter a valid URL."
                    )
                    return@launch
                }

                val url = settings.apiUrl.trim()
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    _state.value = _state.value.copy(
                        apiTestLoading = false,
                        apiTestResult = "Error: URL must start with http:// or https://",
                        errorDialogMessage = "Invalid URL format. URL must start with http:// or https://"
                    )
                    return@launch
                }

                try {
                    java.net.URL(url)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        apiTestLoading = false,
                        apiTestResult = "Error: Malformed URL",
                        errorDialogMessage = "The URL is malformed: ${e.message}"
                    )
                    return@launch
                }

                val response = apiClient.testConnection(settings.copy(apiUrl = url))
                if (response.success) {
                    _state.value = _state.value.copy(
                        apiTestLoading = false,
                        apiTestResult = "OK: ${response.statusCode} (${response.responseTimeMs}ms)\n${response.body.take(200)}"
                    )
                } else {
                    _state.value = _state.value.copy(
                        apiTestLoading = false,
                        apiTestResult = "Error: ${response.statusCode} - ${response.body.take(200)}",
                        errorDialogMessage = "API Test Failed\n\nStatus: ${response.statusCode}\n${response.body.take(300)}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    apiTestLoading = false,
                    apiTestResult = "Error: ${e.message ?: "Unknown error"}",
                    errorDialogMessage = "API Test Error\n\n${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}"
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
                    _state.value = _state.value.copy(
                        emailTestLoading = false,
                        emailTestResult = "Error: Sender email is empty",
                        errorDialogMessage = "Sender email is empty. Please enter a valid sender email address."
                    )
                    return@launch
                }
                if (settings.senderPassword.isBlank()) {
                    _state.value = _state.value.copy(
                        emailTestLoading = false,
                        emailTestResult = "Error: Email password is empty",
                        errorDialogMessage = "Email password is empty. For Gmail, use an App Password (not your regular password)."
                    )
                    return@launch
                }
                if (settings.receiverEmail.isBlank()) {
                    _state.value = _state.value.copy(
                        emailTestLoading = false,
                        emailTestResult = "Error: Receiver email is empty",
                        errorDialogMessage = "Receiver email is empty. Please enter a valid receiver email address."
                    )
                    return@launch
                }
                if (!settings.senderEmail.contains("@") || !settings.receiverEmail.contains("@")) {
                    _state.value = _state.value.copy(
                        emailTestLoading = false,
                        emailTestResult = "Error: Invalid email format",
                        errorDialogMessage = "Invalid email format. Emails must contain an '@' symbol."
                    )
                    return@launch
                }
                if (settings.smtpHost.isBlank()) {
                    _state.value = _state.value.copy(
                        emailTestLoading = false,
                        emailTestResult = "Error: SMTP host is empty",
                        errorDialogMessage = "SMTP host is empty. Common values: smtp.gmail.com, smtp.outlook.com"
                    )
                    return@launch
                }

                val result = emailSender.testConnection(settings)
                _state.value = _state.value.copy(
                    emailTestLoading = false,
                    emailTestResult = result.message
                )
                if (!result.success) {
                    _state.value = _state.value.copy(
                        errorDialogMessage = "Email Test Failed\n\n${result.message}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    emailTestLoading = false,
                    emailTestResult = "Error: ${e.message ?: "Unknown error"}",
                    errorDialogMessage = "Email Test Error\n\n${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }

    fun dismissErrorDialog() {
        _state.value = _state.value.copy(errorDialogMessage = null)
    }

    fun addBankRule(rule: BankRule) {
        viewModelScope.launch {
            try {
                repository.insertBankRule(rule)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorDialogMessage = "Failed to add bank rule: ${e.message}"
                )
            }
        }
    }

    fun updateBankRule(rule: BankRule) {
        viewModelScope.launch {
            try {
                repository.updateBankRule(rule)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorDialogMessage = "Failed to update bank rule: ${e.message}"
                )
            }
        }
    }

    fun deleteBankRule(rule: BankRule) {
        viewModelScope.launch {
            try {
                repository.deleteBankRule(rule)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorDialogMessage = "Failed to delete bank rule: ${e.message}"
                )
            }
        }
    }

    fun clearSaveMessage() {
        _state.value = _state.value.copy(saveMessage = "")
    }
}
