package com.paysms.ui.analytics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.paysms.PaySmsApp
import com.paysms.data.model.Transaction
import com.paysms.data.model.TransactionType
import com.paysms.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class AnalyticsState(
    val totalReceived: Double = 0.0,
    val totalSent: Double = 0.0,
    val transactionCount: Int = 0,
    val averageTransaction: Double = 0.0,
    val weeklyData: List<Pair<String, Double>> = emptyList(),
    val monthlyData: List<Pair<String, Double>> = emptyList(),
    val bankDistribution: List<Pair<String, Double>> = emptyList(),
    val selectedPeriod: TimePeriod = TimePeriod.WEEK
)

enum class TimePeriod(val label: String) {
    WEEK("Week"),
    MONTH("Month"),
    ALL("All Time")
}

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as PaySmsApp
    private val repository = app.repository

    private val _state = MutableStateFlow(AnalyticsState())
    val state: StateFlow<AnalyticsState> = _state

    init {
        loadAnalytics()
    }

    fun onPeriodSelected(period: TimePeriod) {
        _state.value = _state.value.copy(selectedPeriod = period)
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            val startTime = when (_state.value.selectedPeriod) {
                TimePeriod.WEEK -> DateUtils.getDaysAgo(7)
                TimePeriod.MONTH -> DateUtils.getStartOfMonth()
                TimePeriod.ALL -> 0L
            }

            repository.getAllTransactions().collectLatest { allTransactions ->
                val filtered = if (startTime > 0) {
                    allTransactions.filter { it.timestamp >= startTime }
                } else {
                    allTransactions
                }

                val credits = filtered.filter { it.transactionType == TransactionType.CREDIT }
                val debits = filtered.filter { it.transactionType == TransactionType.DEBIT }

                val totalReceived = credits.sumOf { it.amount }
                val totalSent = debits.sumOf { it.amount }

                val bankDist = credits.groupBy { it.bankName }
                    .map { (bank, txns) -> bank to txns.sumOf { it.amount } }
                    .sortedByDescending { it.second }

                val weeklyData = buildWeeklyData(allTransactions)

                _state.value = _state.value.copy(
                    totalReceived = totalReceived,
                    totalSent = totalSent,
                    transactionCount = filtered.size,
                    averageTransaction = if (credits.isNotEmpty()) totalReceived / credits.size else 0.0,
                    weeklyData = weeklyData,
                    bankDistribution = bankDist
                )
            }
        }
    }

    private fun buildWeeklyData(transactions: List<Transaction>): List<Pair<String, Double>> {
        val result = mutableListOf<Pair<String, Double>>()
        for (i in 6 downTo 0) {
            val dayStart = DateUtils.getDaysAgo(i)
            val dayEnd = if (i == 0) System.currentTimeMillis() else DateUtils.getDaysAgo(i - 1)
            val total = transactions
                .filter {
                    it.timestamp in dayStart until dayEnd &&
                        it.transactionType == TransactionType.CREDIT
                }
                .sumOf { it.amount }
            result.add(DateUtils.formatDate(dayStart).takeLast(5) to total)
        }
        return result
    }
}
