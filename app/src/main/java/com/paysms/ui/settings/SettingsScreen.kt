package com.paysms.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paysms.data.model.AppSettings
import com.paysms.ui.theme.CreditGreen
import com.paysms.ui.theme.DebitRed
import com.paysms.ui.theme.Warning

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Configure your PaySMS app",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        // API Settings
        item {
            SettingsSection(
                title = "API Integration",
                icon = Icons.Filled.Api
            ) {
                SwitchRow("Enable API", state.settings.apiEnabled) {
                    viewModel.updateSettings(state.settings.copy(apiEnabled = it))
                }

                if (state.settings.apiEnabled) {
                    SettingsTextField("API URL", state.settings.apiUrl, "https://your-api.com/endpoint") {
                        viewModel.updateSettings(state.settings.copy(apiUrl = it))
                    }

                    MethodDropdown(state.settings.apiMethod) {
                        viewModel.updateSettings(state.settings.copy(apiMethod = it))
                    }

                    SettingsTextField("API Key", state.settings.apiKey, "your-api-key") {
                        viewModel.updateSettings(state.settings.copy(apiKey = it))
                    }

                    SwitchRow("Auto-send to API", state.settings.autoSendApi) {
                        viewModel.updateSettings(state.settings.copy(autoSendApi = it))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TestConnectionButton(
                        label = "Test API Connection",
                        isLoading = state.apiTestLoading,
                        result = state.apiTestResult,
                        onClick = { viewModel.testApiConnection() }
                    )
                }
            }
        }

        // Email Settings
        item {
            SettingsSection(
                title = "Email Notifications",
                icon = Icons.Filled.Email
            ) {
                SwitchRow("Enable Email", state.settings.emailEnabled) {
                    viewModel.updateSettings(state.settings.copy(emailEnabled = it))
                }

                if (state.settings.emailEnabled) {
                    SettingsTextField("Sender Email", state.settings.senderEmail, "your@gmail.com") {
                        viewModel.updateSettings(state.settings.copy(senderEmail = it))
                    }
                    SettingsPasswordField("App Password", state.settings.senderPassword, "App-specific password") {
                        viewModel.updateSettings(state.settings.copy(senderPassword = it))
                    }
                    SettingsTextField("Receiver Email", state.settings.receiverEmail, "notify@example.com") {
                        viewModel.updateSettings(state.settings.copy(receiverEmail = it))
                    }
                    SettingsTextField("SMTP Host", state.settings.smtpHost, "smtp.gmail.com") {
                        viewModel.updateSettings(state.settings.copy(smtpHost = it))
                    }
                    SettingsNumberField("SMTP Port", state.settings.smtpPort) {
                        viewModel.updateSettings(state.settings.copy(smtpPort = it))
                    }

                    SwitchRow("Auto-send emails", state.settings.autoSendEmail) {
                        viewModel.updateSettings(state.settings.copy(autoSendEmail = it))
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    TestConnectionButton(
                        label = "Send Test Email",
                        isLoading = state.emailTestLoading,
                        result = state.emailTestResult,
                        onClick = { viewModel.testEmailConnection() }
                    )
                }
            }
        }

        // Fraud Prevention - Whitelist/Blacklist
        item {
            SettingsSection(
                title = "Fraud Prevention",
                icon = Icons.Filled.Security
            ) {
                SwitchRow("Enable sender whitelist", state.settings.whitelistEnabled) {
                    viewModel.updateSettings(state.settings.copy(whitelistEnabled = it))
                }

                if (state.settings.whitelistEnabled) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Only SMS from whitelisted senders will be processed",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    SenderListEditor(
                        title = "Whitelisted Senders",
                        senders = state.settings.whitelistedSenders,
                        onAdd = { sender ->
                            viewModel.updateSettings(
                                state.settings.copy(
                                    whitelistedSenders = state.settings.whitelistedSenders + sender
                                )
                            )
                        },
                        onRemove = { sender ->
                            viewModel.updateSettings(
                                state.settings.copy(
                                    whitelistedSenders = state.settings.whitelistedSenders - sender
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Blacklisted senders are always blocked",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                SenderListEditor(
                    title = "Blacklisted Senders",
                    senders = state.settings.blacklistedSenders,
                    onAdd = { sender ->
                        viewModel.updateSettings(
                            state.settings.copy(
                                blacklistedSenders = state.settings.blacklistedSenders + sender
                            )
                        )
                    },
                    onRemove = { sender ->
                        viewModel.updateSettings(
                            state.settings.copy(
                                blacklistedSenders = state.settings.blacklistedSenders - sender
                            )
                        )
                    }
                )
            }
        }

        // SMS Filter
        item {
            SettingsSection(
                title = "SMS Filters",
                icon = Icons.Filled.Sms
            ) {
                SenderListEditor(
                    title = "Allowed Senders",
                    senders = state.settings.allowedSenders,
                    onAdd = { sender ->
                        viewModel.updateSettings(
                            state.settings.copy(
                                allowedSenders = state.settings.allowedSenders + sender
                            )
                        )
                    },
                    onRemove = { sender ->
                        viewModel.updateSettings(
                            state.settings.copy(
                                allowedSenders = state.settings.allowedSenders - sender
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
                SenderListEditor(
                    title = "Keywords",
                    senders = state.settings.keywords,
                    onAdd = { keyword ->
                        viewModel.updateSettings(
                            state.settings.copy(
                                keywords = state.settings.keywords + keyword
                            )
                        )
                    },
                    onRemove = { keyword ->
                        viewModel.updateSettings(
                            state.settings.copy(
                                keywords = state.settings.keywords - keyword
                            )
                        )
                    }
                )
            }
        }

        // Notifications
        item {
            SettingsSection(
                title = "Notifications",
                icon = Icons.Filled.Notifications
            ) {
                SwitchRow("Push notifications", state.settings.notificationsEnabled) {
                    viewModel.updateSettings(state.settings.copy(notificationsEnabled = it))
                }
            }
        }

        // Appearance
        item {
            SettingsSection(
                title = "Appearance",
                icon = Icons.Filled.DarkMode
            ) {
                ThemeDropdown(state.settings.darkMode) {
                    viewModel.updateSettings(state.settings.copy(darkMode = it))
                }
            }
        }

        // Export Settings
        item {
            SettingsSection(
                title = "Export",
                icon = Icons.Filled.FileDownload
            ) {
                ExportFormatDropdown(state.settings.exportFormat) {
                    viewModel.updateSettings(state.settings.copy(exportFormat = it))
                }
            }
        }

        // Data Management
        item {
            SettingsSection(
                title = "Data Management",
                icon = Icons.Filled.Storage
            ) {
                SettingsNumberField("Data Retention (days)", state.settings.dataRetentionDays) {
                    viewModel.updateSettings(state.settings.copy(dataRetentionDays = it))
                }
            }
        }

        // AI Settings
        item {
            SettingsSection(
                title = "AI Detection",
                icon = Icons.Filled.Speed
            ) {
                SwitchRow("Enable AI detection", state.settings.aiEnabled) {
                    viewModel.updateSettings(state.settings.copy(aiEnabled = it))
                }
                if (state.settings.aiEnabled) {
                    Text(
                        "Confidence: ${(state.settings.aiConfidenceThreshold * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
private fun SettingsPasswordField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation()
    )
}

@Composable
private fun SettingsNumberField(label: String, value: Int, onValueChange: (Int) -> Unit) {
    OutlinedTextField(
        value = value.toString(),
        onValueChange = { it.toIntOrNull()?.let { v -> onValueChange(v) } },
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MethodDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("HTTP Method") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("POST", "GET", "PUT").forEach { method ->
                DropdownMenuItem(
                    text = { Text(method) },
                    onClick = { onSelected(method); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected.replaceFirstChar { it.uppercase() },
            onValueChange = {},
            readOnly = true,
            label = { Text("Theme") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("system", "light", "dark").forEach { theme ->
                DropdownMenuItem(
                    text = { Text(theme.replaceFirstChar { it.uppercase() }) },
                    onClick = { onSelected(theme); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExportFormatDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("Default Export Format") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("CSV", "EXCEL", "PDF").forEach { format ->
                DropdownMenuItem(
                    text = { Text(format) },
                    onClick = { onSelected(format); expanded = false }
                )
            }
        }
    }
}

@Composable
private fun SenderListEditor(
    title: String,
    senders: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    var newSender by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newSender,
                onValueChange = { newSender = it },
                placeholder = { Text("Add sender or keyword") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newSender.isNotBlank()) {
                        onAdd(newSender.trim())
                        newSender = ""
                    }
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.primary)
            }
        }

        senders.forEach { sender ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sender,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onRemove(sender) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Remove",
                        tint = DebitRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TestConnectionButton(
    label: String,
    isLoading: Boolean,
    result: String?,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Testing...")
            } else {
                Icon(Icons.Filled.Speed, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(label)
            }
        }

        result?.let { resultText ->
            Spacer(modifier = Modifier.height(6.dp))
            val isSuccess = resultText.startsWith("OK") || resultText.startsWith("Test email sent")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                    contentDescription = null,
                    tint = if (isSuccess) CreditGreen else DebitRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSuccess) CreditGreen else DebitRed,
                    fontSize = 12.sp
                )
            }
        }
    }
}
