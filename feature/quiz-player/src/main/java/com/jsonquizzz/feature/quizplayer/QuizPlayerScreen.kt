package com.jsonquizzz.feature.quizplayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jsonquizzz.domain.model.QuizMode
import com.jsonquizzz.feature.quizplayer.cards.QuestionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizPlayerScreen(
    quizId: String,
    mode: String = "practice",
    onFinish: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: QuizPlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val result by viewModel.result.collectAsState()
    val feedback by viewModel.feedbackState.collectAsState()
    val quizLoaded by viewModel.quizLoaded.collectAsState()
    val loadError by viewModel.loadError.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    LaunchedEffect(quizId, mode) {
        viewModel.loadAndStartQuiz(quizId, mode)
    }

    if (result != null && state.isFinished) {
        LaunchedEffect(Unit) {
            onFinish()
        }
        return
    }

    if (loadError != null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(loadError ?: "Error", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onFinish) { Text("Go Back") }
            }
        }
        return
    }

    val currentQuestion = state.currentQuestion
    if (currentQuestion == null || !quizLoaded) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.quiz.title.ifEmpty { "Quiz" },
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "${state.currentSection?.title ?: ""}  |  ${state.flatQuestionIndex + 1}/${state.totalQuestions}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Exit quiz")
                    }
                },
                actions = {
                    if (state.remainingTimeMs != null) {
                        val secs = (state.remainingTimeMs!! / 1000).toInt()
                        val mins = secs / 60
                        val remainSecs = secs % 60
                        val timeColor = if (secs < 60) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                        Icon(Icons.Default.Timer, contentDescription = null, tint = timeColor)
                        Text(
                            text = "%d:%02d".format(mins, remainSecs),
                            style = MaterialTheme.typography.titleMedium,
                            color = timeColor,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconButton(onClick = { viewModel.toggleFlag(currentQuestion.id) }) {
                        Icon(
                            Icons.Default.Flag,
                            contentDescription = "Flag question",
                            tint = if (currentQuestion.id in state.flaggedQuestions)
                                MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth(),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Text(
                    text = "Question ${state.flatQuestionIndex + 1}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "${currentQuestion.points} point${if (currentQuestion.points != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(12.dp))

                val showCorrectAnswer = feedback != null && state.mode == QuizMode.PRACTICE
                QuestionCard(
                    question = currentQuestion,
                    answer = state.answers[currentQuestion.id],
                    showCorrect = showCorrectAnswer,
                    onAnswer = { answer ->
                        viewModel.submitAnswer(currentQuestion.id, answer)
                    },
                )

                AnimatedVisibility(visible = feedback != null, enter = fadeIn(), exit = fadeOut()) {
                    feedback?.let { fb ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (fb.isCorrect)
                                    MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.errorContainer,
                            ),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        if (fb.isCorrect) Icons.Default.Check else Icons.Default.Close,
                                        contentDescription = null,
                                        tint = if (fb.isCorrect) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.error,
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (fb.isCorrect) "Correct! +${fb.earnedPoints} pts"
                                        else "Incorrect (${fb.earnedPoints}/${fb.maxPoints} pts)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                fb.explanation?.let { explanation ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = explanation,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(visible = showHint && currentQuestion.hint != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        ),
                    ) {
                        Row(modifier = Modifier.padding(12.dp)) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = currentQuestion.hint ?: "", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(
                    onClick = { viewModel.previousQuestion() },
                    enabled = state.flatQuestionIndex > 0,
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Back")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (currentQuestion.hint != null && state.mode == QuizMode.PRACTICE) {
                        OutlinedButton(onClick = { showHint = !showHint }) {
                            Icon(Icons.Default.Lightbulb, contentDescription = "Hint")
                        }
                    }

                    if (state.quiz.settings.allowSkip && state.mode == QuizMode.PRACTICE) {
                        OutlinedButton(onClick = { viewModel.skipQuestion() }) {
                            Icon(Icons.Default.SkipNext, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Skip")
                        }
                    }
                }

                val isLastQuestion = state.flatQuestionIndex >= state.totalQuestions - 1
                if (isLastQuestion) {
                    Button(
                        onClick = { viewModel.finishQuiz() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Finish")
                    }
                } else {
                    Button(onClick = {
                        viewModel.clearFeedback()
                        showHint = false
                        viewModel.nextQuestion()
                    }) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit Quiz?") },
            text = { Text("Your progress will be lost. Are you sure you want to exit?") },
            confirmButton = {
                TextButton(onClick = { onFinish() }) {
                    Text("Exit", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("Continue")
                }
            },
        )
    }
}
