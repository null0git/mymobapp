package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@Composable
fun AudioQuestionCard(
    question: Question.Audio,
    selectedAnswer: UserAnswer.SingleChoice?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.AudioFile,
                    contentDescription = "Audio",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Audio: ${question.audioUrl}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        McqOptionsBlock(
            options = question.options,
            selectedIndex = selectedAnswer?.index,
            correctIndex = if (showCorrect) question.correctAnswer else null,
            showCorrect = showCorrect,
            onSelect = { onAnswer(UserAnswer.SingleChoice(it)) },
        )
    }
}

@Composable
fun PictureQuestionCard(
    question: Question.Picture,
    selectedAnswer: UserAnswer.SingleChoice?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Image",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.imageUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        McqOptionsBlock(
            options = question.options,
            selectedIndex = selectedAnswer?.index,
            correctIndex = if (showCorrect) question.correctAnswer else null,
            showCorrect = showCorrect,
            onSelect = { onAnswer(UserAnswer.SingleChoice(it)) },
        )
    }
}

@Composable
fun VideoQuestionCard(
    question: Question.Video,
    selectedAnswer: UserAnswer.SingleChoice?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = "Video",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.videoUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        McqOptionsBlock(
            options = question.options,
            selectedIndex = selectedAnswer?.index,
            correctIndex = if (showCorrect) question.correctAnswer else null,
            showCorrect = showCorrect,
            onSelect = { onAnswer(UserAnswer.SingleChoice(it)) },
        )
    }
}

@Composable
fun MultipleChoiceImageCard(
    question: Question.MultipleChoiceImage,
    selectedAnswer: UserAnswer.SingleChoice?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))

        val labels = question.options.map { it.label }
        McqOptionsBlock(
            options = labels,
            selectedIndex = selectedAnswer?.index,
            correctIndex = if (showCorrect) question.correctAnswer else null,
            showCorrect = showCorrect,
            onSelect = { onAnswer(UserAnswer.SingleChoice(it)) },
        )
    }
}

@Composable
fun GraphQuestionCard(
    question: Question.Graph,
    selectedAnswer: UserAnswer.SingleChoice?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)

        Card(
            modifier = Modifier.fillMaxWidth().aspectRatio(1.5f),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Graph: ${question.functions.joinToString { it.expression }}",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "Range: [${question.xMin}, ${question.xMax}] x [${question.yMin}, ${question.yMax}]",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        McqOptionsBlock(
            options = question.options,
            selectedIndex = selectedAnswer?.index,
            correctIndex = if (showCorrect) question.correctAnswer else null,
            showCorrect = showCorrect,
            onSelect = { onAnswer(UserAnswer.SingleChoice(it)) },
        )
    }
}

@Composable
fun CrosswordCard(
    question: Question.Crossword,
    selectedAnswer: UserAnswer.CrosswordAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val gridAnswers = androidx.compose.runtime.remember(question.id) {
        androidx.compose.runtime.mutableStateMapOf<String, String>().apply {
            selectedAnswer?.grid?.forEach { (k, v) -> put(k, v) }
        }
    }
    val allClues = question.clues.across + question.clues.down

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)

        if (question.clues.across.isNotEmpty()) {
            Text(text = "Across", style = MaterialTheme.typography.titleSmall)
            question.clues.across.forEach { clue ->
                ClueInput(
                    clue = clue,
                    value = gridAnswers[clue.number.toString()] ?: "",
                    showCorrect = showCorrect,
                    onValueChange = { input ->
                        gridAnswers[clue.number.toString()] = input
                        onAnswer(UserAnswer.CrosswordAnswer(gridAnswers.toMap()))
                    },
                )
            }
        }

        if (question.clues.down.isNotEmpty()) {
            Text(text = "Down", style = MaterialTheme.typography.titleSmall)
            question.clues.down.forEach { clue ->
                ClueInput(
                    clue = clue,
                    value = gridAnswers[clue.number.toString()] ?: "",
                    showCorrect = showCorrect,
                    onValueChange = { input ->
                        gridAnswers[clue.number.toString()] = input
                        onAnswer(UserAnswer.CrosswordAnswer(gridAnswers.toMap()))
                    },
                )
            }
        }
    }
}

@Composable
private fun ClueInput(
    clue: com.jsonquizzz.domain.model.CrosswordClue,
    value: String,
    showCorrect: Boolean,
    onValueChange: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = "${clue.number}. ${clue.clue}",
            style = MaterialTheme.typography.bodyMedium,
        )
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = !showCorrect,
            singleLine = true,
            label = { Text("Answer (${clue.answer.length} letters)") },
        )
        if (showCorrect) {
            Text(
                text = "Answer: ${clue.answer}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
internal fun McqOptionsBlock(
    options: List<String>,
    selectedIndex: Int?,
    correctIndex: Int?,
    showCorrect: Boolean,
    onSelect: (Int) -> Unit,
) {
    options.forEachIndexed { index, option ->
        val isSelected = selectedIndex == index
        val isCorrectOpt = showCorrect && index == correctIndex
        val isWrong = showCorrect && isSelected && index != correctIndex

        val bgColor = when {
            isCorrectOpt -> MaterialTheme.colorScheme.primaryContainer
            isWrong -> MaterialTheme.colorScheme.errorContainer
            isSelected -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surface
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .then(
                    if (!showCorrect) Modifier.clickable { onSelect(index) } else Modifier
                ),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            ),
        ) {
            Text(
                text = option,
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
