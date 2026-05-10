package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@Composable
fun OrderingCard(
    question: Question.Ordering,
    selectedAnswer: UserAnswer.OrderingAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOrder = remember(question.id) {
        mutableStateListOf<Int>().apply {
            addAll(selectedAnswer?.order ?: question.items.indices.toList())
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Arrange in the correct order",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))

        currentOrder.forEachIndexed { position, itemIndex ->
            val itemText = question.items.getOrElse(itemIndex) { "" }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${position + 1}.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = itemText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                    )
                    if (!showCorrect) {
                        IconButton(
                            onClick = {
                                if (position > 0) {
                                    val temp = currentOrder[position]
                                    currentOrder[position] = currentOrder[position - 1]
                                    currentOrder[position - 1] = temp
                                    onAnswer(UserAnswer.OrderingAnswer(currentOrder.toList()))
                                }
                            },
                            enabled = position > 0,
                        ) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Move up")
                        }
                        IconButton(
                            onClick = {
                                if (position < currentOrder.size - 1) {
                                    val temp = currentOrder[position]
                                    currentOrder[position] = currentOrder[position + 1]
                                    currentOrder[position + 1] = temp
                                    onAnswer(UserAnswer.OrderingAnswer(currentOrder.toList()))
                                }
                            },
                            enabled = position < currentOrder.size - 1,
                        ) {
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Move down")
                        }
                    }
                }
            }
        }
    }
}
