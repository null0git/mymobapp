package com.paysms.ui.history

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.paysms.data.model.Transaction
import com.paysms.data.model.TransactionType
import com.paysms.export.ExportFormat
import com.paysms.export.ExportManager
import com.paysms.ui.theme.CreditGreen
import com.paysms.ui.theme.DebitRed
import com.paysms.util.CurrencyUtils
import com.paysms.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${state.transactions.size} transactions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            // Export button
            ExportDropdown(context, state.transactions)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by name, bank, or amount...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        if (state.banks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.selectedBank == null,
                        onClick = { viewModel.onBankSelected(null) },
                        label = { Text("All") }
                    )
                }
                items(state.banks) { bank ->
                    FilterChip(
                        selected = state.selectedBank == bank,
                        onClick = {
                            viewModel.onBankSelected(if (state.selectedBank == bank) null else bank)
                        },
                        label = { Text(bank) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (state.transactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.Receipt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No transactions found",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.transactions) { transaction ->
                    HistoryTransactionCard(
                        transaction = transaction,
                        onClick = { selectedTransaction = transaction }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    selectedTransaction?.let { transaction ->
        ModalBottomSheet(
            onDismissRequest = { selectedTransaction = null },
            sheetState = sheetState
        ) {
            TransactionDetailSheet(
                transaction = transaction,
                onSendToApi = { viewModel.sendToApi(transaction) },
                onSendToEmail = { viewModel.sendToEmail(transaction) }
            )
        }
    }
}

@Composable
private fun ExportDropdown(context: Context, transactions: List<Transaction>) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Filled.Share, contentDescription = "Export", tint = MaterialTheme.colorScheme.primary)
        }
        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ExportFormat.entries.forEach { format ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text("Export as ${format.name}") },
                    onClick = {
                        expanded = false
                        val manager = ExportManager(context)
                        val file = manager.export(transactions, format)
                        if (file != null) {
                            val intent = manager.shareFile(file)
                            context.startActivity(android.content.Intent.createChooser(intent, "Share ${format.name}"))
                        } else {
                            Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HistoryTransactionCard(transaction: Transaction, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        when (transaction.transactionType) {
                            TransactionType.CREDIT -> CreditGreen.copy(alpha = 0.1f)
                            TransactionType.DEBIT -> DebitRed.copy(alpha = 0.1f)
                            TransactionType.UNKNOWN -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (transaction.transactionType) {
                        TransactionType.CREDIT -> Icons.Filled.ArrowDownward
                        TransactionType.DEBIT -> Icons.Filled.ArrowUpward
                        TransactionType.UNKNOWN -> Icons.Filled.Receipt
                    },
                    contentDescription = null,
                    tint = when (transaction.transactionType) {
                        TransactionType.CREDIT -> CreditGreen
                        TransactionType.DEBIT -> DebitRed
                        TransactionType.UNKNOWN -> MaterialTheme.colorScheme.primary
                    },
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.senderName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = transaction.bankName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = DateUtils.formatDateTime(transaction.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                        fontSize = 11.sp
                    )
                    if (transaction.transactionRef.isNotEmpty()) {
                        Text(
                            text = " \u2022 ${transaction.transactionRef}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (transaction.transactionType == TransactionType.CREDIT) "+" else "-"}${CurrencyUtils.formatAmount(transaction.amount)}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = when (transaction.transactionType) {
                        TransactionType.CREDIT -> CreditGreen
                        TransactionType.DEBIT -> DebitRed
                        TransactionType.UNKNOWN -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    StatusDot(sent = transaction.apiSent, label = "API")
                    StatusDot(sent = transaction.emailSent, label = "Email")
                }
            }
        }
    }
}

@Composable
private fun StatusDot(sent: Boolean, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (sent) CreditGreen else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
private fun TransactionDetailSheet(
    transaction: Transaction,
    onSendToApi: () -> Unit,
    onSendToEmail: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "Transaction Details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Amount header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (transaction.transactionType) {
                    TransactionType.CREDIT -> CreditGreen.copy(alpha = 0.08f)
                    TransactionType.DEBIT -> DebitRed.copy(alpha = 0.08f)
                    TransactionType.UNKNOWN -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${if (transaction.transactionType == TransactionType.CREDIT) "+" else "-"}${CurrencyUtils.formatAmount(transaction.amount, transaction.currency)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when (transaction.transactionType) {
                        TransactionType.CREDIT -> CreditGreen
                        TransactionType.DEBIT -> DebitRed
                        TransactionType.UNKNOWN -> MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = transaction.transactionType.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onSendToApi,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Send to API", fontSize = 13.sp)
            }
            OutlinedButton(
                onClick = onSendToEmail,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Send Email", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Details section
        DetailSection("Transaction Info") {
            DetailRow("Sender", transaction.senderName)
            DetailRow("Bank", transaction.bankName)
            if (transaction.accountNumber.isNotEmpty()) DetailRow("Account", transaction.accountNumber)
            DetailRow("Date & Time", DateUtils.formatDateTime(transaction.timestamp))
            if (transaction.transactionRef.isNotEmpty()) DetailRow("Reference", transaction.transactionRef)
            if (transaction.balance.isNotEmpty()) DetailRow("Balance After", transaction.balance)
        }

        Spacer(modifier = Modifier.height(12.dp))

        DetailSection("SMS Info") {
            DetailRow("SMS Sender", transaction.smsSenderNumber.ifEmpty { transaction.smsSender })
            DetailRow("SMS Hash", transaction.smsHash.take(16) + "...")
        }

        Spacer(modifier = Modifier.height(12.dp))

        DetailSection("Sync Status") {
            DetailRow("API Sent", if (transaction.apiSent) "Yes" else "Pending")
            if (transaction.apiStatusCode > 0) DetailRow("API Status Code", "${transaction.apiStatusCode}")
            DetailRow("Email Sent", if (transaction.emailSent) "Yes" else "Pending")
        }

        if (transaction.apiRequestPayload.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            DetailSection("API Request Payload") {
                CodeBlock(transaction.apiRequestPayload)
            }
        }

        if (transaction.apiResponse.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            DetailSection("API Response") {
                CodeBlock(transaction.apiResponse)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        DetailSection("Raw SMS") {
            CodeBlock(transaction.rawSms)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun DetailSection(title: String, content: @Composable () -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            content()
        }
    }
}

@Composable
private fun CodeBlock(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall,
            fontSize = 11.sp,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.6f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
