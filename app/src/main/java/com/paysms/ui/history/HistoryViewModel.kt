package com.paysms.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paysms.PaySmsApp
import com.paysms.data.model.PendingRequest
import com.paysms.data.model.RequestType
import com.paysms.data.model.Transaction
import com.paysms.offline.SyncWorker
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class HistoryState(
    val transactions: List<Transaction> = emptyList(),
    val searchQuery: String = "",
    val selectedBank: String? = null,
    val banks: List<String> = emptyList(),
    val isLoading: Boolean = true
)

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as PaySmsApp
    private val repository = app.repository
    private val settingsManager = app.settingsManager
    private val gson = Gson()

    private val _state = MutableStateFlow(HistoryState())
    val state: StateFlow<HistoryState> = _state

    init {
        loadTransactions()
        loadBanks()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            repository.getAllTransactions().collectLatest { transactions ->
                _state.value = _state.value.copy(
                    transactions = applyFilters(transactions),
                    isLoading = false
                )
            }
        }
    }

    private fun loadBanks() {
        viewModelScope.launch {
            repository.getAllBankNames().collectLatest { banks ->
                _state.value = _state.value.copy(banks = banks)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
        refreshFiltered()
    }

    fun onBankSelected(bank: String?) {
        _state.value = _state.value.copy(selectedBank = bank)
        refreshFiltered()
    }

    fun sendToApi(transaction: Transaction) {
        viewModelScope.launch {
            val payload = transaction.apiRequestPayload.ifEmpty {
                gson.toJson(
                    mapOf(
                        "amount" to transaction.amount,
                        "currency" to transaction.currency,
                        "sender" to transaction.senderName,
                        "bank" to transaction.bankName,
                        "account" to transaction.accountNumber,
                        "type" to transaction.transactionType.name,
                        "timestamp" to transaction.timestamp,
                        "transactionRef" to transaction.transactionRef
                    )
                )
            }
            repository.insertPendingRequest(
                PendingRequest(
                    transactionId = transaction.id,
                    requestType = RequestType.API_CALL,
                    payload = payload
                )
            )
            SyncWorker.enqueue(getApplication())
        }
    }

    fun sendToEmail(transaction: Transaction) {
        viewModelScope.launch {
            val emailPayload = gson.toJson(
                mapOf(
                    "subject" to "Payment: ${transaction.currency} ${String.format("%.2f", transaction.amount)}",
                    "body" to (transaction.emailBody.ifEmpty { "Transaction from ${transaction.senderName}: ${transaction.currency} ${String.format("%.2f", transaction.amount)}" })
                )
            )
            repository.insertPendingRequest(
                PendingRequest(
                    transactionId = transaction.id,
                    requestType = RequestType.EMAIL,
                    payload = emailPayload
                )
            )
            SyncWorker.enqueue(getApplication())
        }
    }

    private fun refreshFiltered() {
        viewModelScope.launch {
            val query = _state.value.searchQuery
            val bank = _state.value.selectedBank

            val flow = when {
                query.isNotEmpty() -> repository.searchTransactions(query)
                bank != null -> repository.getTransactionsByBank(bank)
                else -> repository.getAllTransactions()
            }

            flow.collectLatest { transactions ->
                _state.value = _state.value.copy(
                    transactions = applyFilters(transactions)
                )
            }
        }
    }

    private fun applyFilters(transactions: List<Transaction>): List<Transaction> {
        var filtered = transactions
        val query = _state.value.searchQuery
        val bank = _state.value.selectedBank

        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.senderName.contains(query, ignoreCase = true) ||
                    it.bankName.contains(query, ignoreCase = true) ||
                    it.rawSms.contains(query, ignoreCase = true) ||
                    it.transactionRef.contains(query, ignoreCase = true)
            }
        }

        if (bank != null) {
            filtered = filtered.filter { it.bankName == bank }
        }

        return filtered
    }
}
