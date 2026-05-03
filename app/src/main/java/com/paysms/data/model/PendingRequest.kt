package com.paysms.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_requests")
data class PendingRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transactionId: Long,
    val requestType: RequestType,
    val payload: String,
    val retryCount: Int = 0,
    val maxRetries: Int = 5,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAttempt: Long = 0
)

enum class RequestType {
    API_CALL,
    EMAIL
}
