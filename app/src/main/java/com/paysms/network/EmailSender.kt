package com.paysms.network

import com.paysms.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSender {

    suspend fun sendEmail(
        subject: String,
        body: String,
        settings: AppSettings
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", settings.smtpHost)
                    put("mail.smtp.port", settings.smtpPort.toString())
                    put("mail.smtp.ssl.trust", settings.smtpHost)
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(settings.senderEmail, settings.senderPassword)
                    }
                })

                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(settings.senderEmail))
                    setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse(settings.receiverEmail)
                    )
                    setSubject(subject)
                    setText(body)
                }

                Transport.send(message)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
