package com.paysms.data.repository

import com.paysms.data.database.BankRuleDao
import com.paysms.data.database.PendingRequestDao
import com.paysms.data.database.TransactionDao
import com.paysms.data.model.BankRule
import com.paysms.data.model.PendingRequest
import com.paysms.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val transactionDao: TransactionDao,
    private val pendingRequestDao: PendingRequestDao,
    private val bankRuleDao: BankRuleDao
) {
    fun getAllTransactions(): Flow<List<Transaction>> = transactionDao.getAllTransactions()

    fun getTransactionsByDateRange(start: Long, end: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsByDateRange(start, end)

    fun getTransactionsByBank(bankName: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByBank(bankName)

    fun searchTransactions(query: String): Flow<List<Transaction>> =
        transactionDao.searchTransactions(query)

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insert(transaction)

    suspend fun updateTransaction(transaction: Transaction) =
        transactionDao.update(transaction)

    suspend fun isDuplicate(hash: String): Boolean =
        transactionDao.existsByHash(hash)

    fun getTotalCreditSince(startTime: Long): Flow<Double?> =
        transactionDao.getTotalCreditSince(startTime)

    fun getTotalDebitSince(startTime: Long): Flow<Double?> =
        transactionDao.getTotalDebitSince(startTime)

    fun getTransactionCountSince(startTime: Long): Flow<Int> =
        transactionDao.getTransactionCountSince(startTime)

    fun getAllBankNames(): Flow<List<String>> = transactionDao.getAllBankNames()

    fun getTotalCreditByBankSince(bankName: String, startTime: Long): Flow<Double?> =
        transactionDao.getTotalCreditByBankSince(bankName, startTime)

    fun getCreditTransactionsSince(startTime: Long): Flow<List<Transaction>> =
        transactionDao.getCreditTransactionsSince(startTime)

    suspend fun deleteTransaction(id: Long) = transactionDao.deleteTransaction(id)

    // Pending requests
    suspend fun insertPendingRequest(request: PendingRequest): Long =
        pendingRequestDao.insert(request)

    suspend fun getPendingRequests(): List<PendingRequest> =
        pendingRequestDao.getPendingRequests()

    suspend fun updatePendingRequest(request: PendingRequest) =
        pendingRequestDao.update(request)

    suspend fun deletePendingRequest(request: PendingRequest) =
        pendingRequestDao.delete(request)

    fun getPendingCount(): Flow<Int> = pendingRequestDao.getPendingCount()

    // Bank rules
    fun getAllBankRules(): Flow<List<BankRule>> = bankRuleDao.getAllRules()

    suspend fun getEnabledBankRules(): List<BankRule> = bankRuleDao.getEnabledRules()

    suspend fun insertBankRule(rule: BankRule): Long = bankRuleDao.insert(rule)

    suspend fun updateBankRule(rule: BankRule) = bankRuleDao.update(rule)

    suspend fun deleteBankRule(rule: BankRule) = bankRuleDao.delete(rule)
}
