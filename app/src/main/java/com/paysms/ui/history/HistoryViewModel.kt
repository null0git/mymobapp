package com.paysms.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paysms.PaySmsApp
import com.paysms.data.model.Transaction
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
                    it.rawSms.contains(query, ignoreCase = true)
            }
        }

        if (bank != null) {
            filtered = filtered.filter { it.bankName == bank }
        }

        return filtered
    }
}
