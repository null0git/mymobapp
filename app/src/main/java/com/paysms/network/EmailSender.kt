package com.paysms.network

import android.util.Log
import com.paysms.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties

data class EmailResult(
    val success: Boolean,
    val message: String
)

class EmailSender {

    suspend fun sendEmail(
        subject: String,
        body: String,
        settings: AppSettings
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (settings.senderEmail.isBlank() || settings.receiverEmail.isBlank() || settings.senderPassword.isBlank()) {
                    return@withContext false
                }

                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", settings.smtpHost)
                    put("mail.smtp.port", settings.smtpPort.toString())
                    put("mail.smtp.ssl.trust", settings.smtpHost)
                    put("mail.smtp.connectiontimeout", "15000")
                    put("mail.smtp.timeout", "15000")
                    put("mail.smtp.writetimeout", "15000")
                }

                val session = javax.mail.Session.getInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                        return javax.mail.PasswordAuthentication(settings.senderEmail, settings.senderPassword)
                    }
                })

                val message = javax.mail.internet.MimeMessage(session).apply {
                    setFrom(javax.mail.internet.InternetAddress(settings.senderEmail))
                    setRecipients(
                        javax.mail.Message.RecipientType.TO,
                        javax.mail.internet.InternetAddress.parse(settings.receiverEmail)
                    )
                    setSubject(subject)
                    setText(body)
                }

                javax.mail.Transport.send(message)
                true
            } catch (e: Exception) {
                Log.e("EmailSender", "Send email failed: ${e.message}")
                false
            }
        }
    }

    suspend fun testConnection(settings: AppSettings): EmailResult {
        return withContext(Dispatchers.IO) {
            try {
                if (settings.senderEmail.isBlank()) {
                    return@withContext EmailResult(false, "Sender email is empty")
                }
                if (settings.senderPassword.isBlank()) {
                    return@withContext EmailResult(false, "Email password is empty")
                }
                if (settings.receiverEmail.isBlank()) {
                    return@withContext EmailResult(false, "Receiver email is empty")
                }
                if (!settings.senderEmail.contains("@") || !settings.receiverEmail.contains("@")) {
                    return@withContext EmailResult(false, "Invalid email format")
                }
                if (settings.smtpHost.isBlank()) {
                    return@withContext EmailResult(false, "SMTP host is empty")
                }

                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", settings.smtpHost)
                    put("mail.smtp.port", settings.smtpPort.toString())
                    put("mail.smtp.ssl.trust", settings.smtpHost)
                    put("mail.smtp.connectiontimeout", "10000")
                    put("mail.smtp.timeout", "10000")
                    put("mail.smtp.writetimeout", "10000")
                }

                val session = javax.mail.Session.getInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                        return javax.mail.PasswordAuthentication(settings.senderEmail, settings.senderPassword)
                    }
                })

                val message = javax.mail.internet.MimeMessage(session).apply {
                    setFrom(javax.mail.internet.InternetAddress(settings.senderEmail))
                    setRecipients(
                        javax.mail.Message.RecipientType.TO,
                        javax.mail.internet.InternetAddress.parse(settings.receiverEmail)
                    )
                    setSubject("PaySMS Test Email")
                    setText("This is a test email from PaySMS to verify your email configuration is working correctly.\n\nTimestamp: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}")
                }

                javax.mail.Transport.send(message)
                EmailResult(true, "Test email sent successfully to ${settings.receiverEmail}")
            } catch (e: javax.mail.AuthenticationFailedException) {
                EmailResult(false, "Authentication failed: Check email and password. For Gmail, use an App Password.")
            } catch (e: javax.mail.MessagingException) {
                EmailResult(false, "SMTP error: ${e.message ?: "Connection failed"}")
            } catch (e: java.net.ConnectException) {
                EmailResult(false, "Connection refused: Check SMTP host and port")
            } catch (e: java.net.UnknownHostException) {
                EmailResult(false, "Cannot resolve SMTP host: ${settings.smtpHost}")
            } catch (e: java.net.SocketTimeoutException) {
                EmailResult(false, "Connection timed out: Check SMTP host and port")
            } catch (e: Exception) {
                EmailResult(false, "${e.javaClass.simpleName}: ${e.message ?: "Unknown error"}")
            }
        }
    }
}
