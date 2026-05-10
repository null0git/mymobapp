package com.jsonquizzz.feature.quizcreate

import androidx.lifecycle.ViewModel
import com.jsonquizzz.data.repository.QuizRepository
import com.jsonquizzz.domain.parser.QuizParser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
) : ViewModel() {

    fun validateJson(jsonText: String): List<String> {
        if (jsonText.isBlank()) return listOf("Please paste your quiz JSON")
        val parseResult = QuizParser.parse(jsonText)
        return parseResult.fold(
            onSuccess = { quiz -> QuizParser.validate(quiz) },
            onFailure = { e -> listOf("Invalid JSON: ${e.message}") },
        )
    }

    suspend fun saveQuiz(jsonContent: String): Result<String> {
        return quizRepository.saveQuiz(jsonContent)
    }
}
