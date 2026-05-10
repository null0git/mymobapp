package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@Composable
fun NumericCard(
    question: Question.Numeric,
    selectedAnswer: UserAnswer.NumericInput?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    var textValue by remember(question.id) {
        mutableStateOf(selectedAnswer?.value?.toString() ?: "")
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        if (question.tolerance > 0) {
            Text(
                text = "Tolerance: ±${question.tolerance}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = textValue,
            onValueChange = { input ->
                textValue = input
                input.toDoubleOrNull()?.let { onAnswer(UserAnswer.NumericInput(it)) }
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Your answer") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            enabled = !showCorrect,
            singleLine = true,
        )

        if (showCorrect) {
            Text(
                text = "Correct answer: ${question.correctAnswer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
