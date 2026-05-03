package com.paysms.sms

import com.paysms.data.model.BankRule
import com.paysms.data.model.Transaction
import com.paysms.data.model.TransactionType
import java.security.MessageDigest

data class ParsedTransaction(
    val amount: Double,
    val currency: String,
    val senderName: String,
    val bankName: String,
    val accountNumber: String,
    val transactionType: TransactionType,
    val rawSms: String,
    val smsSender: String,
    val smsHash: String
)

class SmsParser {

    companion object {
        private val DEFAULT_BANKS = listOf(
            BankRule(
                bankName = "Commercial Bank of Ethiopia",
                senderKeywords = "CBE,caborBank",
                creditKeywords = "credited,received,deposited,transferred to",
                debitKeywords = "debited,sent,withdrawn,transferred from",
                amountPattern = "ETB\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*ETB|Birr\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*Birr"
            ),
            BankRule(
                bankName = "Abay Bank",
                senderKeywords = "AbayBank,Abay",
                creditKeywords = "credited,received,deposited",
                debitKeywords = "debited,sent,withdrawn",
                amountPattern = "ETB\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*ETB"
            ),
            BankRule(
                bankName = "Telebirr",
                senderKeywords = "Telebirr,127,ethio telecom",
                creditKeywords = "received,credited,sent to you",
                debitKeywords = "sent,paid,transferred",
                amountPattern = "ETB\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*ETB|Birr\\s*([\\d,]+\\.?\\d*)"
            ),
            BankRule(
                bankName = "Awash Bank",
                senderKeywords = "AwashBank,Awash",
                creditKeywords = "credited,received,deposited",
                debitKeywords = "debited,sent,withdrawn",
                amountPattern = "ETB\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*ETB"
            ),
            BankRule(
                bankName = "Dashen Bank",
                senderKeywords = "DashenBank,Dashen",
                creditKeywords = "credited,received,deposited",
                debitKeywords = "debited,sent,withdrawn",
                amountPattern = "ETB\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*ETB"
            ),
            BankRule(
                bankName = "Bank of Abyssinia",
                senderKeywords = "BOA,Abyssinia",
                creditKeywords = "credited,received,deposited",
                debitKeywords = "debited,sent,withdrawn",
                amountPattern = "ETB\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*ETB"
            ),
            BankRule(
                bankName = "Cooperative Bank of Oromia",
                senderKeywords = "CoopBank,Oromia",
                creditKeywords = "credited,received,deposited",
                debitKeywords = "debited,sent,withdrawn",
                amountPattern = "ETB\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*ETB"
            ),
            BankRule(
                bankName = "Wegagen Bank",
                senderKeywords = "Wegagen",
                creditKeywords = "credited,received,deposited",
                debitKeywords = "debited,sent,withdrawn",
                amountPattern = "ETB\\s*([\\d,]+\\.?\\d*)|([\\d,]+\\.?\\d*)\\s*ETB"
            )
        )

        private val AMOUNT_PATTERNS = listOf(
            Regex("ETB\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
            Regex("([\\d,]+\\.?\\d*)\\s*ETB", RegexOption.IGNORE_CASE),
            Regex("Birr\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
            Regex("([\\d,]+\\.?\\d*)\\s*Birr", RegexOption.IGNORE_CASE),
            Regex("amount[:\\s]*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
            Regex("br\\.?\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
        )

        private val ACCOUNT_PATTERNS = listOf(
            Regex("account[:\\s]*([*xX\\d]+)", RegexOption.IGNORE_CASE),
            Regex("a/c[:\\s]*([*xX\\d]+)", RegexOption.IGNORE_CASE),
            Regex("acct[:\\s]*([*xX\\d]+)", RegexOption.IGNORE_CASE)
        )

        private val NAME_PATTERNS = listOf(
            Regex("from\\s+([A-Za-z\\s]+?)(?:\\s+(?:on|at|for|ETB|Birr|amount|account|\\d))", RegexOption.IGNORE_CASE),
            Regex("by\\s+([A-Za-z\\s]+?)(?:\\s+(?:on|at|for|ETB|Birr|amount|account|\\d))", RegexOption.IGNORE_CASE),
            Regex("sender[:\\s]*([A-Za-z\\s]+?)(?:\\s*[,.]|$)", RegexOption.IGNORE_CASE)
        )
    }

    fun parse(smsBody: String, smsSender: String, customRules: List<BankRule> = emptyList()): ParsedTransaction? {
        val allRules = customRules.ifEmpty { DEFAULT_BANKS }
        val matchedRule = findMatchingRule(smsBody, smsSender, allRules)
        val bankName = matchedRule?.bankName ?: detectBankFromSms(smsBody, smsSender)

        if (bankName.isEmpty() && !looksLikeTransaction(smsBody)) {
            return null
        }

        val amount = extractAmount(smsBody, matchedRule) ?: return null
        val transactionType = detectTransactionType(smsBody, matchedRule)
        val senderName = extractSenderName(smsBody)
        val accountNumber = extractAccountNumber(smsBody)
        val hash = generateHash(smsBody, smsSender)

        return ParsedTransaction(
            amount = amount,
            currency = "ETB",
            senderName = senderName,
            bankName = bankName.ifEmpty { "Unknown Bank" },
            accountNumber = accountNumber,
            transactionType = transactionType,
            rawSms = smsBody,
            smsSender = smsSender,
            smsHash = hash
        )
    }

    fun toTransaction(parsed: ParsedTransaction): Transaction {
        return Transaction(
            amount = parsed.amount,
            currency = parsed.currency,
            senderName = parsed.senderName,
            bankName = parsed.bankName,
            accountNumber = parsed.accountNumber,
            transactionType = parsed.transactionType,
            rawSms = parsed.rawSms,
            smsSender = parsed.smsSender,
            smsHash = parsed.smsHash
        )
    }

    private fun findMatchingRule(smsBody: String, smsSender: String, rules: List<BankRule>): BankRule? {
        return rules.firstOrNull { rule ->
            rule.senderKeywords.split(",").any { keyword ->
                smsSender.contains(keyword.trim(), ignoreCase = true) ||
                    smsBody.contains(keyword.trim(), ignoreCase = true)
            }
        }
    }

    private fun detectBankFromSms(smsBody: String, smsSender: String): String {
        val combined = "$smsSender $smsBody"
        return when {
            combined.contains("CBE", ignoreCase = true) ||
                combined.contains("Commercial Bank", ignoreCase = true) -> "Commercial Bank of Ethiopia"
            combined.contains("Telebirr", ignoreCase = true) -> "Telebirr"
            combined.contains("Abay", ignoreCase = true) -> "Abay Bank"
            combined.contains("Awash", ignoreCase = true) -> "Awash Bank"
            combined.contains("Dashen", ignoreCase = true) -> "Dashen Bank"
            combined.contains("BOA", ignoreCase = true) ||
                combined.contains("Abyssinia", ignoreCase = true) -> "Bank of Abyssinia"
            combined.contains("Wegagen", ignoreCase = true) -> "Wegagen Bank"
            combined.contains("Oromia", ignoreCase = true) ||
                combined.contains("Coop", ignoreCase = true) -> "Cooperative Bank of Oromia"
            else -> ""
        }
    }

    private fun looksLikeTransaction(smsBody: String): Boolean {
        val transactionKeywords = listOf(
            "credited", "debited", "received", "sent", "transferred",
            "deposited", "withdrawn", "payment", "balance", "ETB", "Birr"
        )
        return transactionKeywords.any { smsBody.contains(it, ignoreCase = true) }
    }

    private fun extractAmount(smsBody: String, rule: BankRule?): Double? {
        if (rule != null && rule.amountPattern.isNotEmpty()) {
            val customRegex = Regex(rule.amountPattern, RegexOption.IGNORE_CASE)
            val match = customRegex.find(smsBody)
            if (match != null) {
                val amountStr = match.groupValues.drop(1).firstOrNull { it.isNotEmpty() }
                if (amountStr != null) {
                    return amountStr.replace(",", "").toDoubleOrNull()
                }
            }
        }

        for (pattern in AMOUNT_PATTERNS) {
            val match = pattern.find(smsBody)
            if (match != null) {
                val amountStr = match.groupValues[1].replace(",", "")
                val amount = amountStr.toDoubleOrNull()
                if (amount != null && amount > 0) return amount
            }
        }
        return null
    }

    private fun detectTransactionType(smsBody: String, rule: BankRule?): TransactionType {
        val creditKeywords = rule?.creditKeywords?.split(",")?.map { it.trim() }
            ?: listOf("credited", "received", "deposited", "transferred to", "sent to you")
        val debitKeywords = rule?.debitKeywords?.split(",")?.map { it.trim() }
            ?: listOf("debited", "sent", "withdrawn", "transferred from", "paid")

        return when {
            creditKeywords.any { smsBody.contains(it, ignoreCase = true) } -> TransactionType.CREDIT
            debitKeywords.any { smsBody.contains(it, ignoreCase = true) } -> TransactionType.DEBIT
            else -> TransactionType.UNKNOWN
        }
    }

    private fun extractSenderName(smsBody: String): String {
        for (pattern in NAME_PATTERNS) {
            val match = pattern.find(smsBody)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }
        return "Unknown"
    }

    private fun extractAccountNumber(smsBody: String): String {
        for (pattern in ACCOUNT_PATTERNS) {
            val match = pattern.find(smsBody)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }
        return ""
    }

    private fun generateHash(smsBody: String, smsSender: String): String {
        val input = "$smsSender|$smsBody"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
