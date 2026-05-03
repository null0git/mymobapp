package com.paysms.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paysms.data.model.AppSettings
import com.paysms.data.model.BankRule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var currentSettings by remember(state.settings) { mutableStateOf(state.settings) }
    var showBankRuleDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.saveMessage) {
        if (state.saveMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(state.saveMessage)
            viewModel.clearSaveMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // API Settings
            item {
                SettingsSection(
                    title = "API Integration",
                    icon = Icons.Filled.Api
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable API")
                        Switch(
                            checked = currentSettings.apiEnabled,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(apiEnabled = it)
                            }
                        )
                    }

                    if (currentSettings.apiEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentSettings.apiUrl,
                            onValueChange = { currentSettings = currentSettings.copy(apiUrl = it) },
                            label = { Text("API URL") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        var methodExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = methodExpanded,
                            onExpandedChange = { methodExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = currentSettings.apiMethod,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Method") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = methodExpanded,
                                onDismissRequest = { methodExpanded = false }
                            ) {
                                listOf("POST", "GET", "PUT").forEach { method ->
                                    DropdownMenuItem(
                                        text = { Text(method) },
                                        onClick = {
                                            currentSettings = currentSettings.copy(apiMethod = method)
                                            methodExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentSettings.apiKey,
                            onValueChange = { currentSettings = currentSettings.copy(apiKey = it) },
                            label = { Text("API Key / Token") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable Email")
                        Switch(
                            checked = currentSettings.emailEnabled,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(emailEnabled = it)
                            }
                        )
                    }

                    if (currentSettings.emailEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentSettings.smtpHost,
                            onValueChange = { currentSettings = currentSettings.copy(smtpHost = it) },
                            label = { Text("SMTP Host") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentSettings.smtpPort.toString(),
                            onValueChange = {
                                currentSettings = currentSettings.copy(
                                    smtpPort = it.toIntOrNull() ?: 587
                                )
                            },
                            label = { Text("SMTP Port") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentSettings.senderEmail,
                            onValueChange = { currentSettings = currentSettings.copy(senderEmail = it) },
                            label = { Text("Sender Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentSettings.senderPassword,
                            onValueChange = { currentSettings = currentSettings.copy(senderPassword = it) },
                            label = { Text("Sender Password") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = currentSettings.receiverEmail,
                            onValueChange = { currentSettings = currentSettings.copy(receiverEmail = it) },
                            label = { Text("Receiver Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                    }
                }
            }

            // SMS Filter Settings
            item {
                SettingsSection(
                    title = "SMS Filters",
                    icon = Icons.Filled.FilterList
                ) {
                    Text(
                        "Allowed Senders",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        "Leave empty to allow all senders",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    var newSender by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newSender,
                            onValueChange = { newSender = it },
                            label = { Text("Add sender") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (newSender.isNotBlank()) {
                                currentSettings = currentSettings.copy(
                                    allowedSenders = currentSettings.allowedSenders + newSender.trim()
                                )
                                newSender = ""
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add")
                        }
                    }

                    currentSettings.allowedSenders.forEach { sender ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(sender, style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = {
                                currentSettings = currentSettings.copy(
                                    allowedSenders = currentSettings.allowedSenders - sender
                                )
                            }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        "Keywords",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    var newKeyword by remember { mutableStateOf("") }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newKeyword,
                            onValueChange = { newKeyword = it },
                            label = { Text("Add keyword") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (newKeyword.isNotBlank()) {
                                currentSettings = currentSettings.copy(
                                    keywords = currentSettings.keywords + newKeyword.trim()
                                )
                                newKeyword = ""
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add")
                        }
                    }

                    currentSettings.keywords.forEach { keyword ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(keyword, style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = {
                                currentSettings = currentSettings.copy(
                                    keywords = currentSettings.keywords - keyword
                                )
                            }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Remove",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }

            // Bank Rules
            item {
                SettingsSection(
                    title = "Bank Rules",
                    icon = Icons.Filled.AccountBalance
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Custom bank detection rules",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        IconButton(onClick = { showBankRuleDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add rule")
                        }
                    }
                }
            }

            items(state.bankRules) { rule ->
                BankRuleCard(
                    rule = rule,
                    onDelete = { viewModel.deleteBankRule(rule) },
                    onToggle = { viewModel.updateBankRule(rule.copy(isEnabled = it)) }
                )
            }

            // AI Settings
            item {
                SettingsSection(
                    title = "AI Detection",
                    icon = Icons.Filled.SmartToy
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Enable AI Detection")
                        Switch(
                            checked = currentSettings.aiEnabled,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(aiEnabled = it)
                            }
                        )
                    }

                    if (currentSettings.aiEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Confidence Threshold: ${(currentSettings.aiConfidenceThreshold * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        var sliderValue by remember(currentSettings.aiConfidenceThreshold) {
                            mutableFloatStateOf(currentSettings.aiConfidenceThreshold)
                        }
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            onValueChangeFinished = {
                                currentSettings = currentSettings.copy(aiConfidenceThreshold = sliderValue)
                            },
                            valueRange = 0.5f..1.0f,
                            steps = 9
                        )
                    }
                }
            }

            // Notifications
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Show Notifications",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                        Switch(
                            checked = currentSettings.notificationsEnabled,
                            onCheckedChange = {
                                currentSettings = currentSettings.copy(notificationsEnabled = it)
                            }
                        )
                    }
                }
            }

            // Save Button
            item {
                Button(
                    onClick = { viewModel.updateSettings(currentSettings) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !state.isSaving
                ) {
                    Text(if (state.isSaving) "Saving..." else "Save Settings")
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }

        SnackbarHost(hostState = snackbarHostState)
    }

    if (showBankRuleDialog) {
        BankRuleDialog(
            onDismiss = { showBankRuleDialog = false },
            onSave = { rule ->
                viewModel.addBankRule(rule)
                showBankRuleDialog = false
            }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun BankRuleCard(
    rule: BankRule,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.bankName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Keywords: ${rule.senderKeywords}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Switch(checked = rule.isEnabled, onCheckedChange = onToggle)
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun BankRuleDialog(onDismiss: () -> Unit, onSave: (BankRule) -> Unit) {
    var bankName by remember { mutableStateOf("") }
    var senderKeywords by remember { mutableStateOf("") }
    var creditKeywords by remember { mutableStateOf("credited,received,deposited") }
    var debitKeywords by remember { mutableStateOf("debited,sent,withdrawn") }
    var amountPattern by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Bank Rule") },
        text = {
            Column {
                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = senderKeywords,
                    onValueChange = { senderKeywords = it },
                    label = { Text("Sender Keywords (comma-separated)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = creditKeywords,
                    onValueChange = { creditKeywords = it },
                    label = { Text("Credit Keywords") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = debitKeywords,
                    onValueChange = { debitKeywords = it },
                    label = { Text("Debit Keywords") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountPattern,
                    onValueChange = { amountPattern = it },
                    label = { Text("Amount Pattern (regex, optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (bankName.isNotBlank() && senderKeywords.isNotBlank()) {
                        onSave(
                            BankRule(
                                bankName = bankName,
                                senderKeywords = senderKeywords,
                                creditKeywords = creditKeywords,
                                debitKeywords = debitKeywords,
                                amountPattern = amountPattern
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
