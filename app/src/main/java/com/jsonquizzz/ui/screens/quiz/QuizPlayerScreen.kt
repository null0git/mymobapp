package com.jsonquizzz.ui.screens.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsonquizzz.domain.model.*
import com.jsonquizzz.ui.components.*
import com.jsonquizzz.ui.theme.CorrectGreen
import com.jsonquizzz.ui.theme.IncorrectRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayerScreen(
    quizId: String,
    mode: String,
    onNavigateToResult: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNavPanel by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    LaunchedEffect(quizId, mode) {
        viewModel.loadQuiz(quizId, mode)
    }

    // Local answer state
    var selectedOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    var textInput by remember { mutableStateOf("") }
    var numericInput by remember { mutableStateOf("") }
    var blankInputs by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Reset local state on question change
    LaunchedEffect(uiState.currentQuestionIndex) {
        val existing = uiState.currentAnswer
        selectedOptions = existing?.selectedOptions ?: emptyList()
        textInput = existing?.textInput ?: ""
        numericInput = existing?.numericInput?.toString() ?: ""
        blankInputs = existing?.blankInputs ?: emptyMap()
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val question = uiState.currentQuestion ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.quiz?.title ?: "",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "${uiState.mode.displayName} • ${uiState.currentQuestionIndex + 1}/${uiState.totalQuestions}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Exit")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleBookmark() }) {
                        Icon(
                            imageVector = if (uiState.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (uiState.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { showNavPanel = !showNavPanel }) {
                        Icon(Icons.Default.GridView, contentDescription = "Navigation")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { viewModel.previousQuestion() },
                        enabled = uiState.currentQuestionIndex > 0
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    if (uiState.currentQuestionIndex == uiState.totalQuestions - 1) {
                        Button(onClick = { showFinishDialog = true }) {
                            Text("Finish")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    } else {
                        val hasAnswered = uiState.attempt?.answers?.containsKey(question.id) == true
                        Button(
                            onClick = {
                                if (!hasAnswered && !uiState.showFeedback) {
                                    val answer = buildAnswer(question, selectedOptions, textInput, numericInput, blankInputs)
                                    viewModel.submitAnswer(answer)
                                    if (uiState.mode == QuizMode.EXAM) {
                                        viewModel.nextQuestion()
                                    }
                                } else {
                                    viewModel.nextQuestion()
                                }
                            }
                        ) {
                            Text(if (hasAnswered || uiState.showFeedback) "Next" else "Submit")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Progress bar
            LinearProgressIndicator(
                progress = uiState.progress,
                modifier = Modifier.fillMaxWidth(),
            )

            // Navigation panel (collapsible)
            AnimatedVisibility(visible = showNavPanel) {
                QuestionNavPanel(
                    totalQuestions = uiState.totalQuestions,
                    currentIndex = uiState.currentQuestionIndex,
                    answeredQuestions = uiState.attempt?.answers?.keys ?: emptySet(),
                    allQuestions = uiState.quiz?.allQuestions ?: emptyList(),
                    onJumpTo = { viewModel.jumpToQuestion(it); showNavPanel = false }
                )
            }

            // Question content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Question number and points
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question ${uiState.currentQuestionIndex + 1}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("${question.points} pts") },
                        leadingIcon = {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    )
                }

                // Question text
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.titleMedium
                )

                // Render special content
                question.codeSnippet?.let { CodeBlock(code = it.code, language = it.language) }
                question.mathExpression?.let { MathBlock(expression = it) }
                question.chemicalData?.let { ChemistryBlock(data = it) }

                // Hint
                if (uiState.mode == QuizMode.PRACTICE && question.hint != null) {
                    HintCard(hint = question.hint!!)
                }

                // Answer input based on question type
                val hasAnswered = uiState.attempt?.answers?.containsKey(question.id) == true
                val showFb = uiState.showFeedback || (hasAnswered && uiState.mode != QuizMode.EXAM)

                when (question.type) {
                    QuestionType.MULTIPLE_CHOICE,
                    QuestionType.MULTIPLE_CHOICE_IMAGE,
                    QuestionType.MATH_RENDERED,
                    QuestionType.CHEMICAL_EQUATION,
                    QuestionType.GRAPH_BASED -> {
                        MultipleChoiceQuestion(
                            question = question,
                            selectedOptions = selectedOptions,
                            onOptionSelected = { optionId ->
                                selectedOptions = listOf(optionId)
                            },
                            showFeedback = showFb,
                            isReviewMode = uiState.mode == QuizMode.REVIEW
                        )
                    }

                    QuestionType.TRUE_FALSE -> {
                        TrueFalseQuestion(
                            selectedAnswer = selectedOptions.firstOrNull() ?: textInput.takeIf { it.isNotBlank() },
                            onAnswerSelected = { answer ->
                                selectedOptions = listOf(answer)
                                textInput = answer
                            },
                            showFeedback = showFb,
                            correctAnswer = question.correctAnswers.firstOrNull()
                        )
                    }

                    QuestionType.NUMERIC_INPUT -> {
                        NumericInputQuestion(
                            value = numericInput,
                            onValueChange = { numericInput = it },
                            showFeedback = showFb,
                            isCorrect = uiState.currentAnswer?.isCorrect == true,
                            correctAnswer = question.correctAnswers.firstOrNull()
                        )
                    }

                    QuestionType.SHORT_ANSWER,
                    QuestionType.CODE_BASED -> {
                        ShortAnswerQuestion(
                            value = textInput,
                            onValueChange = { textInput = it },
                            showFeedback = showFb,
                            isCorrect = uiState.currentAnswer?.isCorrect == true,
                            correctAnswers = question.correctAnswers
                        )
                    }

                    QuestionType.FILL_IN_THE_BLANK -> {
                        FillInTheBlankQuestion(
                            blanks = question.blanks,
                            answers = blankInputs,
                            onAnswerChange = { id, value ->
                                blankInputs = blankInputs + (id to value)
                            },
                            showFeedback = showFb
                        )
                    }

                    QuestionType.MATCHING -> {
                        Text(
                            text = "Match the following:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        question.matchingPairs.forEach { pair ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = pair.left, modifier = Modifier.weight(1f))
                                Text(text = " → ", color = MaterialTheme.colorScheme.primary)
                                Text(text = pair.right, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Explanation (visible in practice mode after answering, always in review)
                if ((showFb || uiState.mode == QuizMode.REVIEW) && question.explanation != null) {
                    ExplanationCard(explanation = question.explanation!!)
                }

                // Feedback indicator
                if (showFb && uiState.currentAnswer != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.currentAnswer!!.isCorrect)
                                CorrectGreen.copy(alpha = 0.1f)
                            else IncorrectRed.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (uiState.currentAnswer!!.isCorrect)
                                    Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (uiState.currentAnswer!!.isCorrect) CorrectGreen else IncorrectRed
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.currentAnswer!!.isCorrect) "Correct!" else "Incorrect",
                                style = MaterialTheme.typography.titleSmall,
                                color = if (uiState.currentAnswer!!.isCorrect) CorrectGreen else IncorrectRed
                            )
                        }
                    }
                }
            }
        }
    }

    // Finish dialog
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish Quiz?") },
            text = {
                val unanswered = uiState.totalQuestions - uiState.answeredCount
                Text(
                    if (unanswered > 0) "You have $unanswered unanswered questions. Are you sure you want to finish?"
                    else "Submit your answers and see your results?"
                )
            },
            confirmButton = {
                Button(onClick = {
                    showFinishDialog = false
                    // Submit current answer if not yet submitted
                    val hasAnswered = uiState.attempt?.answers?.containsKey(question.id) == true
                    if (!hasAnswered) {
                        val answer = buildAnswer(question, selectedOptions, textInput, numericInput, blankInputs)
                        viewModel.submitAnswer(answer)
                    }
                    val attemptId = viewModel.finishQuiz()
                    if (attemptId != null) {
                        onNavigateToResult(attemptId)
                    }
                }) {
                    Text("Finish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Continue")
                }
            }
        )
    }
}

@Composable
private fun QuestionNavPanel(
    totalQuestions: Int,
    currentIndex: Int,
    answeredQuestions: Set<String>,
    allQuestions: List<Question>,
    onJumpTo: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items((0 until totalQuestions).toList()) { index ->
                val questionId = allQuestions.getOrNull(index)?.id ?: ""
                val isAnswered = questionId in answeredQuestions
                val isCurrent = index == currentIndex

                FilterChip(
                    selected = isCurrent,
                    onClick = { onJumpTo(index) },
                    label = { Text("${index + 1}") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    leadingIcon = if (isAnswered && !isCurrent) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else null
                )
            }
        }
    }
}

private fun buildAnswer(
    question: Question,
    selectedOptions: List<String>,
    textInput: String,
    numericInput: String,
    blankInputs: Map<String, String>
): UserAnswer {
    return UserAnswer(
        questionId = question.id,
        selectedOptions = selectedOptions,
        textInput = textInput.takeIf { it.isNotBlank() },
        numericInput = numericInput.toDoubleOrNull(),
        blankInputs = blankInputs,
        points = question.points
    )
}
