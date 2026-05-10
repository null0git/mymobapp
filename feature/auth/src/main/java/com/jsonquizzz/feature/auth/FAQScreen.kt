package com.jsonquizzz.feature.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jsonquizzz.core.designsystem.component.JsonQuizzzCard

private val faqItems = listOf(
    "What is JsonQuizzz?" to "JsonQuizzz turns any JSON file into an interactive quiz. It supports 19 question types including multiple choice, fill-in-the-blank, matching, ordering, crossword puzzles, and more.",
    "How do I create a quiz?" to "Tap the Create tab and either paste your JSON directly, import a .json file, or use the Prompt Builder to generate an LLM prompt that creates quiz JSON for you.",
    "What JSON format should I use?" to "Your JSON needs a title, sections array with questions. Each question needs a type, id, question text, and type-specific fields. Use the Prompt Builder to generate the correct format.",
    "What's the difference between Practice and Test mode?" to "Practice mode gives instant feedback with hints and explanations. Test mode locks the timer, hides feedback, and auto-submits when time expires.",
    "Can I use the app offline?" to "Yes! All quizzes are stored locally using Room database. You can play any saved quiz without an internet connection.",
    "How does quiz sharing work?" to "Each shared quiz gets a unique 16-character ID and QR code. Share links expire after 48 hours. Recipients can scan the QR code or use the direct link.",
    "What question types are supported?" to "19 types: Multiple Choice (single/multiple/image), True/False, Numeric, Short Answer, Matching, Fill-in-the-Blank, Word Bank, Ordering, Error Identification, Reading Comprehension, Highlight Word, Audio, Picture, Video, Crossword, Multi-Part, and Graph questions.",
    "How is scoring calculated?" to "Each question has point values. Some types support partial credit (e.g., matching, multi-select). Your final score is the percentage of total points earned.",
    "Does it support math equations?" to "Yes! Use $...$ for inline math and $$...$$ for block equations. The app renders LaTeX expressions natively.",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val expandedItems = remember { mutableStateMapOf<Int, Boolean>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FAQ") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            faqItems.forEachIndexed { index, (question, answer) ->
                val isExpanded = expandedItems[index] == true
                JsonQuizzzCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedItems[index] = !isExpanded },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )
                            Icon(
                                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                            )
                        }
                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = answer,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
