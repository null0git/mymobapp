package com.jsonquizzz.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jsonquizzz.JsonQuizzzApp
import com.jsonquizzz.domain.model.*
import com.jsonquizzz.engine.QuizResult
import com.jsonquizzz.util.SampleQuizzes
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeUiState(
    val quizzes: List<Quiz> = emptyList(),
    val recentResults: List<QuizResult> = emptyList(),
    val totalQuizzes: Int = 0,
    val averageScore: Double = 0.0,
    val bookmarkCount: Int = 0,
    val isLoading: Boolean = true
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as JsonQuizzzApp
    private val quizRepository = app.quizRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            quizRepository.getAllQuizzes().collect { quizzes ->
                val count = quizRepository.getQuizCount()
                val avgScore = quizRepository.getAverageScore()
                val bookmarks = quizRepository.getBookmarkCount()

                _uiState.update {
                    it.copy(
                        quizzes = quizzes,
                        totalQuizzes = count,
                        averageScore = avgScore,
                        bookmarkCount = bookmarks,
                        isLoading = false
                    )
                }
            }
        }
        viewModelScope.launch {
            quizRepository.getRecentResults(5).collect { results ->
                _uiState.update { it.copy(recentResults = results) }
            }
        }
    }

    fun loadSampleQuizzes() {
        viewModelScope.launch {
            SampleQuizzes.getAllSampleQuizzes().forEach { quiz ->
                quizRepository.saveQuiz(quiz)
            }
        }
    }
}
