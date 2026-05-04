package com.jsonquizzz.ui.screens.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jsonquizzz.JsonQuizzzApp
import com.jsonquizzz.domain.model.*
import com.jsonquizzz.engine.QuizEngine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class QuizPlayerState(
    val quiz: Quiz? = null,
    val attempt: QuizAttempt? = null,
    val currentQuestionIndex: Int = 0,
    val showFeedback: Boolean = false,
    val currentAnswer: UserAnswer? = null,
    val isBookmarked: Boolean = false,
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false,
    val mode: QuizMode = QuizMode.PRACTICE
) {
    val currentQuestion: Question?
        get() = quiz?.allQuestions?.getOrNull(currentQuestionIndex)

    val totalQuestions: Int
        get() = quiz?.totalQuestions ?: 0

    val progress: Float
        get() = if (totalQuestions > 0) (currentQuestionIndex + 1).toFloat() / totalQuestions else 0f

    val answeredCount: Int
        get() = attempt?.answers?.size ?: 0
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as JsonQuizzzApp
    private val quizRepository = app.quizRepository

    private val _uiState = MutableStateFlow(QuizPlayerState())
    val uiState: StateFlow<QuizPlayerState> = _uiState.asStateFlow()

    fun loadQuiz(quizId: String, modeName: String) {
        viewModelScope.launch {
            val quiz = quizRepository.getQuizById(quizId) ?: return@launch
            val mode = try { QuizMode.valueOf(modeName) } catch (_: Exception) { QuizMode.PRACTICE }

            val settings = app.settingsRepository.settings.first()
            val preparedQuiz = QuizEngine.prepareQuiz(quiz, settings)

            val existingAttempt = quizRepository.getUnfinishedAttempt(quizId)
            val attempt = existingAttempt ?: QuizAttempt(
                quizId = quizId,
                quizTitle = quiz.title,
                mode = mode
            )

            _uiState.update {
                it.copy(
                    quiz = preparedQuiz,
                    attempt = attempt,
                    currentQuestionIndex = attempt.currentQuestionIndex,
                    mode = mode,
                    isLoading = false,
                    showFeedback = mode == QuizMode.REVIEW
                )
            }

            checkBookmark()
            quizRepository.saveAttempt(attempt)
        }
    }

    fun submitAnswer(answer: UserAnswer) {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val isCorrect = QuizEngine.checkAnswer(question, answer)

        val updatedAnswer = answer.copy(
            questionId = question.id,
            isCorrect = isCorrect,
            points = question.points
        )

        val updatedAttempt = state.attempt?.copy(
            answers = state.attempt.answers + (question.id to updatedAnswer),
            currentQuestionIndex = state.currentQuestionIndex
        ) ?: return

        _uiState.update {
            it.copy(
                attempt = updatedAttempt,
                currentAnswer = updatedAnswer,
                showFeedback = state.mode == QuizMode.PRACTICE || state.mode == QuizMode.REVIEW
            )
        }

        viewModelScope.launch {
            quizRepository.saveAttempt(updatedAttempt)
        }
    }

    fun nextQuestion() {
        val state = _uiState.value
        val nextIndex = state.currentQuestionIndex + 1
        if (nextIndex < state.totalQuestions) {
            val updatedAttempt = state.attempt?.copy(currentQuestionIndex = nextIndex)
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    showFeedback = state.mode == QuizMode.REVIEW,
                    currentAnswer = null,
                    attempt = updatedAttempt
                )
            }
            viewModelScope.launch {
                updatedAttempt?.let { quizRepository.saveAttempt(it) }
                checkBookmark()
            }
        }
    }

    fun previousQuestion() {
        val state = _uiState.value
        val prevIndex = state.currentQuestionIndex - 1
        if (prevIndex >= 0) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = prevIndex,
                    showFeedback = state.mode == QuizMode.REVIEW ||
                            (state.mode == QuizMode.PRACTICE &&
                                    state.attempt?.answers?.containsKey(
                                        state.quiz?.allQuestions?.getOrNull(prevIndex)?.id
                                    ) == true),
                    currentAnswer = state.attempt?.answers?.get(
                        state.quiz?.allQuestions?.getOrNull(prevIndex)?.id
                    )
                )
            }
            viewModelScope.launch { checkBookmark() }
        }
    }

    fun jumpToQuestion(index: Int) {
        val state = _uiState.value
        if (index in 0 until state.totalQuestions) {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = index,
                    showFeedback = state.mode == QuizMode.REVIEW,
                    currentAnswer = state.attempt?.answers?.get(
                        state.quiz?.allQuestions?.getOrNull(index)?.id
                    )
                )
            }
            viewModelScope.launch { checkBookmark() }
        }
    }

    fun finishQuiz(): String? {
        val state = _uiState.value
        val quiz = state.quiz ?: return null
        val attempt = state.attempt ?: return null

        val completedAttempt = attempt.copy(
            completedAt = System.currentTimeMillis(),
            isCompleted = true
        )

        val result = QuizEngine.calculateResults(quiz, completedAttempt)

        viewModelScope.launch {
            quizRepository.saveAttempt(completedAttempt)
            quizRepository.saveResult(result)
        }

        _uiState.update { it.copy(isCompleted = true) }
        return completedAttempt.id
    }

    fun toggleBookmark() {
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val quiz = state.quiz ?: return

        viewModelScope.launch {
            val bookmark = Bookmark(
                questionId = question.id,
                quizId = quiz.id,
                quizTitle = quiz.title,
                questionText = question.text
            )
            quizRepository.toggleBookmark(bookmark)
            checkBookmark()
        }
    }

    private suspend fun checkBookmark() {
        val questionId = _uiState.value.currentQuestion?.id ?: return
        val isBookmarked = quizRepository.isQuestionBookmarked(questionId)
        _uiState.update { it.copy(isBookmarked = isBookmarked) }
    }
}
