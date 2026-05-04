package com.jsonquizzz.ui.screens.settings

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jsonquizzz.JsonQuizzzApp
import com.jsonquizzz.domain.model.AppSettings
import com.jsonquizzz.domain.model.FontSize
import com.jsonquizzz.domain.model.ThemeColor
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as JsonQuizzzApp

    val settings: StateFlow<AppSettings> = app.settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun updateSettings(settings: AppSettings) {
        viewModelScope.launch { app.settingsRepository.updateSettings(settings) }
    }

    fun toggleDarkMode() {
        viewModelScope.launch { app.settingsRepository.toggleDarkMode() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Appearance
            item {
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                SettingsSwitch(
                    title = "Dark Mode",
                    subtitle = "Use dark color scheme",
                    icon = Icons.Default.DarkMode,
                    checked = settings.isDarkMode,
                    onCheckedChange = { viewModel.toggleDarkMode() }
                )
            }

            item {
                SettingsSwitch(
                    title = "Dynamic Colors",
                    subtitle = "Use Material You colors from wallpaper",
                    icon = Icons.Default.Palette,
                    checked = settings.dynamicColors,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(dynamicColors = it))
                    }
                )
            }

            item {
                var showThemeDialog by remember { mutableStateOf(false) }
                SettingsClickable(
                    title = "Theme Color",
                    subtitle = settings.selectedThemeColor.displayName,
                    icon = Icons.Default.ColorLens,
                    onClick = { showThemeDialog = true }
                )

                if (showThemeDialog) {
                    AlertDialog(
                        onDismissRequest = { showThemeDialog = false },
                        title = { Text("Select Theme") },
                        text = {
                            Column {
                                ThemeColor.entries.forEach { color ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = settings.selectedThemeColor == color,
                                            onClick = {
                                                viewModel.updateSettings(settings.copy(selectedThemeColor = color))
                                                showThemeDialog = false
                                            }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            modifier = Modifier.size(24.dp),
                                            color = Color(color.hexColor),
                                            shape = MaterialTheme.shapes.small
                                        ) {}
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(color.displayName)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showThemeDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }

            item {
                var showFontDialog by remember { mutableStateOf(false) }
                SettingsClickable(
                    title = "Font Size",
                    subtitle = settings.fontSize.displayName,
                    icon = Icons.Default.FormatSize,
                    onClick = { showFontDialog = true }
                )

                if (showFontDialog) {
                    AlertDialog(
                        onDismissRequest = { showFontDialog = false },
                        title = { Text("Font Size") },
                        text = {
                            Column {
                                FontSize.entries.forEach { size ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        RadioButton(
                                            selected = settings.fontSize == size,
                                            onClick = {
                                                viewModel.updateSettings(settings.copy(fontSize = size))
                                                showFontDialog = false
                                            }
                                        )
                                        Text(size.displayName)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showFontDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }

            // Quiz Settings
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Quiz Settings",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                SettingsSwitch(
                    title = "Shuffle Questions",
                    subtitle = "Randomize question order",
                    icon = Icons.Default.Shuffle,
                    checked = settings.shuffleQuestions,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(shuffleQuestions = it))
                    }
                )
            }

            item {
                SettingsSwitch(
                    title = "Shuffle Options",
                    subtitle = "Randomize answer option order",
                    icon = Icons.Default.SwapVert,
                    checked = settings.shuffleOptions,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(shuffleOptions = it))
                    }
                )
            }

            item {
                SettingsSwitch(
                    title = "Enable Hints",
                    subtitle = "Show hints in practice mode",
                    icon = Icons.Default.Lightbulb,
                    checked = settings.enableHints,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(enableHints = it))
                    }
                )
            }

            item {
                SettingsSwitch(
                    title = "Instant Feedback",
                    subtitle = "Show result after each answer",
                    icon = Icons.Default.Feedback,
                    checked = settings.instantFeedback,
                    onCheckedChange = {
                        viewModel.updateSettings(settings.copy(instantFeedback = it))
                    }
                )
            }

            // About
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            item {
                SettingsClickable(
                    title = "JsonQuizzz",
                    subtitle = "Version 1.0.0",
                    icon = Icons.Default.Info,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun SettingsClickable(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
