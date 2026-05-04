package com.paysms.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
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
        try {
            if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

            val messages = try {
                Telephony.Sms.Intents.getMessagesFromIntent(intent)
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Failed to get messages from intent: ${e.message}")
                return
            }
            if (messages.isNullOrEmpty()) return

            val smsMap = mutableMapOf<String, StringBuilder>()
            for (message in messages) {
                try {
                    val sender = message.displayOriginatingAddress ?: continue
                    smsMap.getOrPut(sender) { StringBuilder() }.append(message.displayMessageBody ?: "")
                } catch (e: Exception) {
                    Log.e("SmsReceiver", "Failed to process SMS message: ${e.message}")
                }
            }

            val app = try {
                context.applicationContext as PaySmsApp
            } catch (e: Exception) {
                Log.e("SmsReceiver", "Failed to get app context: ${e.message}")
                return
            }
            val repository = app.repository
            val settingsManager = app.settingsManager

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val settings = settingsManager.settings.first()

                    for ((sender, bodyBuilder) in smsMap) {
                        try {
                            val body = bodyBuilder.toString()

                            if (settings.whitelistEnabled && settings.whitelistedSenders.isNotEmpty()) {
                                val allowed = settings.whitelistedSenders.any {
                                    sender.contains(it, ignoreCase = true)
                                }
                                if (!allowed) continue
                            }

                            if (settings.blacklistedSenders.isNotEmpty()) {
                                val blocked = settings.blacklistedSenders.any {
                                    sender.contains(it, ignoreCase = true)
                                }
                                if (blocked) continue
                            }

                            if (settings.allowedSenders.isNotEmpty()) {
                                val allowed = settings.allowedSenders.any {
                                    sender.contains(it, ignoreCase = true)
                                }
                                if (!allowed) continue
                            }

                            if (!settings.keywords.any { body.contains(it, ignoreCase = true) }) continue

                            val bankRules = try {
                                repository.getEnabledBankRules()
                            } catch (e: Exception) {
                                Log.e("SmsReceiver", "Failed to get bank rules: ${e.message}")
                                emptyList()
                            }
                            val parsed = parser.parse(body, sender, bankRules) ?: continue

                            if (repository.isDuplicate(parsed.smsHash)) continue

                            val apiPayload = gson.toJson(
                                mapOf(
                                    "amount" to parsed.amount,
                                    "currency" to parsed.currency,
                                    "sender" to parsed.senderName,
                                    "bank" to parsed.bankName,
                                    "account" to parsed.accountNumber,
                                    "type" to parsed.transactionType.name,
                                    "timestamp" to parsed.timestamp,
                                    "transactionRef" to parsed.transactionRef,
                                    "smsSender" to parsed.smsSenderNumber,
                                    "balance" to parsed.balance
                                )
                            )

                            val emailBodyText = buildEmailBody(parsed)

                            val transaction = parser.toTransaction(parsed).copy(
                                apiRequestPayload = apiPayload,
                                emailBody = emailBodyText
                            )
                            val transactionId = repository.insertTransaction(transaction)

                            if (settings.notificationsEnabled) {
                                try {
                                    NotificationHelper.showTransactionNotification(
                                        context,
                                        transaction.copy(id = transactionId)
                                    )
                                } catch (e: Exception) {
                                    Log.e("SmsReceiver", "Notification failed: ${e.message}")
                                }
                            }

                            if (settings.apiEnabled && settings.autoSendApi && settings.apiUrl.isNotEmpty()) {
                                repository.insertPendingRequest(
                                    PendingRequest(
                                        transactionId = transactionId,
                                        requestType = RequestType.API_CALL,
                                        payload = apiPayload
                                    )
                                )
                            }

                            if (settings.emailEnabled && settings.autoSendEmail && settings.receiverEmail.isNotEmpty()) {
                                val emailPayload = gson.toJson(
                                    mapOf(
                                        "subject" to "Payment Received: ${parsed.currency} ${String.format("%.2f", parsed.amount)}",
                                        "body" to emailBodyText
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
                        } catch (e: Exception) {
                            Log.e("SmsReceiver", "Error processing SMS from $sender: ${e.message}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SmsReceiver", "Error in SMS processing coroutine: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "SmsReceiver crashed: ${e.message}", e)
        }
    }

    private fun buildEmailBody(parsed: ParsedTransaction): String {
        return try {
            val df = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            buildString {
                appendLine("Payment Received!")
                appendLine()
                appendLine("Amount: ${parsed.currency} ${String.format("%.2f", parsed.amount)}")
                appendLine("From: ${parsed.senderName}")
                appendLine("Bank: ${parsed.bankName}")
                appendLine("Account: ${parsed.accountNumber}")
                appendLine("Type: ${parsed.transactionType}")
                appendLine("Time: ${df.format(java.util.Date(parsed.timestamp))}")
                if (parsed.transactionRef.isNotEmpty()) {
                    appendLine("Ref: ${parsed.transactionRef}")
                }
                if (parsed.balance.isNotEmpty()) {
                    appendLine("Balance: ${parsed.balance}")
                }
                appendLine("SMS Sender: ${parsed.smsSenderNumber}")
            }
        } catch (e: Exception) {
            "Payment notification (details unavailable)"
        }
    }
}
