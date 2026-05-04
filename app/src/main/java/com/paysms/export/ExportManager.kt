package com.paysms.export

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.paysms.data.model.Transaction
import com.paysms.util.CurrencyUtils
import com.paysms.util.DateUtils
import java.io.File
import java.io.FileWriter

enum class ExportFormat(val extension: String, val mimeType: String) {
    CSV("csv", "text/csv"),
    EXCEL("xls", "application/vnd.ms-excel"),
    PDF("txt", "text/plain")
}

class ExportManager(private val context: Context) {

    fun export(transactions: List<Transaction>, format: ExportFormat): File? {
        return try {
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "PaySMS")
            if (!dir.exists()) dir.mkdirs()

            val timestamp = System.currentTimeMillis()
            val file = File(dir, "paysms_export_$timestamp.${format.extension}")

            when (format) {
                ExportFormat.CSV -> exportCsv(transactions, file)
                ExportFormat.EXCEL -> exportExcel(transactions, file)
                ExportFormat.PDF -> exportPdfText(transactions, file)
            }

            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun exportCsv(transactions: List<Transaction>, file: File) {
        FileWriter(file).use { writer ->
            writer.appendLine("ID,Date,Time,Amount,Currency,Type,Sender,Bank,Account,Ref,SMS Sender,Balance,API Sent,Email Sent")
            for (tx in transactions) {
                val date = DateUtils.formatDate(tx.timestamp)
                val time = DateUtils.formatTime(tx.timestamp)
                val amount = String.format("%.2f", tx.amount)
                val sender = tx.senderName.replace(",", ";")
                val bank = tx.bankName.replace(",", ";")
                writer.appendLine("${tx.id},$date,$time,$amount,${tx.currency},${tx.transactionType},$sender,$bank,${tx.accountNumber},${tx.transactionRef},${tx.smsSenderNumber},${tx.balance},${tx.apiSent},${tx.emailSent}")
            }
        }
    }

    private fun exportExcel(transactions: List<Transaction>, file: File) {
        FileWriter(file).use { writer ->
            writer.appendLine("ID\tDate\tTime\tAmount\tCurrency\tType\tSender\tBank\tAccount\tRef\tSMS Sender\tBalance\tAPI Sent\tEmail Sent")
            for (tx in transactions) {
                val date = DateUtils.formatDate(tx.timestamp)
                val time = DateUtils.formatTime(tx.timestamp)
                val amount = String.format("%.2f", tx.amount)
                writer.appendLine("${tx.id}\t$date\t$time\t$amount\t${tx.currency}\t${tx.transactionType}\t${tx.senderName}\t${tx.bankName}\t${tx.accountNumber}\t${tx.transactionRef}\t${tx.smsSenderNumber}\t${tx.balance}\t${tx.apiSent}\t${tx.emailSent}")
            }
        }
    }

    private fun exportPdfText(transactions: List<Transaction>, file: File) {
        FileWriter(file).use { writer ->
            writer.appendLine("=" .repeat(60))
            writer.appendLine("  PaySMS Transaction Report")
            writer.appendLine("  Generated: ${DateUtils.formatDateTime(System.currentTimeMillis())}")
            writer.appendLine("  Total Transactions: ${transactions.size}")
            writer.appendLine("=" .repeat(60))
            writer.appendLine()

            val totalCredit = transactions.filter { it.transactionType == com.paysms.data.model.TransactionType.CREDIT }.sumOf { it.amount }
            val totalDebit = transactions.filter { it.transactionType == com.paysms.data.model.TransactionType.DEBIT }.sumOf { it.amount }

            writer.appendLine("  Summary:")
            writer.appendLine("  Total Received: ${CurrencyUtils.formatAmount(totalCredit)}")
            writer.appendLine("  Total Sent: ${CurrencyUtils.formatAmount(totalDebit)}")
            writer.appendLine("  Net: ${CurrencyUtils.formatAmount(totalCredit - totalDebit)}")
            writer.appendLine()
            writer.appendLine("-".repeat(60))

            for (tx in transactions) {
                writer.appendLine()
                writer.appendLine("  #${tx.id} | ${DateUtils.formatDateTime(tx.timestamp)}")
                writer.appendLine("  ${tx.transactionType}: ${CurrencyUtils.formatAmount(tx.amount, tx.currency)}")
                writer.appendLine("  From: ${tx.senderName}")
                writer.appendLine("  Bank: ${tx.bankName}")
                if (tx.accountNumber.isNotEmpty()) writer.appendLine("  Account: ${tx.accountNumber}")
                if (tx.transactionRef.isNotEmpty()) writer.appendLine("  Ref: ${tx.transactionRef}")
                if (tx.balance.isNotEmpty()) writer.appendLine("  Balance: ${tx.balance}")
                writer.appendLine("  SMS Sender: ${tx.smsSenderNumber.ifEmpty { tx.smsSender }}")
                writer.appendLine("  API: ${if (tx.apiSent) "Sent" else "Pending"} | Email: ${if (tx.emailSent) "Sent" else "Pending"}")
                writer.appendLine("  -".repeat(30))
            }
        }
    }

    fun shareFile(file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = when (file.extension) {
                "csv" -> "text/csv"
                "xls" -> "application/vnd.ms-excel"
                else -> "text/plain"
            }
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
