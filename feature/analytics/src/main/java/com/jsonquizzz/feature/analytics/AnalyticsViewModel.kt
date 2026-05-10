package com.jsonquizzz.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsonquizzz.data.local.QuizResultEntity
import com.jsonquizzz.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    quizRepository: QuizRepository,
) : ViewModel() {

    val results: StateFlow<List<QuizResultEntity>> = quizRepository.getRecentResults(50)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
