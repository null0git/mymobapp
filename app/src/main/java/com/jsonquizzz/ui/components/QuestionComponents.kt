package com.jsonquizzz.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.*
import com.jsonquizzz.ui.theme.CorrectGreen
import com.jsonquizzz.ui.theme.IncorrectRed

@Composable
fun MultipleChoiceQuestion(
    question: Question,
    selectedOptions: List<String>,
    onOptionSelected: (String) -> Unit,
    showFeedback: Boolean = false,
    isReviewMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        question.options.forEach { option ->
            val isSelected = option.id in selectedOptions
            val isCorrect = option.isCorrect

            val borderColor = when {
                showFeedback && isCorrect -> CorrectGreen
                showFeedback && isSelected && !isCorrect -> IncorrectRed
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }

            val containerColor = when {
                showFeedback && isCorrect -> CorrectGreen.copy(alpha = 0.08f)
                showFeedback && isSelected && !isCorrect -> IncorrectRed.copy(alpha = 0.08f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else -> Color.Transparent
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
                    .selectable(
                        selected = isSelected,
                        enabled = !showFeedback && !isReviewMode,
                        role = Role.RadioButton,
                        onClick = { onOptionSelected(option.id) }
                    ),
                color = containerColor
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showFeedback || isReviewMode) {
                        Icon(
                            imageVector = when {
                                isCorrect -> Icons.Default.CheckCircle
                                isSelected -> Icons.Default.Cancel
                                else -> Icons.Default.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = when {
                                isCorrect -> CorrectGreen
                                isSelected -> IncorrectRed
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = option.text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TrueFalseQuestion(
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit,
    showFeedback: Boolean = false,
    correctAnswer: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listOf("True", "False").forEach { answer ->
            val isSelected = selectedAnswer == answer
            val isCorrect = correctAnswer?.equals(answer, ignoreCase = true) == true

            val borderColor = when {
                showFeedback && isCorrect -> CorrectGreen
                showFeedback && isSelected && !isCorrect -> IncorrectRed
                isSelected -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.outline
            }

            OutlinedButton(
                onClick = { if (!showFeedback) onAnswerSelected(answer) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else Color.Transparent
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                )
            ) {
                if (showFeedback) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.CheckCircle else if (isSelected) Icons.Default.Cancel else Icons.Default.Remove,
                        contentDescription = null,
                        tint = if (isCorrect) CorrectGreen else if (isSelected) IncorrectRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(text = answer, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun NumericInputQuestion(
    value: String,
    onValueChange: (String) -> Unit,
    showFeedback: Boolean = false,
    isCorrect: Boolean = false,
    correctAnswer: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (!showFeedback) {
                    if (newValue.isEmpty() || newValue.matches(Regex("^-?\\d*\\.?\\d*$"))) {
                        onValueChange(newValue)
                    }
                }
            },
            label = { Text("Enter your answer") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            readOnly = showFeedback,
            isError = showFeedback && !isCorrect,
            trailingIcon = {
                if (showFeedback) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (isCorrect) CorrectGreen else IncorrectRed
                    )
                }
            }
        )
        if (showFeedback && !isCorrect && correctAnswer != null) {
            Text(
                text = "Correct answer: $correctAnswer",
                style = MaterialTheme.typography.bodySmall,
                color = CorrectGreen,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ShortAnswerQuestion(
    value: String,
    onValueChange: (String) -> Unit,
    showFeedback: Boolean = false,
    isCorrect: Boolean = false,
    correctAnswers: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (!showFeedback) onValueChange(it) },
            label = { Text("Type your answer") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = showFeedback,
            isError = showFeedback && !isCorrect,
            trailingIcon = {
                if (showFeedback) {
                    Icon(
                        imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                        contentDescription = null,
                        tint = if (isCorrect) CorrectGreen else IncorrectRed
                    )
                }
            }
        )
        if (showFeedback && !isCorrect && correctAnswers.isNotEmpty()) {
            Text(
                text = "Accepted answers: ${correctAnswers.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall,
                color = CorrectGreen,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun FillInTheBlankQuestion(
    blanks: List<BlankSlot>,
    answers: Map<String, String>,
    onAnswerChange: (String, String) -> Unit,
    showFeedback: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        blanks.sortedBy { it.position }.forEach { blank ->
            val currentValue = answers[blank.id] ?: ""
            val isCorrect = blank.acceptedAnswers.any { accepted ->
                if (blank.caseSensitive) accepted.trim() == currentValue.trim()
                else accepted.trim().equals(currentValue.trim(), ignoreCase = true)
            }

            OutlinedTextField(
                value = currentValue,
                onValueChange = { if (!showFeedback) onAnswerChange(blank.id, it) },
                label = { Text("Blank ${blank.position + 1}") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = showFeedback,
                isError = showFeedback && !isCorrect,
                trailingIcon = {
                    if (showFeedback) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) CorrectGreen else IncorrectRed
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun CodeBlock(
    code: String,
    language: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = language.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MathBlock(
    expression: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Functions,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = expression,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun ChemistryBlock(
    data: ChemicalData,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            data.equation?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            data.smiles?.let {
                Text(
                    text = "SMILES: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun HintCard(
    hint: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = { expanded = !expanded },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (expanded) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    text = "Tap to reveal hint",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ExplanationCard(
    explanation: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
