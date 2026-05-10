package com.jsonquizzz.feature.quizcreate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonInputScreen(
    onQuizCreated: (String) -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: CreateViewModel = hiltViewModel(),
) {
    var jsonText by remember { mutableStateOf("") }
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }
    var isValid by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    fun validate() {
        val errors = viewModel.validateJson(jsonText)
        validationErrors = errors
        isValid = errors.isEmpty()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paste JSON") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        val text = clipboardManager.getText()?.text ?: ""
                        if (text.isNotBlank()) {
                            jsonText = text
                            validate()
                        }
                    }) {
                        Icon(Icons.Default.ContentPaste, contentDescription = null)
                        Text("Paste")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Paste your quiz JSON below:",
                style = MaterialTheme.typography.titleMedium,
            )

            OutlinedTextField(
                value = jsonText,
                onValueChange = {
                    jsonText = it
                    if (validationErrors.isNotEmpty()) validate()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Quiz JSON") },
                isError = validationErrors.isNotEmpty(),
                supportingText = if (validationErrors.isNotEmpty()) {
                    { Text(validationErrors.first(), color = MaterialTheme.colorScheme.error) }
                } else null,
            )

            Button(
                onClick = {
                    validate()
                    if (isValid) {
                        isSaving = true
                        scope.launch {
                            viewModel.saveQuiz(jsonText).fold(
                                onSuccess = { id ->
                                    isSaving = false
                                    snackbarHostState.showSnackbar("Quiz saved!")
                                    onQuizCreated(id)
                                },
                                onFailure = { e ->
                                    isSaving = false
                                    snackbarHostState.showSnackbar("Error: ${e.message}")
                                },
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = jsonText.isNotBlank() && !isSaving,
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Text("  Validate & Save")
                }
            }
        }
    }
}
