package com.paysms.ui.transaction

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paysms.data.TransactionManager
import com.paysms.data.model.Transaction
import com.paysms.data.model.TransactionStatus
import com.paysms.data.model.TransactionType
import com.paysms.ui.theme.TelebirrGreen
import com.paysms.ui.theme.TelebirrOrange
import com.paysms.ui.theme.TextHint
import com.paysms.ui.theme.TextSecondary

@Composable
fun TransactionHistoryScreen(
    transactionManager: TransactionManager,
    onBack: () -> Unit
) {
    val transactions by transactionManager.transactions.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(TelebirrGreen)
                .padding(vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "Transaction History",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = null,
                        tint = TextHint,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No transactions yet",
                        color = TextHint,
                        fontSize = 16.sp
                    )
                    Text(
                        "Your transaction history will appear here",
                        color = TextHint,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionItem(transaction)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    val (icon, iconBg, amountPrefix) = getTransactionVisuals(transaction.type)
    val statusColor = when (transaction.status) {
        TransactionStatus.SUCCESS -> TelebirrGreen
        TransactionStatus.PENDING -> TelebirrOrange
        TransactionStatus.FAILED -> Color.Red
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    transaction.recipientName,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                Text(
                    getTransactionTypeLabel(transaction.type),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    "${transaction.date} ${transaction.time}",
                    color = TextHint,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$amountPrefix%.2f ETB".format(transaction.amount),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (amountPrefix == "+") TelebirrGreen else Color.Red
                )
                Text(
                    transaction.status.name,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun getTransactionVisuals(type: TransactionType): Triple<ImageVector, Color, String> {
    return when (type) {
        TransactionType.SEND_MONEY -> Triple(Icons.Default.ArrowUpward, Color(0xFFE53935), "-")
        TransactionType.RECEIVE_MONEY -> Triple(Icons.Default.ArrowDownward, TelebirrGreen, "+")
        TransactionType.BUY_AIRTIME -> Triple(Icons.Default.Phone, TelebirrOrange, "-")
        TransactionType.PAY_BILL -> Triple(Icons.Default.Receipt, Color(0xFF1565C0), "-")
        TransactionType.TRANSFER_BANK -> Triple(Icons.Default.SwapHoriz, Color(0xFF7B1FA2), "-")
        TransactionType.TRANSFER_WALLET -> Triple(Icons.Default.SwapHoriz, TelebirrGreen, "-")
        TransactionType.CASH_IN -> Triple(Icons.Default.ArrowDownward, TelebirrGreen, "+")
        TransactionType.CASH_OUT -> Triple(Icons.Default.ArrowUpward, TelebirrOrange, "-")
    }
}

private fun getTransactionTypeLabel(type: TransactionType): String {
    return when (type) {
        TransactionType.SEND_MONEY -> "Send Money"
        TransactionType.RECEIVE_MONEY -> "Receive Money"
        TransactionType.BUY_AIRTIME -> "Buy Airtime"
        TransactionType.PAY_BILL -> "Pay Bill"
        TransactionType.TRANSFER_BANK -> "Transfer to Bank"
        TransactionType.TRANSFER_WALLET -> "Transfer to Wallet"
        TransactionType.CASH_IN -> "Cash In"
        TransactionType.CASH_OUT -> "Cash Out"
    }
}
