package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ErrorIdentificationCard(
    question: Question.ErrorIdentification,
    selectedAnswer: UserAnswer.ErrorIdAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = remember(question.id) {
        mutableStateListOf<Int>().apply {
            selectedAnswer?.indices?.let { addAll(it) }
        }
    }
    val words = remember(question.id) { question.text.split(" ") }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Tap the word(s) with errors",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            words.forEachIndexed { idx, word ->
                val isSelected = idx in selected
                val isError = showCorrect && idx in question.errorIndices
                val bg = when {
                    isError -> MaterialTheme.colorScheme.errorContainer
                    isSelected -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
                Text(
                    text = word,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (isSelected) TextDecoration.Underline else TextDecoration.None,
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(bg)
                        .clickable(enabled = !showCorrect) {
                            if (isSelected) selected.remove(idx) else selected.add(idx)
                            onAnswer(UserAnswer.ErrorIdAnswer(selected.toSet()))
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}
