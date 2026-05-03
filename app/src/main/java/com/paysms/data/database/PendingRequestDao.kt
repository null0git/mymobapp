package com.paysms.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.paysms.data.model.PendingRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: PendingRequest): Long

    @Update
    suspend fun update(request: PendingRequest)

    @Delete
    suspend fun delete(request: PendingRequest)

    @Query("SELECT * FROM pending_requests WHERE retryCount < maxRetries ORDER BY createdAt ASC")
    suspend fun getPendingRequests(): List<PendingRequest>

    @Query("SELECT COUNT(*) FROM pending_requests")
    fun getPendingCount(): Flow<Int>

    @Query("DELETE FROM pending_requests WHERE transactionId = :transactionId AND requestType = :type")
    suspend fun deleteByTransactionAndType(transactionId: Long, type: String)
}
