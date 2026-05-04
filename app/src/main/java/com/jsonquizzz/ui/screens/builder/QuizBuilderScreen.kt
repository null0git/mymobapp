package com.jsonquizzz.ui.screens.builder

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsonquizzz.JsonQuizzzApp
import com.jsonquizzz.domain.model.*
import com.jsonquizzz.fileformat.JqzFileFormat
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BuilderState(
    val quiz: Quiz = Quiz(
        sections = listOf(QuizSection(title = "Section 1", order = 0))
    ),
    val currentSectionIndex: Int = 0,
    val currentQuestionIndex: Int = -1,
    val isSaving: Boolean = false,
    val message: String? = null,
    val isEditing: Boolean = false
)

class BuilderViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as JsonQuizzzApp
    private val _state = MutableStateFlow(BuilderState())
    val state: StateFlow<BuilderState> = _state.asStateFlow()

    fun loadQuiz(quizId: String?) {
        if (quizId == null) return
        viewModelScope.launch {
            val quiz = app.quizRepository.getQuizById(quizId) ?: return@launch
            _state.update { it.copy(quiz = quiz, isEditing = true) }
        }
    }

    fun updateQuizTitle(title: String) {
        _state.update { it.copy(quiz = it.quiz.copy(title = title)) }
    }

    fun updateQuizDescription(description: String) {
        _state.update { it.copy(quiz = it.quiz.copy(description = description)) }
    }

    fun updateQuizCategory(category: QuizCategory) {
        _state.update { it.copy(quiz = it.quiz.copy(category = category)) }
    }

    fun updateQuizDifficulty(difficulty: Difficulty) {
        _state.update { it.copy(quiz = it.quiz.copy(difficulty = difficulty)) }
    }

    fun addSection() {
        _state.update { state ->
            val sections = state.quiz.sections + QuizSection(
                title = "Section ${state.quiz.sections.size + 1}",
                order = state.quiz.sections.size
            )
            state.copy(
                quiz = state.quiz.copy(sections = sections),
                currentSectionIndex = sections.size - 1
            )
        }
    }

    fun updateSectionTitle(index: Int, title: String) {
        _state.update { state ->
            val sections = state.quiz.sections.toMutableList()
            sections[index] = sections[index].copy(title = title)
            state.copy(quiz = state.quiz.copy(sections = sections))
        }
    }

    fun addQuestion(sectionIndex: Int, type: QuestionType) {
        _state.update { state ->
            val sections = state.quiz.sections.toMutableList()
            val section = sections[sectionIndex]
            val question = Question(type = type, text = "")
            sections[sectionIndex] = section.copy(
                questions = section.questions + question
            )
            state.copy(
                quiz = state.quiz.copy(sections = sections),
                currentQuestionIndex = section.questions.size
            )
        }
    }

    fun updateQuestion(sectionIndex: Int, questionIndex: Int, question: Question) {
        _state.update { state ->
            val sections = state.quiz.sections.toMutableList()
            val section = sections[sectionIndex]
            val questions = section.questions.toMutableList()
            questions[questionIndex] = question
            sections[sectionIndex] = section.copy(questions = questions)
            state.copy(quiz = state.quiz.copy(sections = sections))
        }
    }

    fun removeQuestion(sectionIndex: Int, questionIndex: Int) {
        _state.update { state ->
            val sections = state.quiz.sections.toMutableList()
            val section = sections[sectionIndex]
            val questions = section.questions.toMutableList()
            questions.removeAt(questionIndex)
            sections[sectionIndex] = section.copy(questions = questions)
            state.copy(quiz = state.quiz.copy(sections = sections))
        }
    }

    fun saveQuiz(): String {
        val quiz = _state.value.quiz.copy(updatedAt = System.currentTimeMillis())
        _state.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            if (_state.value.isEditing) {
                app.quizRepository.updateQuiz(quiz)
            } else {
                app.quizRepository.saveQuiz(quiz)
            }
            _state.update { it.copy(isSaving = false, message = "Quiz saved!") }
        }
        return quiz.id
    }

    fun exportQuiz(context: Context, uri: Uri) {
        viewModelScope.launch {
            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                JqzFileFormat.exportQuiz(_state.value.quiz, outputStream)
                outputStream.close()
                _state.update { it.copy(message = "Quiz exported successfully!") }
            }
        }
    }

    fun importQuiz(context: Context, uri: Uri) {
        viewModelScope.launch {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
            val result = JqzFileFormat.importQuiz(inputStream)
            inputStream.close()
            result.onSuccess { quiz ->
                _state.update { it.copy(quiz = quiz, message = "Quiz imported successfully!") }
            }.onFailure { error ->
                _state.update { it.copy(message = "Import failed: ${error.message}") }
            }
        }
    }

    fun clearMessage() {
        _state.update { it.copy(message = null) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizBuilderScreen(
    editQuizId: String?,
    onNavigateBack: () -> Unit,
    onQuizSaved: (String) -> Unit,
    viewModel: BuilderViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let { viewModel.exportQuiz(context, it) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { viewModel.importQuiz(context, it) }
    }

    LaunchedEffect(editQuizId) {
        viewModel.loadQuiz(editQuizId)
    }

    // Show snackbar for messages
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Quiz" else "Create Quiz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        importLauncher.launch(arrayOf("*/*"))
                    }) {
                        Icon(Icons.Default.FileOpen, contentDescription = "Import")
                    }
                    IconButton(onClick = {
                        val title = state.quiz.title.ifBlank { "quiz" }
                        exportLauncher.launch("$title.jqz")
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export")
                    }
                    IconButton(onClick = {
                        val quizId = viewModel.saveQuiz()
                        onQuizSaved(quizId)
                    }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Quiz metadata
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Quiz Details",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = state.quiz.title,
                            onValueChange = { viewModel.updateQuizTitle(it) },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = state.quiz.description,
                            onValueChange = { viewModel.updateQuizDescription(it) },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = true,
                                onClick = { showCategoryDialog = true },
                                label = { Text(state.quiz.category.displayName) },
                                leadingIcon = {
                                    Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                            FilterChip(
                                selected = true,
                                onClick = {
                                    val next = Difficulty.entries.let { entries ->
                                        val idx = entries.indexOf(state.quiz.difficulty)
                                        entries[(idx + 1) % entries.size]
                                    }
                                    viewModel.updateQuizDifficulty(next)
                                },
                                label = { Text(state.quiz.difficulty.name) }
                            )
                        }
                    }
                }
            }

            // Sections
            state.quiz.sections.forEachIndexed { sIdx, section ->
                item(key = "section_header_$sIdx") {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = section.title,
                                    onValueChange = { viewModel.updateSectionTitle(sIdx, it) },
                                    label = { Text("Section Title") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                IconButton(onClick = {
                                    showAddQuestionDialog = true
                                }) {
                                    Icon(Icons.Default.AddCircle, contentDescription = "Add Question")
                                }
                            }

                            // Questions in this section
                            section.questions.forEachIndexed { qIdx, question ->
                                Spacer(modifier = Modifier.height(8.dp))
                                QuestionEditorCard(
                                    question = question,
                                    index = qIdx,
                                    onUpdate = { updated ->
                                        viewModel.updateQuestion(sIdx, qIdx, updated)
                                    },
                                    onRemove = {
                                        viewModel.removeQuestion(sIdx, qIdx)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Add section button
            item {
                OutlinedButton(
                    onClick = { viewModel.addSection() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Section")
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Add question type dialog
    if (showAddQuestionDialog) {
        AlertDialog(
            onDismissRequest = { showAddQuestionDialog = false },
            title = { Text("Select Question Type") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    QuestionType.entries.forEach { type ->
                        TextButton(
                            onClick = {
                                viewModel.addQuestion(state.currentSectionIndex, type)
                                showAddQuestionDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = type.name.replace("_", " "),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddQuestionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Category dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = { Text("Select Category") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    QuizCategory.entries.forEach { cat ->
                        TextButton(
                            onClick = {
                                viewModel.updateQuizCategory(cat)
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(cat.displayName, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuestionEditorCard(
    question: Question,
    index: Int,
    onUpdate: (Question) -> Unit,
    onRemove: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Q${index + 1}: ${question.type.name.replace("_", " ")}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    IconButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onRemove,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            OutlinedTextField(
                value = question.text,
                onValueChange = { onUpdate(question.copy(text = it)) },
                label = { Text("Question text") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                // Options for multiple choice
                if (question.type == QuestionType.MULTIPLE_CHOICE ||
                    question.type == QuestionType.MULTIPLE_CHOICE_IMAGE
                ) {
                    question.options.forEachIndexed { oIdx, option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = option.isCorrect,
                                onCheckedChange = { isCorrect ->
                                    val updatedOptions = question.options.toMutableList()
                                    updatedOptions[oIdx] = option.copy(isCorrect = isCorrect)
                                    onUpdate(question.copy(options = updatedOptions))
                                }
                            )
                            OutlinedTextField(
                                value = option.text,
                                onValueChange = { text ->
                                    val updatedOptions = question.options.toMutableList()
                                    updatedOptions[oIdx] = option.copy(text = text)
                                    onUpdate(question.copy(options = updatedOptions))
                                },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                label = { Text("Option ${oIdx + 1}") }
                            )
                        }
                    }
                    TextButton(onClick = {
                        val newOption = QuestionOption(text = "")
                        onUpdate(question.copy(options = question.options + newOption))
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Text(" Add option")
                    }
                }

                // Explanation
                OutlinedTextField(
                    value = question.explanation ?: "",
                    onValueChange = { onUpdate(question.copy(explanation = it.ifBlank { null })) },
                    label = { Text("Explanation (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Hint
                OutlinedTextField(
                    value = question.hint ?: "",
                    onValueChange = { onUpdate(question.copy(hint = it.ifBlank { null })) },
                    label = { Text("Hint (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Points
                OutlinedTextField(
                    value = question.points.toString(),
                    onValueChange = {
                        val points = it.toIntOrNull() ?: 1
                        onUpdate(question.copy(points = points))
                    },
                    label = { Text("Points") },
                    modifier = Modifier.width(100.dp),
                    singleLine = true
                )
            }
        }
    }
}
