package com.paysms.network

import android.util.Log
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
    val body: String,
    val requestPayload: String = "",
    val responseTimeMs: Long = 0
)

class ApiClient {

    private val client by lazy {
        try {
            OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()
        } catch (e: Exception) {
            Log.e("ApiClient", "Failed to create OkHttpClient: ${e.message}")
            OkHttpClient()
        }
    }

    suspend fun sendTransaction(payload: String, settings: AppSettings): ApiResponse {
        return withContext(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            try {
                val url = settings.apiUrl.trim()
                if (url.isBlank()) {
                    return@withContext ApiResponse(
                        success = false, statusCode = -1,
                        body = "API URL is empty", requestPayload = payload
                    )
                }
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    return@withContext ApiResponse(
                        success = false, statusCode = -1,
                        body = "URL must start with http:// or https://", requestPayload = payload
                    )
                }

                try {
                    java.net.URL(url)
                } catch (e: Exception) {
                    return@withContext ApiResponse(
                        success = false, statusCode = -1,
                        body = "Malformed URL: ${e.message}", requestPayload = payload
                    )
                }

                val requestBuilder = try {
                    Request.Builder().url(url)
                } catch (e: Exception) {
                    return@withContext ApiResponse(
                        success = false, statusCode = -1,
                        body = "Invalid URL: ${e.message}", requestPayload = payload
                    )
                }

                try {
                    for ((key, value) in settings.apiHeaders) {
                        if (key.isNotBlank()) {
                            requestBuilder.addHeader(key.trim(), value.trim())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ApiClient", "Header error: ${e.message}")
                }

                if (settings.apiKey.isNotBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer ${settings.apiKey.trim()}")
                }

                requestBuilder.addHeader("Content-Type", "application/json")

                try {
                    when (settings.apiMethod.uppercase()) {
                        "GET" -> requestBuilder.get()
                        "PUT" -> requestBuilder.put(payload.toRequestBody("application/json".toMediaType()))
                        else -> requestBuilder.post(payload.toRequestBody("application/json".toMediaType()))
                    }
                } catch (e: Exception) {
                    return@withContext ApiResponse(
                        success = false, statusCode = -1,
                        body = "Request build error: ${e.message}", requestPayload = payload
                    )
                }

                val response = client.newCall(requestBuilder.build()).execute()
                val elapsed = System.currentTimeMillis() - startTime
                val responseBody = try {
                    response.body?.string() ?: ""
                } catch (e: Exception) {
                    "(Could not read response body)"
                }
                ApiResponse(
                    success = response.isSuccessful,
                    statusCode = response.code,
                    body = responseBody,
                    requestPayload = payload,
                    responseTimeMs = elapsed
                )
            } catch (e: java.net.UnknownHostException) {
                val elapsed = System.currentTimeMillis() - startTime
                ApiResponse(false, -1, "DNS error: Cannot resolve host", payload, elapsed)
            } catch (e: java.net.ConnectException) {
                val elapsed = System.currentTimeMillis() - startTime
                ApiResponse(false, -1, "Connection refused: ${e.message}", payload, elapsed)
            } catch (e: java.net.SocketTimeoutException) {
                val elapsed = System.currentTimeMillis() - startTime
                ApiResponse(false, -1, "Connection timed out", payload, elapsed)
            } catch (e: javax.net.ssl.SSLException) {
                val elapsed = System.currentTimeMillis() - startTime
                ApiResponse(false, -1, "SSL error: ${e.message}", payload, elapsed)
            } catch (e: IllegalArgumentException) {
                val elapsed = System.currentTimeMillis() - startTime
                ApiResponse(false, -1, "Invalid URL: ${e.message}", payload, elapsed)
            } catch (e: Exception) {
                val elapsed = System.currentTimeMillis() - startTime
                ApiResponse(false, -1, "${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}", payload, elapsed)
            }
        }
    }

    suspend fun testConnection(settings: AppSettings): ApiResponse {
        return try {
            val testPayload = """{"test": true, "source": "PaySMS", "timestamp": ${System.currentTimeMillis()}}"""
            sendTransaction(testPayload, settings)
        } catch (e: Exception) {
            ApiResponse(false, -1, "Test failed: ${e.message ?: "Unknown error"}")
        }
    }
}
