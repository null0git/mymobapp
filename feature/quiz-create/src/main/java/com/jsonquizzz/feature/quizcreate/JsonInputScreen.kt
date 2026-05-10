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
import com.jsonquizzz.domain.parser.QuizParser
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonInputScreen(
    onQuizCreated: (String) -> Unit = {},
    onBack: () -> Unit = {},
    onSaveQuiz: (suspend (String) -> Result<String>)? = null,
    modifier: Modifier = Modifier,
) {
    var jsonText by remember { mutableStateOf("") }
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }
    var isValid by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    fun validate() {
        if (jsonText.isBlank()) {
            validationErrors = listOf("Please paste your quiz JSON")
            isValid = false
            return
        }
        val parseResult = QuizParser.parse(jsonText)
        parseResult.fold(
            onSuccess = { quiz ->
                val errors = QuizParser.validate(quiz)
                validationErrors = errors
                isValid = errors.isEmpty()
            },
            onFailure = { e ->
                validationErrors = listOf("Invalid JSON: ${e.message}")
                isValid = false
            },
        )
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
                    if (isValid && onSaveQuiz != null) {
                        scope.launch {
                            onSaveQuiz(jsonText).fold(
                                onSuccess = { id ->
                                    snackbarHostState.showSnackbar("Quiz saved!")
                                    onQuizCreated(id)
                                },
                                onFailure = { e ->
                                    snackbarHostState.showSnackbar("Error: ${e.message}")
                                },
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = jsonText.isNotBlank(),
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Text("  Validate & Save")
            }
        }
    }
}
