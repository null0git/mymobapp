package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@Composable
fun MultipleChoiceMultipleCard(
    question: Question.MultipleChoiceMultiple,
    selectedAnswer: UserAnswer.MultipleChoice?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = selectedAnswer?.indices ?: emptySet()

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Select all that apply",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))

        question.options.forEachIndexed { index, option ->
            val isSelected = index in selected
            val isCorrectOption = showCorrect && index in question.correctAnswers
            val isWrong = showCorrect && isSelected && index !in question.correctAnswers

            val bgColor by animateColorAsState(
                targetValue = when {
                    isCorrectOption -> MaterialTheme.colorScheme.primaryContainer
                    isWrong -> MaterialTheme.colorScheme.errorContainer
                    isSelected -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surface
                },
                label = "optionBg",
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !showCorrect) {
                        val newSet = selected.toMutableSet()
                        if (isSelected) newSet.remove(index) else newSet.add(index)
                        onAnswer(UserAnswer.MultipleChoice(newSet))
                    },
                colors = CardDefaults.cardColors(containerColor = bgColor),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = option, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
