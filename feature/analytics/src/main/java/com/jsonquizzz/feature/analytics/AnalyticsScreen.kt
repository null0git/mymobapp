package com.jsonquizzz.feature.analytics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jsonquizzz.core.designsystem.component.JsonQuizzzCard
import com.jsonquizzz.data.local.QuizResultEntity
import com.jsonquizzz.data.repository.QuizRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    modifier: Modifier = Modifier,
    quizRepository: QuizRepository? = null,
) {
    val results by (quizRepository?.getRecentResults(50) ?: kotlinx.coroutines.flow.flowOf(emptyList()))
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Analytics") })
        },
        modifier = modifier,
    ) { padding ->
        if (results.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.BarChart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No quiz data yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "Complete a quiz to see your analytics",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Summary stats
            val totalAttempts = results.size
            val avgScore = if (results.isNotEmpty()) results.map { it.percentage }.average() else 0.0
            val bestScore = results.maxOfOrNull { it.percentage } ?: 0.0
            val totalTime = results.sumOf { it.timeTakenSeconds }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    icon = Icons.Default.Quiz,
                    label = "Attempts",
                    value = "$totalAttempts",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Default.Speed,
                    label = "Avg Score",
                    value = "%.0f%%".format(avgScore),
                    modifier = Modifier.weight(1f),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    icon = Icons.Default.EmojiEvents,
                    label = "Best Score",
                    value = "%.0f%%".format(bestScore),
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Default.Timer,
                    label = "Total Time",
                    value = formatDuration(totalTime),
                    modifier = Modifier.weight(1f),
                )
            }

            // Performance chart
            Text(
                text = "Score Over Time",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            PerformanceChart(
                scores = results.map { it.percentage.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f),
            )

            // Recent results
            Text(
                text = "Recent Results",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            results.take(10).forEach { result ->
                JsonQuizzzCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = result.quizTitle.ifEmpty { "Quiz" },
                                style = MaterialTheme.typography.titleSmall,
                            )
                            Text(
                                text = "${result.correctAnswers}/${result.totalQuestions} correct  |  ${formatDuration(result.timeTakenSeconds)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = "%.0f%%".format(result.percentage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (result.percentage >= 70) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    JsonQuizzzCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PerformanceChart(
    scores: List<Float>,
    modifier: Modifier = Modifier,
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant

    JsonQuizzzCard(modifier = modifier) {
        if (scores.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("No data", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                val w = size.width
                val h = size.height
                val maxScore = 100f
                val stepX = if (scores.size > 1) w / (scores.size - 1) else w

                // Grid lines
                for (i in 0..4) {
                    val y = h - (i * h / 4)
                    drawLine(surfaceColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                }

                // Line chart
                if (scores.size > 1) {
                    for (i in 0 until scores.size - 1) {
                        val x1 = i * stepX
                        val y1 = h - (scores[i] / maxScore * h)
                        val x2 = (i + 1) * stepX
                        val y2 = h - (scores[i + 1] / maxScore * h)
                        drawLine(primaryColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 3f, cap = StrokeCap.Round)
                    }
                }

                // Dots
                scores.forEachIndexed { i, score ->
                    val x = i * stepX
                    val y = h - (score / maxScore * h)
                    drawCircle(primaryColor, radius = 5f, center = Offset(x, y))
                }
            }
        }
    }
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return when {
        h > 0 -> "${h}h ${m}m"
        m > 0 -> "${m}m ${s}s"
        else -> "${s}s"
    }
}
