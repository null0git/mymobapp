package com.paysms.network

import com.paysms.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

data class ApiResponse(
    val success: Boolean,
    val statusCode: Int,
    val body: String
)

class ApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun sendTransaction(payload: String, settings: AppSettings): ApiResponse {
        return withContext(Dispatchers.IO) {
            try {
                val requestBuilder = Request.Builder().url(settings.apiUrl)

                for ((key, value) in settings.apiHeaders) {
                    requestBuilder.addHeader(key, value)
                }

                if (settings.apiKey.isNotEmpty()) {
                    requestBuilder.addHeader("Authorization", "Bearer ${settings.apiKey}")
                }

                requestBuilder.addHeader("Content-Type", "application/json")

                when (settings.apiMethod.uppercase()) {
                    "POST" -> {
                        val body = payload.toRequestBody("application/json".toMediaType())
                        requestBuilder.post(body)
                    }
                    "GET" -> {
                        requestBuilder.get()
                    }
                    "PUT" -> {
                        val body = payload.toRequestBody("application/json".toMediaType())
                        requestBuilder.put(body)
                    }
                    else -> {
                        val body = payload.toRequestBody("application/json".toMediaType())
                        requestBuilder.post(body)
                    }
                }

                val response = client.newCall(requestBuilder.build()).execute()
                ApiResponse(
                    success = response.isSuccessful,
                    statusCode = response.code,
                    body = response.body?.string() ?: ""
                )
            } catch (e: Exception) {
                ApiResponse(
                    success = false,
                    statusCode = -1,
                    body = e.message ?: "Unknown error"
                )
            }
        }
    }
}
