package com.paysms.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val currency: String = "ETB",
    val senderName: String,
    val bankName: String,
    val accountNumber: String = "",
    val transactionType: TransactionType,
    val timestamp: Long = System.currentTimeMillis(),
    val rawSms: String,
    val smsSender: String,
    val smsHash: String,
    val apiSent: Boolean = false,
    val emailSent: Boolean = false,
    val apiResponse: String = "",
    val apiStatusCode: Int = 0
)

enum class TransactionType {
    CREDIT,
    DEBIT,
    UNKNOWN
}
