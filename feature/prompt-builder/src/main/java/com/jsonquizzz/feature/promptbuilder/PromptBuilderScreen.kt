package com.jsonquizzz.feature.promptbuilder

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jsonquizzz.core.designsystem.component.JsonQuizzzCard

private val questionTypes = listOf(
    "multiple_choice_single", "multiple_choice_multiple", "true_false",
    "numeric", "short_answer", "fill_in_blank", "matching",
    "ordering", "word_bank", "error_identification",
    "reading_comprehension", "highlight_word",
)

private val bloomLevels = listOf(
    "Remember", "Understand", "Apply", "Analyze", "Evaluate", "Create",
)

private val difficulties = listOf("Easy", "Medium", "Hard", "Mixed")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PromptBuilderScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var topic by remember { mutableStateOf("") }
    var questionCount by remember { mutableFloatStateOf(10f) }
    var selectedDifficulty by remember { mutableStateOf("Mixed") }
    val selectedTypes = remember { mutableStateListOf<String>().apply { addAll(questionTypes.take(5)) } }
    val selectedBlooms = remember { mutableStateListOf<String>().apply { addAll(listOf("Understand", "Apply")) } }
    var includeMath by remember { mutableStateOf(false) }
    var includeCode by remember { mutableStateOf(false) }
    var includeGraphs by remember { mutableStateOf(false) }

    fun buildPrompt(): String = buildString {
        appendLine("Generate a JSON quiz for JsonQuizzz with the following specifications:")
        appendLine()
        appendLine("Topic: ${topic.ifBlank { "[YOUR TOPIC]" }}")
        appendLine("Number of questions: ${questionCount.toInt()}")
        appendLine("Difficulty: $selectedDifficulty")
        appendLine("Question types: ${selectedTypes.joinToString(", ")}")
        appendLine("Bloom's Taxonomy levels: ${selectedBlooms.joinToString(", ")}")
        if (includeMath) appendLine("Include math/LaTeX expressions using ${'$'}...${'$'} notation")
        if (includeCode) appendLine("Include code snippets in fenced code blocks")
        if (includeGraphs) appendLine("Include graph questions with function expressions")
        appendLine()
        appendLine("Output format: Valid JSON matching this schema:")
        appendLine("""
{
  "title": "Quiz Title",
  "description": "Quiz description",
  "sections": [{
    "title": "Section Title",
    "questions": [{
      "type": "question_type",
      "id": "unique_id",
      "question": "Question text",
      "points": 1,
      "hint": "Optional hint",
      "explanation": "Explanation shown after answering",
      ...type-specific fields
    }]
  }],
  "settings": {
    "shuffleQuestions": false,
    "showFeedback": true,
    "passingScore": 70
  }
}""".trimIndent())
        appendLine()
        appendLine("Ensure all questions have hints and explanations. Use varied question types.")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Prompt Builder") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Build an LLM prompt to generate quiz JSON",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Topic / Subject") },
                placeholder = { Text("e.g., Organic Chemistry, World War II, Python Basics") },
            )

            Text("Questions: ${questionCount.toInt()}", style = MaterialTheme.typography.titleSmall)
            Slider(
                value = questionCount,
                onValueChange = { questionCount = it },
                valueRange = 5f..50f,
                steps = 8,
            )

            Text("Difficulty", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                difficulties.forEach { diff ->
                    FilterChip(
                        selected = selectedDifficulty == diff,
                        onClick = { selectedDifficulty = diff },
                        label = { Text(diff) },
                    )
                }
            }

            Text("Question Types", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                questionTypes.forEach { type ->
                    val label = type.replace("_", " ").replaceFirstChar { it.uppercase() }
                    FilterChip(
                        selected = type in selectedTypes,
                        onClick = {
                            if (type in selectedTypes) selectedTypes.remove(type) else selectedTypes.add(type)
                        },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                    )
                }
            }

            Text("Bloom's Taxonomy", style = MaterialTheme.typography.titleSmall)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                bloomLevels.forEach { level ->
                    FilterChip(
                        selected = level in selectedBlooms,
                        onClick = {
                            if (level in selectedBlooms) selectedBlooms.remove(level) else selectedBlooms.add(level)
                        },
                        label = { Text(level) },
                    )
                }
            }

            Text("Advanced Options", style = MaterialTheme.typography.titleSmall)
            ToggleRow("Math / LaTeX", includeMath) { includeMath = it }
            ToggleRow("Code Snippets", includeCode) { includeCode = it }
            ToggleRow("Graph Questions", includeGraphs) { includeGraphs = it }

            Spacer(modifier = Modifier.height(8.dp))

            // Preview
            JsonQuizzzCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Prompt Preview", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = buildPrompt(),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 8,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Prompt", buildPrompt()))
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy")
                }
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, buildPrompt())
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share prompt"))
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share")
                }
            }
        }
    }
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
