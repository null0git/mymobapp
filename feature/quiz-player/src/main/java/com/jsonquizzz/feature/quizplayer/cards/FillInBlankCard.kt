package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@Composable
fun FillInBlankCard(
    question: Question.FillInBlank,
    selectedAnswer: UserAnswer.FillBlanksAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val blanksMap = remember(question.id) {
        mutableStateMapOf<Int, String>().apply {
            selectedAnswer?.answers?.forEach { (k, v) -> put(k, v) }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = question.text, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        question.blanks.forEach { blank ->
            OutlinedTextField(
                value = blanksMap[blank.index] ?: "",
                onValueChange = { input ->
                    blanksMap[blank.index] = input
                    onAnswer(UserAnswer.FillBlanksAnswer(blanksMap.toMap()))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Blank ${blank.index + 1}") },
                enabled = !showCorrect,
                singleLine = true,
            )
            if (showCorrect) {
                Text(
                    text = "Answer: ${blank.answer}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
