package com.paysms

import android.app.Application
import android.util.Log
import com.paysms.data.database.PaySmsDatabase
import com.paysms.data.repository.SettingsManager
import com.paysms.data.repository.TransactionRepository
import com.paysms.notification.NotificationHelper

class PaySmsApp : Application() {

    val database by lazy { PaySmsDatabase.getDatabase(this) }
    val repository by lazy {
        TransactionRepository(
            database.transactionDao(),
            database.pendingRequestDao(),
            database.bankRuleDao()
        )
    }
    val settingsManager by lazy { SettingsManager(this) }

    override fun onCreate() {
        super.onCreate()
        try {
            NotificationHelper.createNotificationChannels(this)
        } catch (e: Exception) {
            Log.e("PaySMS", "Failed to create notification channels: ${e.message}")
        }
    }
}
