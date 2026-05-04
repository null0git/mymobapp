package com.paysms.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.paysms.MainActivity
import com.paysms.R
import com.paysms.data.model.Transaction

object NotificationHelper {

    private const val CHANNEL_TRANSACTION = "transaction_channel"
    private const val CHANNEL_SYNC = "sync_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java)

            val transactionChannel = NotificationChannel(
                CHANNEL_TRANSACTION,
                "Payment Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming payments"
                enableVibration(true)
            }

            val syncChannel = NotificationChannel(
                CHANNEL_SYNC,
                "Sync Notifications",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Background sync status"
            }

            manager.createNotificationChannel(transactionChannel)
            manager.createNotificationChannel(syncChannel)
        }
    }

    fun showTransactionNotification(context: Context, transaction: Transaction) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val typeLabel = when (transaction.transactionType) {
            com.paysms.data.model.TransactionType.CREDIT -> "Received"
            com.paysms.data.model.TransactionType.DEBIT -> "Sent"
            else -> "Transaction"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_TRANSACTION)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("💰 $typeLabel: ${transaction.currency} ${String.format("%.2f", transaction.amount)}")
            .setContentText("From: ${transaction.senderName} via ${transaction.bankName}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(transaction.id.toInt(), notification)
    }

    fun showSyncNotification(context: Context, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_SYNC)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("PaySMS Sync")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(999, notification)
    }
}
