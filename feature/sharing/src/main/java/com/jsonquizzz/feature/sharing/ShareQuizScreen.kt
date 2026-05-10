package com.jsonquizzz.feature.sharing

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jsonquizzz.core.designsystem.component.JsonQuizzzCard
import java.security.SecureRandom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareQuizScreen(
    quizId: String,
    quizTitle: String = "",
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var shareId by remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(quizId) {
        shareId = generateShareId()
        qrBitmap = generateQrBitmap("https://jsonquizzz.app/q/$shareId")
    }

    val shareUrl = "https://jsonquizzz.app/q/$shareId"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Share Quiz") },
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = quizTitle.ifBlank { "Quiz" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            // QR Code
            JsonQuizzzCard(modifier = Modifier.size(260.dp)) {
                qrBitmap?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                    )
                }
            }

            // Share ID
            JsonQuizzzCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Share ID", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = shareId,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = MaterialTheme.typography.headlineMedium.letterSpacing * 1.5,
                    )
                }
            }

            // Expiry info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Expires in 48 hours",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Share link
            Text(
                text = shareUrl,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Share Link", shareUrl))
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy Link")
                }
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Try this quiz: $quizTitle")
                            putExtra(Intent.EXTRA_TEXT, "Take this quiz on JsonQuizzz!\n$shareUrl")
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Quiz"))
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

private fun generateShareId(): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    val random = SecureRandom()
    return (1..16).map { chars[random.nextInt(chars.length)] }.joinToString("")
}

private fun generateQrBitmap(data: String): Bitmap {
    // Simple QR code generation without ZXing dependency
    // Creates a placeholder pattern. In production, replace with ZXing.
    val size = 512
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    bitmap.eraseColor(Color.WHITE)

    // Draw a simple pattern based on data hash
    val hash = data.hashCode()
    val moduleSize = size / 25
    for (row in 0 until 25) {
        for (col in 0 until 25) {
            // Finder patterns (corners)
            val isFinderTL = row < 7 && col < 7
            val isFinderTR = row < 7 && col >= 18
            val isFinderBL = row >= 18 && col < 7
            val isFinder = isFinderTL || isFinderTR || isFinderBL

            val shouldFill = if (isFinder) {
                // Standard QR finder pattern
                val lr = if (isFinderTL) row else if (isFinderTR) row else row - 18
                val lc = if (isFinderTL) col else if (isFinderTR) col - 18 else col
                (lr == 0 || lr == 6 || lc == 0 || lc == 6) ||
                    (lr in 2..4 && lc in 2..4)
            } else {
                // Data pattern based on hash
                ((hash shr ((row * 25 + col) % 31)) and 1) == 1
            }

            if (shouldFill) {
                for (px in 0 until moduleSize) {
                    for (py in 0 until moduleSize) {
                        val x = col * moduleSize + px
                        val y = row * moduleSize + py
                        if (x < size && y < size) {
                            bitmap.setPixel(x, y, Color.BLACK)
                        }
                    }
                }
            }
        }
    }
    return bitmap
}
