package com.jsonquizzz.feature.quizplayer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jsonquizzz.data.local.QuizResultEntity
import com.jsonquizzz.data.repository.QuizRepository
import com.jsonquizzz.domain.model.Quiz
import com.jsonquizzz.domain.model.QuizMode
import com.jsonquizzz.domain.model.QuizResult
import com.jsonquizzz.domain.model.QuizState
import com.jsonquizzz.domain.model.ScoringEngine
import com.jsonquizzz.domain.model.QuestionScore
import com.jsonquizzz.domain.model.UserAnswer
import com.jsonquizzz.domain.parser.QuizParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizPlayerViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    private val _result = MutableStateFlow<QuizResult?>(null)
    val result: StateFlow<QuizResult?> = _result.asStateFlow()

    private val _feedbackState = MutableStateFlow<FeedbackState?>(null)
    val feedbackState: StateFlow<FeedbackState?> = _feedbackState.asStateFlow()

    private val _quizLoaded = MutableStateFlow(false)
    val quizLoaded: StateFlow<Boolean> = _quizLoaded.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)
    val loadError: StateFlow<String?> = _loadError.asStateFlow()

    private var timerJob: Job? = null
    private var currentQuizId: String? = null

    fun loadAndStartQuiz(quizId: String, mode: String) {
        if (_quizLoaded.value && currentQuizId == quizId) return
        currentQuizId = quizId

        viewModelScope.launch {
            val entity = quizRepository.getQuizById(quizId)
            if (entity == null) {
                _loadError.value = "Quiz not found"
                return@launch
            }
            val parseResult = QuizParser.parse(entity.jsonContent)
            parseResult.fold(
                onSuccess = { quiz ->
                    val quizMode = if (mode == "test") QuizMode.TEST else QuizMode.PRACTICE
                    startQuiz(quiz, quizMode)
                    quizRepository.recordPlay(quizId)
                    _quizLoaded.value = true
                },
                onFailure = { e ->
                    _loadError.value = "Failed to parse quiz: ${e.message}"
                },
            )
        }
    }

    fun loadQuizForSetup(quizId: String) {
        viewModelScope.launch {
            val entity = quizRepository.getQuizById(quizId)
            if (entity == null) {
                _loadError.value = "Quiz not found"
                return@launch
            }
            QuizParser.parse(entity.jsonContent).fold(
                onSuccess = { quiz ->
                    _state.update { it.copy(quiz = quiz) }
                    _quizLoaded.value = true
                },
                onFailure = { e ->
                    _loadError.value = "Failed to parse quiz: ${e.message}"
                },
            )
        }
    }

    fun startQuiz(quiz: Quiz, mode: QuizMode, shuffleQuestions: Boolean = false, shuffleOptions: Boolean = false) {
        val processedQuiz = if (shuffleQuestions) {
            quiz.copy(sections = quiz.sections.map { section ->
                section.copy(questions = section.questions.shuffled())
            })
        } else quiz

        val timeLimit = quiz.settings.timeLimit
        _state.value = QuizState(
            quiz = processedQuiz,
            mode = mode,
            startTimeMs = System.currentTimeMillis(),
            remainingTimeMs = timeLimit?.let { it * 1000L },
        )
        _result.value = null
        _feedbackState.value = null

        if (timeLimit != null && mode == QuizMode.TEST) {
            startTimer(timeLimit * 1000L)
        }
    }

    private fun startTimer(totalMs: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remaining = totalMs
            while (remaining > 0 && !_state.value.isFinished) {
                delay(1000)
                remaining -= 1000
                _state.update { it.copy(remainingTimeMs = remaining.coerceAtLeast(0)) }
            }
            if (!_state.value.isFinished) {
                finishQuiz()
            }
        }
    }

    fun submitAnswer(questionId: String, answer: UserAnswer) {
        _state.update { state ->
            state.copy(answers = state.answers + (questionId to answer))
        }

        val currentState = _state.value
        if (currentState.mode == QuizMode.PRACTICE && currentState.quiz.settings.showFeedback) {
            val question = currentState.allQuestions.find { it.id == questionId }
            if (question != null) {
                val score = ScoringEngine.scoreQuestion(question, answer)
                _feedbackState.value = FeedbackState(
                    questionId = questionId,
                    isCorrect = score.isCorrect,
                    earnedPoints = score.earnedPoints,
                    maxPoints = score.maxPoints,
                    explanation = question.explanation,
                    hint = question.hint,
                )
            }
        }
    }

    fun clearFeedback() {
        _feedbackState.value = null
    }

    fun nextQuestion() {
        _feedbackState.value = null
        val currentState = _state.value
        val currentSection = currentState.quiz.sections.getOrNull(currentState.currentSectionIndex)
        if (currentSection != null && currentState.currentQuestionIndex < currentSection.questions.size - 1) {
            _state.update { it.copy(currentQuestionIndex = it.currentQuestionIndex + 1) }
        } else if (currentState.currentSectionIndex < currentState.quiz.sections.size - 1) {
            _state.update { it.copy(currentSectionIndex = it.currentSectionIndex + 1, currentQuestionIndex = 0) }
        } else {
            finishQuiz()
        }
    }

    fun previousQuestion() {
        _feedbackState.value = null
        val currentState = _state.value
        if (currentState.currentQuestionIndex > 0) {
            _state.update { it.copy(currentQuestionIndex = it.currentQuestionIndex - 1) }
        } else if (currentState.currentSectionIndex > 0) {
            val prevSectionIdx = currentState.currentSectionIndex - 1
            val prevSection = currentState.quiz.sections[prevSectionIdx]
            _state.update {
                it.copy(
                    currentSectionIndex = prevSectionIdx,
                    currentQuestionIndex = prevSection.questions.size - 1,
                )
            }
        }
    }

    fun jumpToQuestion(flatIndex: Int) {
        _feedbackState.value = null
        var remaining = flatIndex
        for ((sIdx, section) in _state.value.quiz.sections.withIndex()) {
            if (remaining < section.questions.size) {
                _state.update { it.copy(currentSectionIndex = sIdx, currentQuestionIndex = remaining) }
                return
            }
            remaining -= section.questions.size
        }
    }

    fun toggleFlag(questionId: String) {
        _state.update { state ->
            val flags = state.flaggedQuestions.toMutableSet()
            if (flags.contains(questionId)) flags.remove(questionId) else flags.add(questionId)
            state.copy(flaggedQuestions = flags)
        }
    }

    fun skipQuestion() {
        nextQuestion()
    }

    fun finishQuiz() {
        timerJob?.cancel()
        val currentState = _state.value
        _state.update { it.copy(isFinished = true) }

        val timeTaken = (System.currentTimeMillis() - currentState.startTimeMs) / 1000
        val quizResult = ScoringEngine.scoreQuiz(currentState.quiz, currentState.answers)
        val finalResult = quizResult.copy(
            quizId = currentQuizId ?: currentState.quiz.title,
            timeTakenSeconds = timeTaken,
            completedAt = System.currentTimeMillis(),
        )
        _result.value = finalResult

        // Save result to database
        viewModelScope.launch {
            quizRepository.saveResult(
                QuizResultEntity(
                    quizId = currentQuizId ?: "",
                    quizTitle = finalResult.quizTitle,
                    totalQuestions = finalResult.totalQuestions,
                    correctAnswers = finalResult.correctAnswers,
                    totalPoints = finalResult.totalPoints,
                    earnedPoints = finalResult.earnedPoints,
                    percentage = finalResult.percentage,
                    timeTakenSeconds = timeTaken,
                    mode = currentState.mode.name.lowercase(),
                )
            )
        }
    }

    fun getScoreForQuestion(questionId: String): QuestionScore? {
        val state = _state.value
        val question = state.allQuestions.find { it.id == questionId } ?: return null
        val answer = state.answers[questionId] ?: return null
        return ScoringEngine.scoreQuestion(question, answer)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class FeedbackState(
    val questionId: String,
    val isCorrect: Boolean,
    val earnedPoints: Int,
    val maxPoints: Int,
    val explanation: String? = null,
    val hint: String? = null,
)
