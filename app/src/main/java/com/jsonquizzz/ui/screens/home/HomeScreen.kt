package com.jsonquizzz.ui.screens.home

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsonquizzz.domain.model.QuizMode
import com.jsonquizzz.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToQuiz: (String, String) -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToBuilder: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showModeDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "JsonQuizzz",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToBookmarks) {
                        Icon(Icons.Default.Bookmark, contentDescription = "Bookmarks")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToBuilder,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Create Quiz") }
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
            // Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Quizzes",
                        value = "${uiState.totalQuizzes}",
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
                        title = "Avg Score",
                        value = "${uiState.averageScore.toInt()}%",
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
                    StatCard(
                        title = "Bookmarks",
                        value = "${uiState.bookmarkCount}",
                        icon = {
                            Icon(
                                Icons.Default.Bookmark,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onNavigateToLibrary,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.LibraryBooks, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Library")
                    }
                    FilledTonalButton(
                        onClick = onNavigateToDashboard,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dashboard")
                    }
                }
            }

            // My Quizzes
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Quizzes",
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (uiState.quizzes.size > 3) {
                        TextButton(onClick = onNavigateToLibrary) {
                            Text("See all")
                        }
                    }
                }
            }

            if (uiState.quizzes.isEmpty() && !uiState.isLoading) {
                item {
                    EmptyState(
                        icon = {
                            Icon(
                                Icons.Default.Quiz,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        title = "No quizzes yet",
                        subtitle = "Create your first quiz or load sample quizzes",
                        action = {
                            Button(onClick = { viewModel.loadSampleQuizzes() }) {
                                Text("Load Sample Quizzes")
                            }
                        }
                    )
                }
            }

            items(uiState.quizzes.take(5)) { quiz ->
                QuizCard(
                    quiz = quiz,
                    onClick = { showModeDialog = quiz.id }
                )
            }
        }
    }

    // Quiz Mode Selection Dialog
    showModeDialog?.let { quizId ->
        AlertDialog(
            onDismissRequest = { showModeDialog = null },
            title = { Text("Select Mode") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuizMode.entries.forEach { mode ->
                        OutlinedCard(
                            onClick = {
                                showModeDialog = null
                                onNavigateToQuiz(quizId, mode.name)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = mode.displayName,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = mode.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModeDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
