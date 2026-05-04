package com.paysms.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_rules")
data class BankRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bankName: String,
    val senderKeywords: String,
    val creditKeywords: String = "credited,received,deposited",
    val debitKeywords: String = "debited,sent,withdrawn,transferred",
    val amountPattern: String = "",
    val isEnabled: Boolean = true
)
