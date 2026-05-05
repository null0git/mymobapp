package com.paysms.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paysms.data.model.Transaction
import com.paysms.data.model.TransactionStatus
import com.paysms.data.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

private val Context.transactionStore by preferencesDataStore(name = "transactions")

class TransactionManager(private val context: Context) {

    private val gson = Gson()

    companion object {
        private val TRANSACTIONS_KEY = stringPreferencesKey("transaction_list")
    }

    val transactions: Flow<List<Transaction>> = context.transactionStore.data.map { prefs ->
        val json = prefs[TRANSACTIONS_KEY] ?: "[]"
        val type = object : TypeToken<List<Transaction>>() {}.type
        gson.fromJson(json, type)
    }

    suspend fun addTransaction(transaction: Transaction) {
        context.transactionStore.edit { prefs ->
            val json = prefs[TRANSACTIONS_KEY] ?: "[]"
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            val list: MutableList<Transaction> = gson.fromJson(json, type)
            list.add(0, transaction)
            prefs[TRANSACTIONS_KEY] = gson.toJson(list)
        }
    }

    fun createSendMoneyTransaction(
        amount: Double,
        recipientName: String,
        recipientPhone: String,
        note: String = ""
    ): Transaction {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val now = Date()

        return Transaction(
            id = UUID.randomUUID().toString().take(8).uppercase(),
            type = TransactionType.SEND_MONEY,
            amount = amount,
            recipientName = recipientName,
            recipientPhone = recipientPhone,
            date = dateFormat.format(now),
            time = timeFormat.format(now),
            status = TransactionStatus.SUCCESS,
            note = note
        )
    }

    fun createBuyAirtimeTransaction(
        amount: Double,
        phone: String
    ): Transaction {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val now = Date()

        return Transaction(
            id = UUID.randomUUID().toString().take(8).uppercase(),
            type = TransactionType.BUY_AIRTIME,
            amount = amount,
            recipientName = "Airtime",
            recipientPhone = phone,
            date = dateFormat.format(now),
            time = timeFormat.format(now),
            status = TransactionStatus.SUCCESS
        )
    }
}
