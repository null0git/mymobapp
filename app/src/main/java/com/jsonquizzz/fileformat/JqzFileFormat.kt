package com.jsonquizzz.fileformat

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.jsonquizzz.domain.model.*
import java.io.InputStream
import java.io.OutputStream

/**
 * .jqz (JsonQuizzz) file format handler.
 *
 * File structure is JSON-based with metadata wrapper:
 * {
 *   "format": "jqz",
 *   "schemaVersion": "1.0",
 *   "createdWith": "JsonQuizzz Android",
 *   "exportedAt": <timestamp>,
 *   "quiz": { ... }
 * }
 */
object JqzFileFormat {

    const val FILE_EXTENSION = "jqz"
    const val MIME_TYPE = "application/x-jqz"
    const val CURRENT_SCHEMA_VERSION = "1.0"

    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create()

    fun exportQuiz(quiz: Quiz, outputStream: OutputStream): Result<Unit> {
        return try {
            val wrapper = JqzFileWrapper(
                format = "jqz",
                schemaVersion = CURRENT_SCHEMA_VERSION,
                createdWith = "JsonQuizzz Android",
                exportedAt = System.currentTimeMillis(),
                quiz = quiz.toJqzQuiz()
            )
            val json = gson.toJson(wrapper)
            outputStream.write(json.toByteArray(Charsets.UTF_8))
            outputStream.flush()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(JqzExportException("Failed to export quiz: ${e.message}", e))
        }
    }

    fun importQuiz(inputStream: InputStream): Result<Quiz> {
        return try {
            val json = inputStream.bufferedReader(Charsets.UTF_8).readText()
            val validation = validateJson(json)
            if (validation.isFailure) {
                return Result.failure(validation.exceptionOrNull()!!)
            }
            val wrapper = gson.fromJson(json, JqzFileWrapper::class.java)
            val quiz = wrapper.quiz.toDomainQuiz()
            Result.success(quiz)
        } catch (e: JsonSyntaxException) {
            Result.failure(JqzImportException("Invalid JSON format: ${e.message}", e))
        } catch (e: Exception) {
            Result.failure(JqzImportException("Failed to import quiz: ${e.message}", e))
        }
    }

    fun validateJson(json: String): Result<JqzValidation> {
        return try {
            val wrapper = gson.fromJson(json, JqzFileWrapper::class.java)

            val errors = mutableListOf<String>()
            val warnings = mutableListOf<String>()

            if (wrapper.format != "jqz") {
                errors.add("Invalid format identifier: '${wrapper.format}', expected 'jqz'")
            }

            if (wrapper.schemaVersion.isBlank()) {
                errors.add("Missing schema version")
            }

            if (wrapper.quiz.title.isBlank()) {
                errors.add("Quiz title is required")
            }

            if (wrapper.quiz.sections.isEmpty()) {
                errors.add("Quiz must have at least one section")
            }

            wrapper.quiz.sections.forEachIndexed { sIdx, section ->
                if (section.questions.isEmpty()) {
                    warnings.add("Section ${sIdx + 1} '${section.title}' has no questions")
                }
                section.questions.forEachIndexed { qIdx, question ->
                    if (question.text.isBlank()) {
                        errors.add("Question ${qIdx + 1} in section '${section.title}' has no text")
                    }
                    val qType = QuestionType.fromString(question.type)
                    when (qType) {
                        QuestionType.MULTIPLE_CHOICE, QuestionType.MULTIPLE_CHOICE_IMAGE -> {
                            if (question.options.isEmpty()) {
                                errors.add("Question ${qIdx + 1}: Multiple choice requires options")
                            }
                            if (question.correctAnswers.isEmpty()) {
                                errors.add("Question ${qIdx + 1}: No correct answer specified")
                            }
                        }
                        QuestionType.TRUE_FALSE -> {
                            if (question.correctAnswers.isEmpty()) {
                                errors.add("Question ${qIdx + 1}: True/False requires a correct answer")
                            }
                        }
                        QuestionType.MATCHING -> {
                            if (question.matchingPairs.isEmpty()) {
                                errors.add("Question ${qIdx + 1}: Matching requires pairs")
                            }
                        }
                        QuestionType.FILL_IN_THE_BLANK -> {
                            if (question.blanks.isEmpty()) {
                                errors.add("Question ${qIdx + 1}: Fill-in-the-blank requires blanks")
                            }
                        }
                        else -> { /* Other types have flexible validation */ }
                    }
                }
            }

            if (wrapper.schemaVersion != CURRENT_SCHEMA_VERSION) {
                warnings.add("Schema version '${wrapper.schemaVersion}' differs from current '$CURRENT_SCHEMA_VERSION'")
            }

            val validation = JqzValidation(
                isValid = errors.isEmpty(),
                errors = errors,
                warnings = warnings,
                schemaVersion = wrapper.schemaVersion
            )

            if (errors.isEmpty()) Result.success(validation)
            else Result.failure(JqzValidationException(errors.joinToString("; "), validation))
        } catch (e: JsonSyntaxException) {
            Result.failure(JqzValidationException("Invalid JSON: ${e.message}"))
        }
    }

