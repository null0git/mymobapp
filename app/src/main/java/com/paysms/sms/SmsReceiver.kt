package com.paysms.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.paysms.PaySmsApp
import com.paysms.data.model.PendingRequest
import com.paysms.data.model.RequestType
import com.paysms.notification.NotificationHelper
import com.paysms.offline.SyncWorker
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    private val parser = SmsParser()
    private val gson = Gson()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        val smsMap = mutableMapOf<String, StringBuilder>()
        for (message in messages) {
            val sender = message.displayOriginatingAddress ?: continue
            smsMap.getOrPut(sender) { StringBuilder() }.append(message.displayMessageBody)
        }

        val app = context.applicationContext as PaySmsApp
        val repository = app.repository
        val settingsManager = app.settingsManager

        CoroutineScope(Dispatchers.IO).launch {
            val settings = settingsManager.settings.first()

            for ((sender, bodyBuilder) in smsMap) {
                val body = bodyBuilder.toString()

                if (settings.allowedSenders.isNotEmpty()) {
                    val allowed = settings.allowedSenders.any {
                        sender.contains(it, ignoreCase = true)
                    }
                    if (!allowed) continue
                }

                if (!settings.keywords.any { body.contains(it, ignoreCase = true) }) continue

                val bankRules = repository.getEnabledBankRules()
                val parsed = parser.parse(body, sender, bankRules) ?: continue

                if (repository.isDuplicate(parsed.smsHash)) continue

                val transaction = parser.toTransaction(parsed)
                val transactionId = repository.insertTransaction(transaction)

                if (settings.notificationsEnabled) {
                    NotificationHelper.showTransactionNotification(
                        context,
                        transaction.copy(id = transactionId)
                    )
                }

                if (settings.apiEnabled && settings.apiUrl.isNotEmpty()) {
                    val payload = gson.toJson(
                        mapOf(
                            "amount" to transaction.amount,
                            "currency" to transaction.currency,
                            "sender" to transaction.senderName,
                            "bank" to transaction.bankName,
                            "account" to transaction.accountNumber,
                            "type" to transaction.transactionType.name,
                            "timestamp" to transaction.timestamp
                        )
                    )
                    repository.insertPendingRequest(
                        PendingRequest(
                            transactionId = transactionId,
                            requestType = RequestType.API_CALL,
                            payload = payload
                        )
                    )
                }

                if (settings.emailEnabled && settings.receiverEmail.isNotEmpty()) {
                    val emailPayload = gson.toJson(
                        mapOf(
                            "subject" to "Payment Received: ${transaction.currency} ${transaction.amount}",
                            "body" to buildEmailBody(transaction)
                        )
                    )
                    repository.insertPendingRequest(
                        PendingRequest(
                            transactionId = transactionId,
                            requestType = RequestType.EMAIL,
                            payload = emailPayload
                        )
                    )
                }

                SyncWorker.enqueue(context)
            }
        }
    }

    private fun buildEmailBody(transaction: com.paysms.data.model.Transaction): String {
        return """
            Payment Received!
            
            Amount: ${transaction.currency} ${transaction.amount}
            From: ${transaction.senderName}
            Bank: ${transaction.bankName}
            Account: ${transaction.accountNumber}
            Type: ${transaction.transactionType}
            Time: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(transaction.timestamp))}
        """.trimIndent()
    }
}
