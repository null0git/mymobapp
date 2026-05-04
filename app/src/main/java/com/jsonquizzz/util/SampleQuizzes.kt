package com.jsonquizzz.util

import com.jsonquizzz.domain.model.*

object SampleQuizzes {

    fun getAllSampleQuizzes(): List<Quiz> = listOf(
        createMathQuiz(),
        createProgrammingQuiz(),
        createScienceQuiz(),
        createGeneralKnowledgeQuiz()
    )

    private fun createMathQuiz() = Quiz(
        title = "Mathematics Fundamentals",
        description = "Test your math knowledge with algebra, geometry, and calculus questions",
        author = "JsonQuizzz",
        category = QuizCategory.MATH,
        difficulty = Difficulty.MEDIUM,
        sections = listOf(
            QuizSection(
                title = "Algebra",
                order = 0,
                questions = listOf(
                    Question(
                        type = QuestionType.MULTIPLE_CHOICE,
                        text = "What is the value of x in the equation 2x + 6 = 14?",
                        options = listOf(
                            QuestionOption(text = "2"),
                            QuestionOption(text = "4", isCorrect = true),
                            QuestionOption(text = "6"),
                            QuestionOption(text = "8")
                        ),
                        correctAnswers = listOf("4"),
                        explanation = "2x + 6 = 14 → 2x = 8 → x = 4",
                        hint = "Subtract 6 from both sides first",
                        points = 1,
                        tags = listOf("algebra", "equations"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.NUMERIC_INPUT,
                        text = "Calculate: 15² - 10²",
                        correctAnswers = listOf("125"),
                        tolerance = 0.0,
                        explanation = "15² = 225, 10² = 100, 225 - 100 = 125",
                        points = 2,
                        tags = listOf("algebra", "arithmetic"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.MATH_RENDERED,
                        text = "Solve for x:",
                        mathExpression = "x² - 5x + 6 = 0",
                        options = listOf(
                            QuestionOption(text = "x = 1, x = 6"),
                            QuestionOption(text = "x = 2, x = 3", isCorrect = true),
                            QuestionOption(text = "x = -2, x = -3"),
                            QuestionOption(text = "x = 0, x = 5")
                        ),
                        correctAnswers = listOf("x = 2, x = 3"),
                        explanation = "Factor: (x-2)(x-3) = 0, so x = 2 or x = 3",
                        hint = "Try factoring the quadratic",
                        points = 3,
                        tags = listOf("algebra", "quadratic"),
                        difficulty = Difficulty.MEDIUM
                    )
                )
            ),
            QuizSection(
                title = "Geometry",
                order = 1,
                questions = listOf(
                    Question(
                        type = QuestionType.MULTIPLE_CHOICE,
                        text = "What is the area of a circle with radius 5?",
                        options = listOf(
                            QuestionOption(text = "25π", isCorrect = true),
                            QuestionOption(text = "10π"),
                            QuestionOption(text = "50π"),
                            QuestionOption(text = "5π")
                        ),
                        correctAnswers = listOf("25π"),
                        explanation = "Area = πr² = π × 5² = 25π",
                        points = 2,
                        tags = listOf("geometry", "circles"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.TRUE_FALSE,
                        text = "The sum of angles in a triangle is always 180°",
                        correctAnswers = listOf("True"),
                        explanation = "In Euclidean geometry, the sum of interior angles of a triangle is always 180°",
                        points = 1,
                        tags = listOf("geometry", "triangles"),
                        difficulty = Difficulty.EASY
                    )
                )
            )
        ),
        tags = listOf("math", "algebra", "geometry")
    )

    private fun createProgrammingQuiz() = Quiz(
        title = "Kotlin Programming Basics",
        description = "Test your Kotlin programming knowledge",
        author = "JsonQuizzz",
        category = QuizCategory.PROGRAMMING,
        difficulty = Difficulty.MEDIUM,
        sections = listOf(
            QuizSection(
                title = "Kotlin Basics",
                order = 0,
                questions = listOf(
                    Question(
                        type = QuestionType.MULTIPLE_CHOICE,
                        text = "Which keyword is used to declare a variable that can be reassigned in Kotlin?",
                        options = listOf(
                            QuestionOption(text = "val"),
                            QuestionOption(text = "var", isCorrect = true),
                            QuestionOption(text = "let"),
                            QuestionOption(text = "const")
                        ),
                        correctAnswers = listOf("var"),
                        explanation = "'var' declares a mutable variable, while 'val' declares an immutable (read-only) variable",
                        hint = "Think about mutability",
                        points = 1,
                        tags = listOf("kotlin", "variables"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.CODE_BASED,
                        text = "What does the following code print?",
                        codeSnippet = CodeSnippet(
                            code = "fun main() {\n    val list = listOf(1, 2, 3, 4, 5)\n    println(list.filter { it % 2 == 0 }.sum())\n}",
                            language = "kotlin"
                        ),
                        correctAnswers = listOf("6"),
                        explanation = "filter keeps even numbers [2, 4], sum() = 2 + 4 = 6",
                        points = 3,
                        tags = listOf("kotlin", "collections"),
                        difficulty = Difficulty.MEDIUM
                    ),
                    Question(
                        type = QuestionType.SHORT_ANSWER,
                        text = "What is the Kotlin keyword used to define a class that cannot be instantiated directly?",
                        correctAnswers = listOf("abstract"),
                        explanation = "The 'abstract' keyword is used to define a class that cannot be instantiated directly",
                        hint = "It's the same keyword used in Java",
                        points = 2,
                        tags = listOf("kotlin", "classes"),
                        difficulty = Difficulty.MEDIUM
                    ),
                    Question(
                        type = QuestionType.FILL_IN_THE_BLANK,
                        text = "Complete the null-safe call: val length = name___?.___length",
                        blanks = listOf(
                            BlankSlot(
                                position = 0,
                                acceptedAnswers = listOf("?", "?."),
                                caseSensitive = true
                            )
                        ),
                        explanation = "The ?. operator is the safe call operator in Kotlin",
                        points = 2,
                        tags = listOf("kotlin", "null-safety"),
                        difficulty = Difficulty.MEDIUM
                    )
                )
            )
        ),
        tags = listOf("programming", "kotlin", "android")
    )

    private fun createScienceQuiz() = Quiz(
        title = "Science: Chemistry & Physics",
        description = "A mix of chemistry and physics questions",
        author = "JsonQuizzz",
        category = QuizCategory.SCIENCE,
        difficulty = Difficulty.MEDIUM,
        sections = listOf(
            QuizSection(
                title = "Chemistry",
                order = 0,
                questions = listOf(
                    Question(
                        type = QuestionType.CHEMICAL_EQUATION,
                        text = "Balance this equation:",
                        chemicalData = ChemicalData(
                            equation = "H₂ + O₂ → H₂O",
                            formulaType = "equation"
                        ),
                        options = listOf(
                            QuestionOption(text = "2H₂ + O₂ → 2H₂O", isCorrect = true),
                            QuestionOption(text = "H₂ + O₂ → H₂O"),
                            QuestionOption(text = "H₂ + 2O₂ → 2H₂O"),
                            QuestionOption(text = "2H₂ + 2O₂ → 2H₂O")
                        ),
                        correctAnswers = listOf("2H₂ + O₂ → 2H₂O"),
                        explanation = "2 hydrogen molecules react with 1 oxygen molecule to form 2 water molecules",
                        points = 2,
                        tags = listOf("chemistry", "equations"),
                        difficulty = Difficulty.MEDIUM
                    ),
                    Question(
                        type = QuestionType.MULTIPLE_CHOICE,
                        text = "What is the atomic number of Carbon?",
                        options = listOf(
                            QuestionOption(text = "4"),
                            QuestionOption(text = "6", isCorrect = true),
                            QuestionOption(text = "8"),
                            QuestionOption(text = "12")
                        ),
                        correctAnswers = listOf("6"),
                        explanation = "Carbon has 6 protons, giving it an atomic number of 6",
                        points = 1,
                        tags = listOf("chemistry", "periodic-table"),
                        difficulty = Difficulty.EASY
                    )
                )
            ),
            QuizSection(
                title = "Physics",
                order = 1,
                questions = listOf(
                    Question(
                        type = QuestionType.NUMERIC_INPUT,
                        text = "What is the speed of light in vacuum (in million m/s)?",
                        correctAnswers = listOf("300"),
                        tolerance = 1.0,
                        explanation = "The speed of light is approximately 3 × 10⁸ m/s = 300 million m/s",
                        points = 2,
                        tags = listOf("physics", "light"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.TRUE_FALSE,
                        text = "Acceleration due to gravity on Earth is approximately 9.8 m/s²",
                        correctAnswers = listOf("True"),
                        explanation = "The standard acceleration due to gravity on Earth's surface is approximately 9.8 m/s²",
                        points = 1,
                        tags = listOf("physics", "gravity"),
                        difficulty = Difficulty.EASY
                    )
                )
            )
        ),
        tags = listOf("science", "chemistry", "physics")
    )

    private fun createGeneralKnowledgeQuiz() = Quiz(
        title = "General Knowledge Challenge",
        description = "Test your general knowledge across various topics",
        author = "JsonQuizzz",
        category = QuizCategory.GENERAL,
        difficulty = Difficulty.EASY,
        sections = listOf(
            QuizSection(
                title = "Mixed Questions",
                order = 0,
                questions = listOf(
                    Question(
                        type = QuestionType.MULTIPLE_CHOICE,
                        text = "What is the largest planet in our solar system?",
                        options = listOf(
                            QuestionOption(text = "Saturn"),
                            QuestionOption(text = "Jupiter", isCorrect = true),
                            QuestionOption(text = "Neptune"),
                            QuestionOption(text = "Uranus")
                        ),
                        correctAnswers = listOf("Jupiter"),
                        explanation = "Jupiter is the largest planet with a diameter of about 139,820 km",
                        points = 1,
                        tags = listOf("space", "planets"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.MULTIPLE_CHOICE,
                        text = "Which language has the most native speakers worldwide?",
                        options = listOf(
                            QuestionOption(text = "English"),
                            QuestionOption(text = "Spanish"),
                            QuestionOption(text = "Mandarin Chinese", isCorrect = true),
                            QuestionOption(text = "Hindi")
                        ),
                        correctAnswers = listOf("Mandarin Chinese"),
                        explanation = "Mandarin Chinese has over 900 million native speakers",
                        points = 1,
                        tags = listOf("languages", "culture"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.TRUE_FALSE,
                        text = "The Great Wall of China is visible from space with the naked eye",
                        correctAnswers = listOf("False"),
                        explanation = "This is a common myth. The Great Wall is not visible from space with the naked eye",
                        points = 1,
                        tags = listOf("myths", "geography"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.SHORT_ANSWER,
                        text = "What is the chemical symbol for Gold?",
                        correctAnswers = listOf("Au"),
                        explanation = "Au comes from the Latin word 'Aurum'",
                        hint = "It comes from Latin",
                        points = 1,
                        tags = listOf("chemistry", "elements"),
                        difficulty = Difficulty.EASY
                    ),
                    Question(
                        type = QuestionType.MATCHING,
                        text = "Match the country with its capital city",
                        matchingPairs = listOf(
                            MatchingPair(left = "France", right = "Paris"),
                            MatchingPair(left = "Japan", right = "Tokyo"),
                            MatchingPair(left = "Brazil", right = "Brasília"),
                            MatchingPair(left = "Australia", right = "Canberra")
                        ),
                        explanation = "France-Paris, Japan-Tokyo, Brazil-Brasília, Australia-Canberra",
                        points = 4,
                        tags = listOf("geography", "capitals"),
                        difficulty = Difficulty.MEDIUM
                    )
                )
            )
        ),
        tags = listOf("general", "trivia")
    )
}