    fun quizToJson(quiz: Quiz): String {
        val wrapper = JqzFileWrapper(
            format = "jqz",
            schemaVersion = CURRENT_SCHEMA_VERSION,
            createdWith = "JsonQuizzz Android",
            exportedAt = System.currentTimeMillis(),
            quiz = quiz.toJqzQuiz()
        )
        return gson.toJson(wrapper)
    }
}

data class JqzFileWrapper(
    val format: String = "jqz",
    val schemaVersion: String = JqzFileFormat.CURRENT_SCHEMA_VERSION,
    val createdWith: String = "JsonQuizzz Android",
    val exportedAt: Long = System.currentTimeMillis(),
    val quiz: JqzQuiz = JqzQuiz()
)

data class JqzQuiz(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val category: String = "GENERAL",
    val difficulty: String = "MEDIUM",
    val sections: List<JqzSection> = emptyList(),
    val tags: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val version: String = "1.0",
    val timeLimit: Int? = null,
    val shuffleQuestions: Boolean = false,
    val shuffleOptions: Boolean = false,
    val imageUrl: String? = null
)

data class JqzSection(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val questions: List<JqzQuestion> = emptyList(),
    val order: Int = 0
)

data class JqzQuestion(
    val id: String = "",
    val type: String = "MULTIPLE_CHOICE",
    val text: String = "",
    val options: List<JqzOption> = emptyList(),
    val correctAnswers: List<String> = emptyList(),
    val explanation: String? = null,
    val hint: String? = null,
    val imageUrl: String? = null,
    val points: Int = 1,
    val tags: List<String> = emptyList(),
    val difficulty: String = "MEDIUM",
    val tolerance: Double? = null,
    val matchingPairs: List<JqzMatchingPair> = emptyList(),
    val blanks: List<JqzBlank> = emptyList(),
    val codeSnippet: JqzCodeSnippet? = null,
    val mathExpression: String? = null,
    val chemicalData: JqzChemicalData? = null,
    val graphData: JqzGraphData? = null
)

data class JqzOption(
    val id: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val isCorrect: Boolean = false
)

data class JqzMatchingPair(
    val id: String = "",
    val left: String = "",
    val right: String = ""
)

data class JqzBlank(
    val id: String = "",
    val position: Int = 0,
    val acceptedAnswers: List<String> = emptyList(),
    val caseSensitive: Boolean = false
)

data class JqzCodeSnippet(
    val code: String = "",
    val language: String = "kotlin",
    val highlightLines: List<Int> = emptyList()
)

data class JqzChemicalData(
    val equation: String? = null,
    val smiles: String? = null,
    val formulaType: String = "equation"
)

data class JqzGraphData(
    val functions: List<JqzGraphFunction> = emptyList(),
    val xMin: Double = -10.0,
    val xMax: Double = 10.0,
    val yMin: Double = -10.0,
    val yMax: Double = 10.0,
    val gridEnabled: Boolean = true
)

data class JqzGraphFunction(
    val expression: String = "",
    val color: String = "#2196F3",
    val label: String = ""
)

