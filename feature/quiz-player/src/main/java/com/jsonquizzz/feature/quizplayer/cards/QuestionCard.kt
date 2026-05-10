package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@Composable
fun QuestionCard(
    question: Question,
    answer: UserAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (question) {
        is Question.MultipleChoiceSingle -> MultipleChoiceSingleCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.SingleChoice,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.MultipleChoiceMultiple -> MultipleChoiceMultipleCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.MultipleChoice,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.MultipleChoiceImage -> MultipleChoiceImageCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.SingleChoice,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.TrueFalse -> TrueFalseCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.BooleanInput,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.Numeric -> NumericCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.NumericInput,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.ShortAnswer -> ShortAnswerCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.TextInput,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.Matching -> MatchingCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.MatchingAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.FillInBlank -> FillInBlankCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.FillBlanksAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.WordBank -> WordBankCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.WordBankAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.Ordering -> OrderingCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.OrderingAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.ErrorIdentification -> ErrorIdentificationCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.ErrorIdAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.ReadingComprehension -> ReadingComprehensionCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.SubQuestionsAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.HighlightWord -> HighlightWordCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.HighlightAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.Audio -> AudioQuestionCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.SingleChoice,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.Picture -> PictureQuestionCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.SingleChoice,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.Video -> VideoQuestionCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.SingleChoice,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.Crossword -> CrosswordCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.CrosswordAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.MultiPart -> MultiPartCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.SubQuestionsAnswer,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.Graph -> GraphQuestionCard(
            question = question,
            selectedAnswer = answer as? UserAnswer.SingleChoice,
            showCorrect = showCorrect,
            onAnswer = onAnswer,
            modifier = modifier,
        )
        is Question.RawJson -> {
            Column(modifier = modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    text = "Unsupported question type",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = question.question,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
