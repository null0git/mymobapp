package com.jsonquizzz.ui.screens.library

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
import com.jsonquizzz.domain.model.*
import com.jsonquizzz.ui.components.DifficultyBadge
import com.jsonquizzz.ui.components.EmptyState
import com.jsonquizzz.ui.components.QuizCard
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LibraryState(
    val quizzes: List<Quiz> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: QuizCategory? = null,
    val selectedDifficulty: Difficulty? = null,
    val isLoading: Boolean = true
)

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as JsonQuizzzApp
    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()

    init {
        loadQuizzes()
    }

    private fun loadQuizzes() {
        viewModelScope.launch {
            app.quizRepository.getAllQuizzes().collect { quizzes ->
                _state.update { it.copy(quizzes = quizzes, isLoading = false) }
            }
        }
    }

    fun search(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.isBlank()) {
            loadQuizzes()
        } else {
            viewModelScope.launch {
                app.quizRepository.searchQuizzes(query).collect { quizzes ->
                    _state.update { it.copy(quizzes = quizzes) }
                }
            }
        }
    }

    fun filterByCategory(category: QuizCategory?) {
        _state.update { it.copy(selectedCategory = category) }
        if (category == null) {
            loadQuizzes()
        } else {
            viewModelScope.launch {
                app.quizRepository.getQuizzesByCategory(category).collect { quizzes ->
                    _state.update { it.copy(quizzes = quizzes) }
                }
            }
        }
    }

    fun filterByDifficulty(difficulty: Difficulty?) {
        _state.update { it.copy(selectedDifficulty = difficulty) }
        if (difficulty == null) {
            loadQuizzes()
        } else {
            viewModelScope.launch {
                app.quizRepository.getQuizzesByDifficulty(difficulty).collect { quizzes ->
                    _state.update { it.copy(quizzes = quizzes) }
                }
            }
        }
    }

    fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            app.quizRepository.deleteQuiz(quizId)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateToQuiz: (String, String) -> Unit,
    onNavigateToBuilder: (String?) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LibraryViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showModeDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Library") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToBuilder(null) }) {
                        Icon(Icons.Default.Add, contentDescription = "Create")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.search(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search quizzes...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.search("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )

            // Category filter chips
            ScrollableTabRow(
                selectedTabIndex = QuizCategory.entries.indexOf(state.selectedCategory).let {
                    if (it < 0) 0 else it + 1
                },
                modifier = Modifier.fillMaxWidth(),
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = state.selectedCategory == null,
                    onClick = { viewModel.filterByCategory(null) },
                    text = { Text("All") }
                )
                QuizCategory.entries.forEach { category ->
                    Tab(
                        selected = state.selectedCategory == category,
                        onClick = {
                            viewModel.filterByCategory(
                                if (state.selectedCategory == category) null else category
                            )
                        },
                        text = { Text(category.displayName) }
                    )
                }
            }

            // Quiz list
            if (state.quizzes.isEmpty() && !state.isLoading) {
                EmptyState(
                    icon = {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    title = "No quizzes found",
                    subtitle = "Try adjusting your search or filters",
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.quizzes, key = { it.id }) { quiz ->
                        QuizCard(
                            quiz = quiz,
                            onClick = { showModeDialog = quiz.id }
                        )
                    }
                }
            }
        }
    }

    // Mode selection dialog
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
                                Text(mode.displayName, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    mode.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showModeDialog = null }) { Text("Cancel") }
            }
        )
    }
}
