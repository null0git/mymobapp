package com.paysms.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    fun formatAmount(amount: Double, currency: String = "ETB"): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        return "$currency ${formatter.format(amount)}"
    }

    fun formatAmountShort(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
            else -> String.format("%.0f", amount)
        }
    }
}
