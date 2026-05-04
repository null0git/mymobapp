package com.jsonquizzz.domain.model

enum class QuizMode(val displayName: String, val description: String) {
    PRACTICE(
        displayName = "Practice Mode",
        description = "Instant feedback, hints enabled, explanations visible"
    ),
    EXAM(
        displayName = "Exam Mode",
        description = "No hints, no instant feedback, results shown at the end"
    ),
    REVIEW(
        displayName = "Review Mode",
        description = "All answers visible, explanations always shown"
    )
}
