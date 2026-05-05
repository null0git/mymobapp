package com.paysms.data.model

data class Transaction(
    val id: String,
    val type: TransactionType,
    val amount: Double,
    val recipientName: String,
    val recipientPhone: String,
    val date: String,
    val time: String,
    val status: TransactionStatus,
    val note: String = ""
)

enum class TransactionType {
    SEND_MONEY,
    RECEIVE_MONEY,
    BUY_AIRTIME,
    PAY_BILL,
    TRANSFER_BANK,
    TRANSFER_WALLET,
    CASH_IN,
    CASH_OUT
}

enum class TransactionStatus {
    SUCCESS,
    PENDING,
    FAILED
}
