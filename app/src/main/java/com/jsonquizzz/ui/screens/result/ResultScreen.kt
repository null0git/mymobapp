package com.jsonquizzz.ui.screens.result

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsonquizzz.JsonQuizzzApp
import com.jsonquizzz.engine.QuestionResult
import com.jsonquizzz.engine.QuizResult
import com.jsonquizzz.ui.components.ExplanationCard
import com.jsonquizzz.ui.theme.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ResultViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as JsonQuizzzApp
    private val _result = MutableStateFlow<QuizResult?>(null)
    val result: StateFlow<QuizResult?> = _result.asStateFlow()

    fun loadResult(attemptId: String) {
        viewModelScope.launch {
            app.quizRepository.getAllResults().collect { results ->
                _result.value = results.find { it.attemptId == attemptId }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    attemptId: String,
    onNavigateHome: () -> Unit,
    onRetryQuiz: (String, String) -> Unit,
    viewModel: ResultViewModel = viewModel()
) {
    val result by viewModel.result.collectAsState()
    var showDetails by remember { mutableStateOf(false) }

    LaunchedEffect(attemptId) {
        viewModel.loadResult(attemptId)
    }

    val quizResult = result
    if (quizResult == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Results") },
                navigationIcon = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = quizResult.quizTitle,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Score percentage
                        Text(
                            text = "${quizResult.percentage.toInt()}%",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${quizResult.earnedPoints}/${quizResult.totalPoints} points",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ResultStatItem(
                                value = "${quizResult.correctCount}",
                                label = "Correct",
                                color = CorrectGreen
                            )
                            ResultStatItem(
                                value = "${quizResult.incorrectCount}",
                                label = "Incorrect",
                                color = IncorrectRed
                            )
                            ResultStatItem(
                                value = "${quizResult.unansweredCount}",
                                label = "Skipped",
                                color = WarningOrange
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Duration
                        val minutes = quizResult.duration / 60000
                        val seconds = (quizResult.duration % 60000) / 1000
                        Text(
                            text = "Time: ${minutes}m ${seconds}s",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Section Breakdown
            if (quizResult.sectionBreakdown.isNotEmpty()) {
                item {
                    Text(
                        text = "Section Breakdown",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(quizResult.sectionBreakdown) { section ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = section.sectionTitle.ifBlank { "General" },
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "${section.correctCount}/${section.totalQuestions} correct",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "${section.percentage.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = if (section.percentage >= 60) CorrectGreen else IncorrectRed
                            )
                        }
                    }
                }
            }

            // Weak Topics
            if (quizResult.weakTopics.isNotEmpty()) {
                item {
                    Text(
                        text = "Areas to Improve",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(quizResult.weakTopics) { topic ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = WarningOrangeLight
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = topic.topic,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = "${(topic.correctRate * 100).toInt()}% correct (${topic.totalQuestions} questions)",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Toggle question details
            item {
                OutlinedButton(
                    onClick = { showDetails = !showDetails },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        if (showDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (showDetails) "Hide Details" else "Show Question Details")
                }
            }

            // Question details
            if (showDetails) {
                items(quizResult.questionResults) { qResult ->
                    QuestionResultCard(result = qResult)
                }
            }

            // Action buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateHome,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Home")
                    }
                    Button(
                        onClick = {
                            onRetryQuiz(quizResult.quizId, quizResult.mode.name)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Retry")
                    }
                }
            }

            // Spacer for FAB
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ResultStatItem(
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun QuestionResultCard(result: QuestionResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isCorrect)
                CorrectGreen.copy(alpha = 0.05f)
            else if (result.isAnswered) IncorrectRed.copy(alpha = 0.05f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when {
                        result.isCorrect -> Icons.Default.CheckCircle
                        result.isAnswered -> Icons.Default.Cancel
                        else -> Icons.Default.Remove
                    },
                    contentDescription = null,
                    tint = when {
                        result.isCorrect -> CorrectGreen
                        result.isAnswered -> IncorrectRed
                        else -> WarningOrange
                    },
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = result.questionText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${result.points}/${result.maxPoints}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (result.explanation != null) {
                Spacer(modifier = Modifier.height(8.dp))
                ExplanationCard(explanation = result.explanation!!)
            }
        }
    }
}
