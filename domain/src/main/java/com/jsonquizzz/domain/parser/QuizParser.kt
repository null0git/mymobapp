package com.jsonquizzz.domain.parser

import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.Quiz
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

object QuizParser {

    private val questionModule = SerializersModule {
        polymorphic(Question::class) {
            subclass(Question.MultipleChoiceSingle::class)
            subclass(Question.MultipleChoiceMultiple::class)
            subclass(Question.MultipleChoiceImage::class)
            subclass(Question.TrueFalse::class)
            subclass(Question.Numeric::class)
            subclass(Question.ShortAnswer::class)
            subclass(Question.Matching::class)
            subclass(Question.FillInBlank::class)
            subclass(Question.WordBank::class)
            subclass(Question.Ordering::class)
            subclass(Question.ErrorIdentification::class)
            subclass(Question.ReadingComprehension::class)
            subclass(Question.HighlightWord::class)
            subclass(Question.Audio::class)
            subclass(Question.Picture::class)
            subclass(Question.Video::class)
            subclass(Question.Crossword::class)
            subclass(Question.MultiPart::class)
            subclass(Question.Graph::class)
            subclass(Question.RawJson::class)
            defaultDeserializer { Question.RawJson.serializer() }
        }
    }

    val json = Json {
        serializersModule = questionModule
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        encodeDefaults = true
        classDiscriminator = "type"
    }

    fun parse(jsonString: String): Result<Quiz> {
        return try {
            val quiz = json.decodeFromString<Quiz>(jsonString)
            Result.success(quiz)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun validate(quiz: Quiz): List<String> {
        val errors = mutableListOf<String>()
        if (quiz.title.isBlank()) {
            errors.add("Quiz title is required")
        }
        if (quiz.sections.isEmpty()) {
            errors.add("Quiz must have at least one section")
        }
        quiz.sections.forEachIndexed { sIdx, section ->
            if (section.questions.isEmpty()) {
                errors.add("Section ${sIdx + 1} must have at least one question")
            }
        }
        return errors
    }
}
