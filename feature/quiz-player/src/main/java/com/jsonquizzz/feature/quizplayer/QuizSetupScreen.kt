package com.jsonquizzz.feature.quizplayer

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jsonquizzz.core.designsystem.component.JsonQuizzzCard
import com.jsonquizzz.data.repository.QuizRepository
import com.jsonquizzz.domain.model.Quiz
import com.jsonquizzz.domain.parser.QuizParser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSetupScreen(
    quizId: String,
    onStartQuiz: (String, String) -> Unit,
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    quizRepository: QuizRepository? = null,
) {
    var quiz by remember { mutableStateOf<Quiz?>(null) }
    var selectedMode by remember { mutableStateOf("practice") }
    var shuffleQuestions by remember { mutableStateOf(false) }
    var shuffleOptions by remember { mutableStateOf(false) }

    LaunchedEffect(quizId) {
        if (quizRepository != null) {
            val entity = quizRepository.getQuizById(quizId)
            if (entity != null) {
                QuizParser.parse(entity.jsonContent).onSuccess { quiz = it }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Setup") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Quiz info card
            JsonQuizzzCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = quiz?.title ?: "Loading...",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    if (!quiz?.description.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = quiz?.description ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    val totalQ = quiz?.sections?.sumOf { it.questions.size } ?: 0
                    val totalSections = quiz?.sections?.size ?: 0
                    Text(
                        text = "$totalQ questions  |  $totalSections section${if (totalSections != 1) "s" else ""}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    quiz?.settings?.timeLimit?.let { time ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Time limit: ${time / 60}m ${time % 60}s",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }

            // Mode selection
            Text(text = "Select Mode", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { selectedMode = "practice" },
                    modifier = Modifier.weight(1f),
                    colors = if (selectedMode == "practice") ButtonDefaults.buttonColors()
                    else ButtonDefaults.outlinedButtonColors(),
                ) {
                    Icon(Icons.Default.School, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Practice")
                }
                OutlinedButton(
                    onClick = { selectedMode = "test" },
                    modifier = Modifier.weight(1f),
                    colors = if (selectedMode == "test") ButtonDefaults.buttonColors()
                    else ButtonDefaults.outlinedButtonColors(),
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Test")
                }
            }

            // Description of selected mode
            JsonQuizzzCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (selectedMode == "practice") {
                        Text(text = "Practice Mode", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Instant feedback after each answer. Hints and explanations available. You can skip and retry questions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(text = "Test Mode", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            text = "No feedback until the end. Timer is locked. Auto-submit when time expires. Results shown after completion.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Options
            Text(text = "Options", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Shuffle Questions")
                Switch(checked = shuffleQuestions, onCheckedChange = { shuffleQuestions = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Shuffle Options")
                Switch(checked = shuffleOptions, onCheckedChange = { shuffleOptions = it })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onStartQuiz(quizId, selectedMode) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = quiz != null,
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Quiz", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
