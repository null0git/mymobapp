package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
fun MultiPartCard(
    question: Question.MultiPart,
    selectedAnswer: UserAnswer.SubQuestionsAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val subAnswers = remember(question.id) {
        mutableStateMapOf<String, UserAnswer>().apply {
            selectedAnswer?.answers?.forEach { (k, v) -> put(k, v) }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))

        question.parts.forEachIndexed { idx, part ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Part ${idx + 1}: ${part.question}", style = MaterialTheme.typography.bodyLarge)

                    if (part.options.isNotEmpty()) {
                        part.options.forEachIndexed { optIdx, option ->
                            val subAnswer = subAnswers[part.id] as? UserAnswer.SingleChoice
                            val isSelected = subAnswer?.index == optIdx
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !showCorrect) {
                                        subAnswers[part.id] = UserAnswer.SingleChoice(optIdx)
                                        onAnswer(UserAnswer.SubQuestionsAnswer(subAnswers.toMap()))
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                                    else MaterialTheme.colorScheme.surface,
                                ),
                            ) {
                                Text(
                                    text = option,
                                    modifier = Modifier.padding(12.dp),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    } else {
                        val textAnswer = subAnswers[part.id] as? UserAnswer.TextInput
                        OutlinedTextField(
                            value = textAnswer?.text ?: "",
                            onValueChange = { input ->
                                subAnswers[part.id] = UserAnswer.TextInput(input)
                                onAnswer(UserAnswer.SubQuestionsAnswer(subAnswers.toMap()))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Your answer") },
                            enabled = !showCorrect,
                        )
                    }
                }
            }
        }
    }
}
