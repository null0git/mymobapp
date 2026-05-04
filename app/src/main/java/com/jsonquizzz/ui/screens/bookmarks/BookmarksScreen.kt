package com.jsonquizzz.ui.screens.bookmarks

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsonquizzz.JsonQuizzzApp
import com.jsonquizzz.domain.model.Bookmark
import com.jsonquizzz.ui.components.EmptyState
import kotlinx.coroutines.flow.*

class BookmarksViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as JsonQuizzzApp
    val bookmarks: StateFlow<List<Bookmark>> = app.quizRepository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuiz: (String, String) -> Unit,
    viewModel: BookmarksViewModel = viewModel()
) {
    val bookmarks by viewModel.bookmarks.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bookmarks") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (bookmarks.isEmpty()) {
            EmptyState(
                icon = {
                    Icon(
                        Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                title = "No bookmarks yet",
                subtitle = "Bookmark questions during quizzes to review later",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bookmarks, key = { it.id }) { bookmark ->
                    Card(
                        onClick = {
                            onNavigateToQuiz(bookmark.quizId, "REVIEW")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = bookmark.quizTitle,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Icon(
                                    Icons.Default.Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = bookmark.questionText,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 3
                            )
                            bookmark.note?.let { note ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
