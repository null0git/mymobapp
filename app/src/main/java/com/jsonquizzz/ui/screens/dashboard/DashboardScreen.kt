package com.jsonquizzz.ui.screens.dashboard

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsonquizzz.JsonQuizzzApp
import com.jsonquizzz.engine.QuizResult
import com.jsonquizzz.ui.components.EmptyState
import com.jsonquizzz.ui.components.StatCard
import com.jsonquizzz.ui.theme.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardState(
    val recentResults: List<QuizResult> = emptyList(),
    val totalQuizzes: Int = 0,
    val totalAttempts: Int = 0,
    val averageScore: Double = 0.0,
    val isLoading: Boolean = true
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as JsonQuizzzApp
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            app.quizRepository.getRecentResults(20).collect { results ->
                val totalQuizzes = app.quizRepository.getQuizCount()
                val totalAttempts = app.quizRepository.getResultCount()
                val avgScore = app.quizRepository.getAverageScore()

                _state.update {
                    it.copy(
                        recentResults = results,
                        totalQuizzes = totalQuizzes,
                        totalAttempts = totalAttempts,
                        averageScore = avgScore,
                        isLoading = false
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateBack: () -> Unit,
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            // Stats overview
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Quizzes",
                        value = "${state.totalQuizzes}",
                        icon = {
                            Icon(
                                Icons.Default.Quiz,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Attempts",
                        value = "${state.totalAttempts}",
                        icon = {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Avg Score",
                        value = "${state.averageScore.toInt()}%",
                        icon = {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Recent activity header
            item {
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (state.recentResults.isEmpty() && !state.isLoading) {
                item {
                    EmptyState(
                        icon = {
                            Icon(
                                Icons.Default.BarChart,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        title = "No activity yet",
                        subtitle = "Complete quizzes to see your performance stats"
                    )
                }
            }

            items(state.recentResults) { result ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = result.quizTitle,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "${result.mode.displayName} • ${result.correctCount}/${result.totalQuestions} correct",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            val minutes = result.duration / 60000
                            val seconds = (result.duration % 60000) / 1000
                            Text(
                                text = "${minutes}m ${seconds}s",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${result.percentage.toInt()}%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = when {
                                result.percentage >= 80 -> CorrectGreen
                                result.percentage >= 60 -> WarningOrange
                                else -> IncorrectRed
                            }
                        )
                    }
                }
            }
        }
    }
}
