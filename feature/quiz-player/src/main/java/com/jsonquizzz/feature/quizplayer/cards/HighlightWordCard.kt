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
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HighlightWordCard(
    question: Question.HighlightWord,
    selectedAnswer: UserAnswer.HighlightAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = remember(question.id) {
        mutableStateListOf<String>().apply {
            selectedAnswer?.words?.let { addAll(it) }
        }
    }
    val words = remember(question.id) { question.text.split(" ") }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Tap the words to highlight them",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            words.forEach { word ->
                val isSelected = word in selected
                val isCorrect = showCorrect && word in question.correctHighlights
                val bg = when {
                    isCorrect -> MaterialTheme.colorScheme.primaryContainer
                    isSelected -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
                Text(
                    text = word,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(bg)
                        .clickable(enabled = !showCorrect) {
                            if (isSelected) selected.remove(word) else selected.add(word)
                            onAnswer(UserAnswer.HighlightAnswer(selected.toSet()))
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
        }
    }
}