// Conversion extensions
private fun Quiz.toJqzQuiz() = JqzQuiz(
    id = id,
    title = title,
    description = description,
    author = author,
    category = category.name,
    difficulty = difficulty.name,
    sections = sections.map { it.toJqzSection() },
    tags = tags,
    createdAt = createdAt,
    version = version,
    timeLimit = timeLimit,
    shuffleQuestions = shuffleQuestions,
    shuffleOptions = shuffleOptions,
    imageUrl = imageUrl
)

private fun QuizSection.toJqzSection() = JqzSection(
    id = id,
    title = title,
    description = description,
    questions = questions.map { it.toJqzQuestion() },
    order = order
)

private fun Question.toJqzQuestion() = JqzQuestion(
    id = id,
    type = type.name,
    text = text,
    options = options.map { JqzOption(it.id, it.text, it.imageUrl, it.isCorrect) },
    correctAnswers = correctAnswers,
    explanation = explanation,
    hint = hint,
    imageUrl = imageUrl,
    points = points,
    tags = tags,
    difficulty = difficulty.name,
    tolerance = tolerance,
    matchingPairs = matchingPairs.map { JqzMatchingPair(it.id, it.left, it.right) },
    blanks = blanks.map { JqzBlank(it.id, it.position, it.acceptedAnswers, it.caseSensitive) },
    codeSnippet = codeSnippet?.let { JqzCodeSnippet(it.code, it.language, it.highlightLines) },
    mathExpression = mathExpression,
    chemicalData = chemicalData?.let { JqzChemicalData(it.equation, it.smiles, it.formulaType) },
    graphData = graphData?.let {
        JqzGraphData(
            functions = it.functions.map { f -> JqzGraphFunction(f.expression, f.color, f.label) },
            xMin = it.xRange.first, xMax = it.xRange.second,
            yMin = it.yRange.first, yMax = it.yRange.second,
            gridEnabled = it.gridEnabled
        )
    }
)

fun JqzQuiz.toDomainQuiz() = Quiz(
    id = id,
    title = title,
    description = description,
    author = author,
    category = QuizCategory.fromString(category),
    difficulty = Difficulty.fromString(difficulty),
    sections = sections.map { it.toDomainSection() },
    tags = tags,
    createdAt = createdAt,
    version = version,
    timeLimit = timeLimit,
    shuffleQuestions = shuffleQuestions,
    shuffleOptions = shuffleOptions,
    imageUrl = imageUrl
)

private fun JqzSection.toDomainSection() = QuizSection(
    id = id,
    title = title,
    description = description,
    questions = questions.map { it.toDomainQuestion() },
    order = order
)

private fun JqzQuestion.toDomainQuestion() = Question(
    id = id,
    type = QuestionType.fromString(type),
    text = text,
    options = options.map { QuestionOption(it.id, it.text, it.imageUrl, it.isCorrect) },
    correctAnswers = correctAnswers,
    explanation = explanation,
    hint = hint,
    imageUrl = imageUrl,
    points = points,
    tags = tags,
    difficulty = Difficulty.fromString(difficulty),
    tolerance = tolerance,
    matchingPairs = matchingPairs.map { MatchingPair(it.id, it.left, it.right) },
    blanks = blanks.map { BlankSlot(it.id, it.position, it.acceptedAnswers, it.caseSensitive) },
    codeSnippet = codeSnippet?.let { CodeSnippet(it.code, it.language, it.highlightLines) },
    mathExpression = mathExpression,
    chemicalData = chemicalData?.let { ChemicalData(it.equation, it.smiles, it.formulaType) },
    graphData = graphData?.let {
        GraphData(
            functions = it.functions.map { f -> GraphFunction(f.expression, f.color, f.label) },
            xRange = it.xMin to it.xMax,
            yRange = it.yMin to it.yMax,
            gridEnabled = it.gridEnabled
        )
    }
)

data class JqzValidation(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val schemaVersion: String = ""
)

class JqzExportException(message: String, cause: Throwable? = null) : Exception(message, cause)
class JqzImportException(message: String, cause: Throwable? = null) : Exception(message, cause)
class JqzValidationException(
    message: String,
    val validation: JqzValidation? = null
) : Exception(message)
