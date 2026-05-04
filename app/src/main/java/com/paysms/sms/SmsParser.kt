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
    val timestamp: Long,
    val rawSms: String,
    val smsSender: String,
    val smsHash: String,
    val transactionRef: String = "",
    val smsSenderNumber: String = "",
    val balance: String = ""
)

data class BankPattern(
    val bankName: String,
    val identifyKeywords: List<String>,
    val amountPatterns: List<Regex>,
    val senderNamePatterns: List<Regex>,
    val accountPatterns: List<Regex>,
    val creditKeywords: List<String>,
    val debitKeywords: List<String>,
    val transactionRefPatterns: List<Regex> = emptyList(),
    val balancePatterns: List<Regex> = emptyList()
)

class SmsParser {

    companion object {

        val BANK_PATTERNS: List<BankPattern> = listOf(

            // Abay Bank
            BankPattern(
                bankName = "Abay Bank",
                identifyKeywords = listOf("Abay Bank", "AbayBank", "abaymobile"),
                amountPatterns = listOf(
                    Regex("by\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                ),
                senderNamePatterns = listOf(
                    Regex("from\\s+([A-Z][A-Z\\s]+?)\\s+by\\s+ETB", RegexOption.IGNORE_CASE),
                    Regex("to\\s+([A-Z][A-Z\\s]+?)\\s+by\\s+ETB", RegexOption.IGNORE_CASE)
                ),
                accountPatterns = listOf(
                    Regex("account\\s+([*\\d]+)", RegexOption.IGNORE_CASE)
                ),
                creditKeywords = listOf("transfer was made to account", "credited", "received", "deposited"),
                debitKeywords = listOf("transfer was made from account", "debited", "sent", "withdrawn"),
                transactionRefPatterns = listOf(
                    Regex("info/([A-Z0-9]+)", RegexOption.IGNORE_CASE),
                    Regex("FT[A-Z0-9]+", RegexOption.IGNORE_CASE)
                ),
                balancePatterns = listOf(
                    Regex("current\\s+balance\\s+is\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                )
            ),

            // Bank of Abyssinia (BOA)
            BankPattern(
                bankName = "Bank of Abyssinia",
                identifyKeywords = listOf("Bank of Abyssinia", "bankofabyssinia", "BOA"),
                amountPatterns = listOf(
                    Regex("(?:credited|debited)\\s+with\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                ),
                senderNamePatterns = listOf(
                    Regex("by\\s+([A-Za-z][A-Za-z\\s]+?)\\s*\\.\\s*Available", RegexOption.IGNORE_CASE),
                    Regex("to\\s+([A-Za-z][A-Za-z\\s]+?)\\s*\\.\\s*Available", RegexOption.IGNORE_CASE)
                ),
                accountPatterns = listOf(
                    Regex("your\\s+account\\s+([\\d*]+)", RegexOption.IGNORE_CASE),
                    Regex("account\\s+([\\d*]+)", RegexOption.IGNORE_CASE)
                ),
                creditKeywords = listOf("credited"),
                debitKeywords = listOf("debited"),
                transactionRefPatterns = listOf(
                    Regex("trx=([A-Z0-9]+)", RegexOption.IGNORE_CASE),
                    Regex("FT[A-Z0-9]+", RegexOption.IGNORE_CASE)
                ),
                balancePatterns = listOf(
                    Regex("Available\\s+Balance:\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                )
            ),

            // Dashen Bank
            BankPattern(
                bankName = "Dashen Bank",
                identifyKeywords = listOf("Dashen", "DashenBank", "Dashen Super App"),
                amountPatterns = listOf(
                    Regex("(?:credited|debited)\\s+with\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                ),
                senderNamePatterns = listOf(
                    Regex("from\\s+([A-Z][A-Z\\s]+?)\\s+on\\s+\\d", RegexOption.IGNORE_CASE),
                    Regex("to\\s+([A-Z][A-Z\\s]+?)\\s+on\\s+\\d", RegexOption.IGNORE_CASE)
                ),
                accountPatterns = listOf(
                    Regex("your\\s+account\\s+([\\d*]+)", RegexOption.IGNORE_CASE),
                    Regex("account\\s+([\\d*]+)", RegexOption.IGNORE_CASE)
                ),
                creditKeywords = listOf("credited"),
                debitKeywords = listOf("debited"),
                transactionRefPatterns = listOf(
                    Regex("Ref[\\s:]*([A-Z0-9]+)", RegexOption.IGNORE_CASE)
                ),
                balancePatterns = listOf(
                    Regex("current\\s+balance\\s+is\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                )
            ),

            // Commercial Bank of Ethiopia (CBE)
            BankPattern(
                bankName = "Commercial Bank of Ethiopia",
                identifyKeywords = listOf("CBE", "cbe.com.et", "Banking with CBE"),
                amountPatterns = listOf(
                    Regex("(?:Credited|Debited)\\s+with\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                ),
                senderNamePatterns = listOf(
                    Regex("from\\s+([A-Za-z][A-Za-z\\s]+?),?\\s+on\\s+\\d", RegexOption.IGNORE_CASE),
                    Regex("to\\s+([A-Za-z][A-Za-z\\s]+?),?\\s+on\\s+\\d", RegexOption.IGNORE_CASE)
                ),
                accountPatterns = listOf(
                    Regex("(?:your\\s+)?Account\\s+([\\d*]+)", RegexOption.IGNORE_CASE)
                ),
                creditKeywords = listOf("Credited"),
                debitKeywords = listOf("Debited"),
                transactionRefPatterns = listOf(
                    Regex("Ref\\s+No\\s+([A-Z0-9]+)", RegexOption.IGNORE_CASE),
                    Regex("\\?id=([A-Z0-9]+)", RegexOption.IGNORE_CASE)
                ),
                balancePatterns = listOf(
                    Regex("Current\\s+Balance\\s+is\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                )
            ),

            // Telebirr
            BankPattern(
                bankName = "Telebirr",
                identifyKeywords = listOf("Telebirr", "ethio telecom", "127"),
                amountPatterns = listOf(
                    Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("Birr\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("([\\d,]+\\.?\\d*)\\s+ETB", RegexOption.IGNORE_CASE)
                ),
                senderNamePatterns = listOf(
                    Regex("from\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|for|ETB|Birr|\\d)", RegexOption.IGNORE_CASE),
                    Regex("by\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|for|ETB|Birr|\\d)", RegexOption.IGNORE_CASE)
                ),
                accountPatterns = listOf(
                    Regex("account[:\\s]*([*\\d]+)", RegexOption.IGNORE_CASE)
                ),
                creditKeywords = listOf("received", "credited", "sent to you", "deposited"),
                debitKeywords = listOf("sent", "paid", "transferred", "debited"),
                transactionRefPatterns = listOf(
                    Regex("Ref[:\\s]*([A-Z0-9]+)", RegexOption.IGNORE_CASE)
                ),
                balancePatterns = listOf(
                    Regex("balance[:\\s]*(?:ETB|Birr)?\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                )
            ),

            // Awash Bank
            BankPattern(
                bankName = "Awash Bank",
                identifyKeywords = listOf("Awash", "AwashBank"),
                amountPatterns = listOf(
                    Regex("(?:credited|debited)\\s+with\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                ),
                senderNamePatterns = listOf(
                    Regex("from\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|\\.|,)", RegexOption.IGNORE_CASE),
                    Regex("by\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|\\.|,)", RegexOption.IGNORE_CASE)
                ),
                accountPatterns = listOf(
                    Regex("account\\s+([*\\d]+)", RegexOption.IGNORE_CASE)
                ),
                creditKeywords = listOf("credited", "received", "deposited"),
                debitKeywords = listOf("debited", "sent", "withdrawn"),
                transactionRefPatterns = listOf(
                    Regex("Ref[:\\s]*([A-Z0-9]+)", RegexOption.IGNORE_CASE)
                ),
                balancePatterns = listOf(
                    Regex("balance[:\\s]*(?:ETB)?\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                )
            ),

            // Wegagen Bank
            BankPattern(
                bankName = "Wegagen Bank",
                identifyKeywords = listOf("Wegagen"),
                amountPatterns = listOf(
                    Regex("(?:credited|debited)\\s+with\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                ),
                senderNamePatterns = listOf(
                    Regex("from\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|\\.|,)", RegexOption.IGNORE_CASE),
                    Regex("by\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|\\.|,)", RegexOption.IGNORE_CASE)
                ),
                accountPatterns = listOf(
                    Regex("account\\s+([*\\d]+)", RegexOption.IGNORE_CASE)
                ),
                creditKeywords = listOf("credited", "received", "deposited"),
                debitKeywords = listOf("debited", "sent", "withdrawn"),
                transactionRefPatterns = listOf(
                    Regex("Ref[:\\s]*([A-Z0-9]+)", RegexOption.IGNORE_CASE)
                ),
                balancePatterns = listOf(
                    Regex("balance[:\\s]*(?:ETB)?\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                )
            ),

            // Cooperative Bank of Oromia
            BankPattern(
                bankName = "Cooperative Bank of Oromia",
                identifyKeywords = listOf("CoopBank", "Oromia", "Cooperative Bank"),
                amountPatterns = listOf(
                    Regex("(?:credited|debited)\\s+with\\s+ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
                    Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                ),
                senderNamePatterns = listOf(
                    Regex("from\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|\\.|,)", RegexOption.IGNORE_CASE),
                    Regex("by\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|\\.|,)", RegexOption.IGNORE_CASE)
                ),
                accountPatterns = listOf(
                    Regex("account\\s+([*\\d]+)", RegexOption.IGNORE_CASE)
                ),
                creditKeywords = listOf("credited", "received", "deposited"),
                debitKeywords = listOf("debited", "sent", "withdrawn"),
                transactionRefPatterns = listOf(
                    Regex("Ref[:\\s]*([A-Z0-9]+)", RegexOption.IGNORE_CASE)
                ),
                balancePatterns = listOf(
                    Regex("balance[:\\s]*(?:ETB)?\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
                )
            )
        )

        private val GENERIC_AMOUNT_PATTERNS = listOf(
            Regex("ETB\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
            Regex("([\\d,]+\\.?\\d*)\\s+ETB", RegexOption.IGNORE_CASE),
            Regex("Birr\\s+([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE),
            Regex("([\\d,]+\\.?\\d*)\\s+Birr", RegexOption.IGNORE_CASE)
        )

        private val GENERIC_NAME_PATTERNS = listOf(
            Regex("from\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|for|by|,|\\.|ETB|Birr|\\d)", RegexOption.IGNORE_CASE),
            Regex("by\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|for|,|\\.|ETB|Birr|\\d)", RegexOption.IGNORE_CASE),
            Regex("to\\s+([A-Za-z][A-Za-z\\s]+?)\\s+(?:on|at|for|by|,|\\.|ETB|Birr|\\d)", RegexOption.IGNORE_CASE)
        )

        private val GENERIC_ACCOUNT_PATTERNS = listOf(
            Regex("(?:your\\s+)?account\\s+([*\\d]+)", RegexOption.IGNORE_CASE),
            Regex("a/c[:\\s]*([*\\d]+)", RegexOption.IGNORE_CASE),
            Regex("acct[:\\s]*([*\\d]+)", RegexOption.IGNORE_CASE)
        )

        private val GENERIC_REF_PATTERNS = listOf(
            Regex("Ref\\s*(?:No)?[:\\s]*([A-Z0-9]{6,})", RegexOption.IGNORE_CASE),
            Regex("FT[A-Z0-9]{8,}", RegexOption.IGNORE_CASE),
            Regex("\\?(?:id|trx|ref)=([A-Z0-9]+)", RegexOption.IGNORE_CASE)
        )

        private val GENERIC_BALANCE_PATTERNS = listOf(
            Regex("(?:current|available)?\\s*balance[:\\s]*(?:is)?\\s*(?:ETB)?\\s*([\\d,]+\\.?\\d*)", RegexOption.IGNORE_CASE)
        )

        private val TRANSACTION_KEYWORDS = listOf(
            "credited", "debited", "received", "sent", "transferred",
            "deposited", "withdrawn", "payment", "balance", "ETB", "Birr"
        )
    }

    fun parse(smsBody: String, smsSender: String, customRules: List<BankRule> = emptyList()): ParsedTransaction? {
        for (rule in customRules.filter { it.isEnabled }) {
            val result = tryCustomRule(smsBody, smsSender, rule)
            if (result != null) return result
        }

        for (pattern in BANK_PATTERNS) {
            val result = tryBankPattern(smsBody, smsSender, pattern)
            if (result != null) return result
        }

        if (looksLikeTransaction(smsBody)) {
            return tryGenericParse(smsBody, smsSender)
        }

        return null
    }

    fun toTransaction(parsed: ParsedTransaction): Transaction {
        return Transaction(
            amount = parsed.amount,
            currency = parsed.currency,
            senderName = parsed.senderName,
            bankName = parsed.bankName,
            accountNumber = parsed.accountNumber,
            transactionType = parsed.transactionType,
            timestamp = parsed.timestamp,
            rawSms = parsed.rawSms,
            smsSender = parsed.smsSender,
            smsHash = parsed.smsHash,
            transactionRef = parsed.transactionRef,
            smsSenderNumber = parsed.smsSenderNumber,
            balance = parsed.balance
        )
    }

    private fun tryBankPattern(smsBody: String, smsSender: String, pattern: BankPattern): ParsedTransaction? {
        val combined = "$smsSender $smsBody"
        val matches = pattern.identifyKeywords.any { combined.contains(it, ignoreCase = true) }
        if (!matches) return null

        val amount = extractFirst(smsBody, pattern.amountPatterns) ?: return null
        val senderName = extractFirst(smsBody, pattern.senderNamePatterns)?.trim() ?: "Unknown"
        val account = extractFirst(smsBody, pattern.accountPatterns) ?: ""
        val type = detectType(smsBody, pattern.creditKeywords, pattern.debitKeywords)
        val ref = extractRef(smsBody, pattern.transactionRefPatterns)
        val balance = extractFirst(smsBody, pattern.balancePatterns.ifEmpty { GENERIC_BALANCE_PATTERNS }) ?: ""

        return ParsedTransaction(
            amount = parseAmount(amount),
            currency = "ETB",
            senderName = senderName,
            bankName = pattern.bankName,
            accountNumber = account,
            transactionType = type,
            timestamp = System.currentTimeMillis(),
            rawSms = smsBody,
            smsSender = smsSender,
            smsHash = generateHash(smsBody, smsSender),
            transactionRef = ref,
            smsSenderNumber = smsSender,
            balance = if (balance.isNotEmpty()) "ETB ${formatBalance(balance)}" else ""
        )
    }

    private fun tryCustomRule(smsBody: String, smsSender: String, rule: BankRule): ParsedTransaction? {
        val combined = "$smsSender $smsBody"
        val matches = rule.senderKeywords.split(",").any {
            combined.contains(it.trim(), ignoreCase = true)
        }
        if (!matches) return null

        val amountPatterns = if (rule.amountPattern.isNotEmpty()) {
            listOf(Regex(rule.amountPattern, RegexOption.IGNORE_CASE))
        } else {
            GENERIC_AMOUNT_PATTERNS
        }

        val amountStr = extractFirst(smsBody, amountPatterns) ?: return null
        val senderName = extractFirst(smsBody, GENERIC_NAME_PATTERNS)?.trim() ?: "Unknown"
        val account = extractFirst(smsBody, GENERIC_ACCOUNT_PATTERNS) ?: ""

        val creditKw = rule.creditKeywords.split(",").map { it.trim() }
        val debitKw = rule.debitKeywords.split(",").map { it.trim() }
        val type = detectType(smsBody, creditKw, debitKw)
        val ref = extractRef(smsBody, GENERIC_REF_PATTERNS)
        val balance = extractFirst(smsBody, GENERIC_BALANCE_PATTERNS) ?: ""

        return ParsedTransaction(
            amount = parseAmount(amountStr),
            currency = "ETB",
            senderName = senderName,
            bankName = rule.bankName,
            accountNumber = account,
            transactionType = type,
            timestamp = System.currentTimeMillis(),
            rawSms = smsBody,
            smsSender = smsSender,
            smsHash = generateHash(smsBody, smsSender),
            transactionRef = ref,
            smsSenderNumber = smsSender,
            balance = if (balance.isNotEmpty()) "ETB ${formatBalance(balance)}" else ""
        )
    }

    private fun tryGenericParse(smsBody: String, smsSender: String): ParsedTransaction? {
        val amountStr = extractFirst(smsBody, GENERIC_AMOUNT_PATTERNS) ?: return null
        val senderName = extractFirst(smsBody, GENERIC_NAME_PATTERNS)?.trim() ?: "Unknown"
        val account = extractFirst(smsBody, GENERIC_ACCOUNT_PATTERNS) ?: ""

        val type = detectType(
            smsBody,
            listOf("credited", "received", "deposited", "sent to you"),
            listOf("debited", "sent", "withdrawn", "paid")
        )

        val ref = extractRef(smsBody, GENERIC_REF_PATTERNS)
        val balance = extractFirst(smsBody, GENERIC_BALANCE_PATTERNS) ?: ""

        return ParsedTransaction(
            amount = parseAmount(amountStr),
            currency = "ETB",
            senderName = senderName,
            bankName = "Unknown Bank",
            accountNumber = account,
            transactionType = type,
            timestamp = System.currentTimeMillis(),
            rawSms = smsBody,
            smsSender = smsSender,
            smsHash = generateHash(smsBody, smsSender),
            transactionRef = ref,
            smsSenderNumber = smsSender,
            balance = if (balance.isNotEmpty()) "ETB ${formatBalance(balance)}" else ""
        )
    }

    private fun extractFirst(text: String, patterns: List<Regex>): String? {
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null && match.groupValues.size > 1) {
                val value = match.groupValues[1]
                if (value.isNotBlank()) return value
            }
        }
        return null
    }

    private fun extractRef(text: String, patterns: List<Regex>): String {
        for (pattern in patterns) {
            val match = pattern.find(text)
            if (match != null) {
                return if (match.groupValues.size > 1 && match.groupValues[1].isNotBlank()) {
                    match.groupValues[1]
                } else {
                    match.value
                }
            }
        }
        return ""
    }

    private fun parseAmount(amountStr: String): Double {
        return amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
    }

    private fun formatBalance(balanceStr: String): String {
        val amount = balanceStr.replace(",", "").toDoubleOrNull() ?: return balanceStr
        return String.format("%,.2f", amount)
    }

    private fun detectType(
        smsBody: String,
        creditKeywords: List<String>,
        debitKeywords: List<String>
    ): TransactionType {
        return when {
            creditKeywords.any { smsBody.contains(it, ignoreCase = true) } -> TransactionType.CREDIT
            debitKeywords.any { smsBody.contains(it, ignoreCase = true) } -> TransactionType.DEBIT
            else -> TransactionType.UNKNOWN
        }
    }

    private fun looksLikeTransaction(smsBody: String): Boolean {
        return TRANSACTION_KEYWORDS.any { smsBody.contains(it, ignoreCase = true) }
    }

    private fun generateHash(smsBody: String, smsSender: String): String {
        val input = "$smsSender|$smsBody"
        val digest = MessageDigest.getInstance("SHA-256")
        return digest.digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
