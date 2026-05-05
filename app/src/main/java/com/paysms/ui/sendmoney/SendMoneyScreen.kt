package com.paysms.ui.sendmoney

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paysms.ui.theme.TelebirrGreen
import com.paysms.ui.theme.TextHint
import com.paysms.ui.theme.TextSecondary

@Composable
fun SendMoneyScreen(
    currentBalance: Double,
    onSendMoney: (recipientPhone: String, recipientName: String, amount: Double, note: String) -> Unit,
    onBack: () -> Unit
) {
    var recipientPhone by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val amount = amountText.toDoubleOrNull() ?: 0.0
    val serviceFee = if (amount > 0) calculateServiceFee(amount) else 0.0
    val totalDeduction = amount + serviceFee
    val remainingBalance = currentBalance - totalDeduction

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Green header
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
                    text = "Send Money",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Available balance card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = TelebirrGreen.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Available Balance", color = TextSecondary, fontSize = 14.sp)
                    Text(
                        text = "%.2f ETB".format(currentBalance),
                        color = TelebirrGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recipient phone
            Text("Recipient Phone Number", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = recipientPhone,
                onValueChange = { recipientPhone = it.filter { c -> c.isDigit() }.take(9) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter phone number", color = TextHint) },
                prefix = { Text("+251  ", fontWeight = FontWeight.Medium) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TelebirrGreen,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Recipient name
            Text("Recipient Name", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = recipientName,
                onValueChange = { recipientName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter recipient name", color = TextHint) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TelebirrGreen,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Amount
            Text("Amount (ETB)", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { input ->
                    val filtered = input.filter { c -> c.isDigit() || c == '.' }
                    if (filtered.count { it == '.' } <= 1) {
                        amountText = filtered
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("0.00", color = TextHint) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TelebirrGreen,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Note (optional)
            Text("Note (optional)", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                placeholder = { Text("Add a note...", color = TextHint) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TelebirrGreen,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Calculation breakdown
            if (amount > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Transaction Summary",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryRow("Send Amount", "%.2f ETB".format(amount))
                        SummaryRow("Service Fee", "%.2f ETB".format(serviceFee))
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        SummaryRow(
                            "Total Deduction",
                            "%.2f ETB".format(totalDeduction),
                            isBold = true
                        )
                        SummaryRow(
                            "Remaining Balance",
                            "%.2f ETB".format(remainingBalance),
                            valueColor = if (remainingBalance >= 0) TelebirrGreen else Color.Red
                        )
                    }
                }
            }

            // Error message
            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Send button
            Button(
                onClick = {
                    errorMessage = validateSendMoney(
                        recipientPhone, recipientName, amount, currentBalance, totalDeduction
                    )
                    if (errorMessage == null) {
                        showConfirmDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreen)
            ) {
                Icon(Icons.Default.Send, null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send Money", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    // Confirmation dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text("Confirm Transfer", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Send %.2f ETB to:".format(amount))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(recipientName, fontWeight = FontWeight.Bold)
                    Text("+251 $recipientPhone", color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Service fee: %.2f ETB".format(serviceFee), color = TextSecondary, fontSize = 13.sp)
                    Text(
                        "Total: %.2f ETB".format(totalDeduction),
                        fontWeight = FontWeight.Bold,
                        color = TelebirrGreen
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onSendMoney(recipientPhone, recipientName, amount, note)
                        showSuccessDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreen)
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Success dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBack()
            },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = TelebirrGreen,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text("Transfer Successful!", fontWeight = FontWeight.Bold, color = TelebirrGreen)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "%.2f ETB sent to %s".format(amount, recipientName),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "New balance: %.2f ETB".format(remainingBalance),
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TelebirrGreen)
                ) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            value,
            color = valueColor,
            fontSize = 14.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun calculateServiceFee(amount: Double): Double {
    return when {
        amount <= 50 -> 1.0
        amount <= 100 -> 2.0
        amount <= 200 -> 4.0
        amount <= 500 -> 7.0
        amount <= 1000 -> 10.0
        amount <= 2000 -> 15.0
        amount <= 5000 -> 25.0
        amount <= 10000 -> 40.0
        else -> amount * 0.005
    }
}

private fun validateSendMoney(
    phone: String,
    name: String,
    amount: Double,
    balance: Double,
    totalDeduction: Double
): String? {
    if (phone.length < 9) return "Please enter a valid 9-digit phone number"
    if (name.isBlank()) return "Please enter the recipient's name"
    if (amount <= 0) return "Please enter a valid amount"
    if (amount < 1) return "Minimum transfer amount is 1 ETB"
    if (totalDeduction > balance) return "Insufficient balance. You need %.2f ETB but only have %.2f ETB".format(totalDeduction, balance)
    return null
}
