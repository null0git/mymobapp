package com.jsonquizzz.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsonquizzz.data.local.QuizEntity
import com.jsonquizzz.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    val allQuizzes: StateFlow<List<QuizEntity>> = quizRepository.getAllQuizzes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<QuizEntity>> = quizRepository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val displayedQuizzes: StateFlow<List<QuizEntity>> = combine(
        _searchQuery,
        _showFavoritesOnly,
        allQuizzes,
        favorites,
    ) { query, favOnly, all, favs ->
        when {
            query.isNotBlank() -> all.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
            }
            favOnly -> favs
            else -> all
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setShowFavoritesOnly(show: Boolean) {
        _showFavoritesOnly.value = show
    }

    fun toggleFavorite(quizId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            quizRepository.toggleFavorite(quizId, isFavorite)
        }
    }

    fun deleteQuiz(quizId: String) {
        viewModelScope.launch {
            quizRepository.deleteQuiz(quizId)
        }
    }
}
