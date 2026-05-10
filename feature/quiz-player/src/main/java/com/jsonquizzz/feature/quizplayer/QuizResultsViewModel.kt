package com.jsonquizzz.feature.quizplayer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsonquizzz.data.local.QuizResultEntity
import com.jsonquizzz.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizResultsViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
) : ViewModel() {

    private val _latestResult = MutableStateFlow<QuizResultEntity?>(null)
    val latestResult: StateFlow<QuizResultEntity?> = _latestResult.asStateFlow()

    fun loadLatestResult(quizId: String) {
        viewModelScope.launch {
            quizRepository.getRecentResults(50).collect { results ->
                _latestResult.value = results.firstOrNull { it.quizId == quizId }
                    ?: results.firstOrNull()
            }
        }
    }
}
