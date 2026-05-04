package com.paysms.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paysms.PaySmsApp
import com.paysms.data.model.Transaction
import com.paysms.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class BankSummary(val bankName: String, val total: Double)

data class DashboardState(
    val totalReceivedToday: Double = 0.0,
    val totalSentToday: Double = 0.0,
    val transactionCountToday: Int = 0,
    val bankSummaries: List<BankSummary> = emptyList(),
    val recentTransactions: List<Transaction> = emptyList(),
    val pendingCount: Int = 0,
    val dailyData: List<Pair<String, Double>> = emptyList()
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as PaySmsApp
    private val repository = app.repository

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        val startOfDay = DateUtils.getStartOfDay()

        viewModelScope.launch {
            try {
                combine(
                    repository.getTotalCreditSince(startOfDay),
                    repository.getTotalDebitSince(startOfDay),
                    repository.getTransactionCountSince(startOfDay),
                    repository.getAllTransactions(),
                    repository.getPendingCount()
                ) { credit, debit, count, transactions, pendingCount ->
                    val bankMap = mutableMapOf<String, Double>()
                    transactions
                        .filter { it.timestamp >= startOfDay && it.transactionType == com.paysms.data.model.TransactionType.CREDIT }
                        .forEach { tx ->
                            bankMap[tx.bankName] = (bankMap[tx.bankName] ?: 0.0) + tx.amount
                        }

                    val dailyData = buildDailyData(transactions)

                    DashboardState(
                        totalReceivedToday = credit ?: 0.0,
                        totalSentToday = debit ?: 0.0,
                        transactionCountToday = count,
                        bankSummaries = bankMap.map { BankSummary(it.key, it.value) }
                            .sortedByDescending { it.total },
                        recentTransactions = transactions.take(5),
                        pendingCount = pendingCount,
                        dailyData = dailyData
                    )
                }.collect { state ->
                    _state.value = state
                }
            } catch (e: Exception) {
                Log.e("DashboardVM", "Error loading dashboard: ${e.message}")
            }
        }
    }

    private fun buildDailyData(transactions: List<Transaction>): List<Pair<String, Double>> {
        return try {
            val result = mutableListOf<Pair<String, Double>>()
            for (i in 6 downTo 0) {
                val dayStart = DateUtils.getDaysAgo(i)
                val dayEnd = if (i == 0) System.currentTimeMillis() else DateUtils.getDaysAgo(i - 1)
                val total = transactions
                    .filter {
                        it.timestamp in dayStart until dayEnd &&
                            it.transactionType == com.paysms.data.model.TransactionType.CREDIT
                    }
                    .sumOf { it.amount }
                result.add(DateUtils.formatDate(dayStart).takeLast(5) to total)
            }
            result
        } catch (e: Exception) {
            Log.e("DashboardVM", "Error building daily data: ${e.message}")
            emptyList()
        }
    }
}
