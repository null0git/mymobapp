package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchingCard(
    question: Question.Matching,
    selectedAnswer: UserAnswer.MatchingAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val matchMap = remember(question.id) {
        mutableStateMapOf<Int, Int>().apply {
            selectedAnswer?.pairs?.forEach { (k, v) -> put(k, v) }
        }
    }
    val rightOptions = remember(question.id) { question.pairs.mapIndexed { i, p -> i to p.right } }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Match each item on the left with the correct item on the right",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))

        question.pairs.forEachIndexed { leftIdx, pair ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = pair.left, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))

                    var expanded by remember { mutableStateOf(false) }
                    val selectedIdx = matchMap[leftIdx]
                    val selectedText = selectedIdx?.let { rightOptions.getOrNull(it)?.second } ?: "Select match..."

                    ExposedDropdownMenuBox(
                        expanded = expanded && !showCorrect,
                        onExpandedChange = { if (!showCorrect) expanded = it },
                    ) {
                        OutlinedTextField(
                            value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        )
                        ExposedDropdownMenu(
                            expanded = expanded && !showCorrect,
                            onDismissRequest = { expanded = false },
                        ) {
                            rightOptions.forEach { (rightIdx, text) ->
                                DropdownMenuItem(
                                    text = { Text(text) },
                                    onClick = {
                                        matchMap[leftIdx] = rightIdx
                                        expanded = false
                                        onAnswer(UserAnswer.MatchingAnswer(matchMap.toMap()))
                                    },
                                )
                            }
                        }
                    }

                    if (showCorrect) {
                        Text(
                            text = "Correct: ${pair.right}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}
