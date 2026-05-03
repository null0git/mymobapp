package com.paysms.offline

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.paysms.PaySmsApp
import com.paysms.data.model.RequestType
import com.paysms.network.ApiClient
import com.paysms.network.EmailSender
import com.paysms.notification.NotificationHelper
import kotlinx.coroutines.flow.first

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val apiClient = ApiClient()
    private val emailSender = EmailSender()
    private val gson = Gson()

    override suspend fun doWork(): Result {
        val app = applicationContext as PaySmsApp
        val repository = app.repository
        val settings = app.settingsManager.settings.first()

        val pendingRequests = repository.getPendingRequests()
        if (pendingRequests.isEmpty()) return Result.success()

        var successCount = 0
        var failCount = 0

        for (request in pendingRequests) {
            val success = when (request.requestType) {
                RequestType.API_CALL -> {
                    if (settings.apiEnabled) {
                        val response = apiClient.sendTransaction(request.payload, settings)
                        if (response.success) {
                            val transaction = repository.run {
                                val dao = app.database.transactionDao()
                                dao.getTransactionById(request.transactionId)
                            }
                            transaction?.let {
                                repository.updateTransaction(
                                    it.copy(
                                        apiSent = true,
                                        apiResponse = response.body,
                                        apiStatusCode = response.statusCode
                                    )
                                )
                            }
                            true
                        } else {
                            false
                        }
                    } else {
                        true
                    }
                }
                RequestType.EMAIL -> {
                    if (settings.emailEnabled) {
                        val emailData: Map<String, String> = gson.fromJson(
                            request.payload,
                            object : TypeToken<Map<String, String>>() {}.type
                        )
                        emailSender.sendEmail(
                            subject = emailData["subject"] ?: "Payment Notification",
                            body = emailData["body"] ?: "",
                            settings = settings
                        )
                    } else {
                        true
                    }
                }
            }

            if (success) {
                repository.deletePendingRequest(request)
                successCount++
            } else {
                repository.updatePendingRequest(
                    request.copy(
                        retryCount = request.retryCount + 1,
                        lastAttempt = System.currentTimeMillis()
                    )
                )
                failCount++
            }
        }

        if (successCount > 0) {
            NotificationHelper.showSyncNotification(
                applicationContext,
                "Synced $successCount request(s)${if (failCount > 0) ", $failCount failed" else ""}"
            )
        }

        return if (failCount > 0) Result.retry() else Result.success()
    }

    companion object {
        private const val WORK_NAME = "paysms_sync"

        fun enqueue(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
        }
    }
}
