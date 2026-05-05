package com.paysms

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paysms.data.UserPreferences
import com.paysms.ui.login.LoginScreen
import com.paysms.ui.login.LoginViewModel
import com.paysms.ui.navigation.MainNavigation
import com.paysms.ui.theme.TelebirrTheme

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPreferences = UserPreferences(applicationContext)

        setContent {
            TelebirrTheme {
                val loginViewModel: LoginViewModel = viewModel()
                val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
                val hasExistingLogin by loginViewModel.hasExistingLogin.collectAsState()
                val biometricAvailable by loginViewModel.biometricAvailable.collectAsState()
                var showMainApp by remember { mutableStateOf(false) }

                if (showMainApp || isLoggedIn) {
                    MainNavigation(
                        userPreferences = userPreferences,
                        onLogout = {
                            showMainApp = false
                        }
                    )
                } else {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            showMainApp = true
                        }
                    )
                }
            }
        }
    }
}
