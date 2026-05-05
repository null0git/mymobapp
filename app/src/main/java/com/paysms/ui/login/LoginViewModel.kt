package com.paysms.ui.login

import android.app.Application
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paysms.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = UserPreferences(application)

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _hasExistingLogin = MutableStateFlow(false)
    val hasExistingLogin: StateFlow<Boolean> = _hasExistingLogin

    private val _biometricAvailable = MutableStateFlow(false)
    val biometricAvailable: StateFlow<Boolean> = _biometricAvailable

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    init {
        viewModelScope.launch {
            val savedPhone = prefs.phoneNumber.first()
            val loggedIn = prefs.isLoggedIn.first()
            if (savedPhone.isNotEmpty()) {
                _phoneNumber.value = savedPhone
                _hasExistingLogin.value = true
            }
            _isLoggedIn.value = loggedIn
        }
        checkBiometricAvailability()
    }

    private fun checkBiometricAvailability() {
        val biometricManager = BiometricManager.from(getApplication())
        _biometricAvailable.value = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun updatePhoneNumber(phone: String) {
        _phoneNumber.value = phone.filter { it.isDigit() }.take(9)
    }

    fun loginWithPhone(activity: FragmentActivity) {
        val phone = _phoneNumber.value
        if (phone.length < 9) {
            _loginError.value = "Please enter a valid phone number"
            return
        }

        if (_hasExistingLogin.value && _biometricAvailable.value) {
            showBiometricPrompt(activity)
        } else {
            performLogin()
        }
    }

    fun showBiometricPrompt(activity: FragmentActivity) {
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    performLogin()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                        errorCode == BiometricPrompt.ERROR_USER_CANCELED
                    ) {
                        _loginError.value = "Authentication cancelled"
                    } else {
                        performLogin()
                    }
                }

                override fun onAuthenticationFailed() {
                    _loginError.value = "Authentication failed. Try again."
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("telebirr Authentication")
            .setSubtitle("Verify your identity to continue")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun performLogin() {
        viewModelScope.launch {
            prefs.savePhoneNumber(_phoneNumber.value)
            prefs.setLoggedIn(true)
            if (!_hasExistingLogin.value) {
                prefs.saveUserName("CXQMD")
            }
            _isLoggedIn.value = true
        }
    }

    fun clearError() {
        _loginError.value = null
    }
}
