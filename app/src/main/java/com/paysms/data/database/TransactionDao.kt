package com.paysms.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.paysms.data.model.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getTransactionsByDateRange(startTime: Long, endTime: Long): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE bankName = :bankName ORDER BY timestamp DESC")
    fun getTransactionsByBank(bankName: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE senderName LIKE '%' || :query || '%' OR bankName LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchTransactions(query: String): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): Transaction?

    @Query("SELECT EXISTS(SELECT 1 FROM transactions WHERE smsHash = :hash)")
    suspend fun existsByHash(hash: String): Boolean

    @Query("SELECT SUM(amount) FROM transactions WHERE transactionType = 'CREDIT' AND timestamp >= :startTime")
    fun getTotalCreditSince(startTime: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE transactionType = 'DEBIT' AND timestamp >= :startTime")
    fun getTotalDebitSince(startTime: Long): Flow<Double?>

    @Query("SELECT COUNT(*) FROM transactions WHERE timestamp >= :startTime")
    fun getTransactionCountSince(startTime: Long): Flow<Int>

    @Query("SELECT DISTINCT bankName FROM transactions")
    fun getAllBankNames(): Flow<List<String>>

    @Query("SELECT SUM(amount) FROM transactions WHERE transactionType = 'CREDIT' AND bankName = :bankName AND timestamp >= :startTime")
    fun getTotalCreditByBankSince(bankName: String, startTime: Long): Flow<Double?>

    @Query("SELECT * FROM transactions WHERE transactionType = 'CREDIT' AND timestamp >= :startTime ORDER BY timestamp ASC")
    fun getCreditTransactionsSince(startTime: Long): Flow<List<Transaction>>

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)
}
