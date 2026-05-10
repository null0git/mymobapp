package com.jsonquizzz.feature.quizplayer.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jsonquizzz.domain.model.Question
import com.jsonquizzz.domain.model.UserAnswer

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WordBankCard(
    question: Question.WordBank,
    selectedAnswer: UserAnswer.WordBankAnswer?,
    showCorrect: Boolean,
    onAnswer: (UserAnswer) -> Unit,
    modifier: Modifier = Modifier,
) {
    val placements = remember(question.id) {
        mutableStateMapOf<String, String>().apply {
            selectedAnswer?.placements?.forEach { (k, v) -> put(k, v) }
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = question.question, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = question.text, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Word Bank:", style = MaterialTheme.typography.titleSmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            question.words.forEach { word ->
                val isUsed = word in placements.values
                AssistChip(
                    onClick = {},
                    label = { Text(word) },
                    enabled = !isUsed,
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        question.correctPlacements.keys.forEach { slot ->
            var expanded by remember { mutableStateOf(false) }
            val currentWord = placements[slot] ?: ""

            ExposedDropdownMenuBox(
                expanded = expanded && !showCorrect,
                onExpandedChange = { if (!showCorrect) expanded = it },
            ) {
                OutlinedTextField(
                    value = currentWord.ifEmpty { "Select word for slot: $slot" },
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    label = { Text("Slot: $slot") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                )
                ExposedDropdownMenu(
                    expanded = expanded && !showCorrect,
                    onDismissRequest = { expanded = false },
                ) {
                    question.words.forEach { word ->
                        DropdownMenuItem(
                            text = { Text(word) },
                            onClick = {
                                placements[slot] = word
                                expanded = false
                                onAnswer(UserAnswer.WordBankAnswer(placements.toMap()))
                            },
                        )
                    }
                }
            }

            if (showCorrect) {
                Text(
                    text = "Correct: ${question.correctPlacements[slot]}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
