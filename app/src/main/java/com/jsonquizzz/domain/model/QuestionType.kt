package com.jsonquizzz.domain.model

enum class QuestionType {
    MULTIPLE_CHOICE,
    MULTIPLE_CHOICE_IMAGE,
    TRUE_FALSE,
    NUMERIC_INPUT,
    SHORT_ANSWER,
    MATCHING,
    FILL_IN_THE_BLANK,
    GRAPH_BASED,
    CODE_BASED,
    MATH_RENDERED,
    CHEMICAL_EQUATION;

    companion object {
        fun fromString(value: String): QuestionType {
            return entries.find { it.name.equals(value, ignoreCase = true) }
                ?: MULTIPLE_CHOICE
        }
    }
}
