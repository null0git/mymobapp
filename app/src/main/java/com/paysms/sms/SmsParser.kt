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
    val smsHash: String
)

/**
 * Bank-specific parser definition.
 *
 * To add a new bank, simply create a new BankPattern and add it to [SmsParser.BANK_PATTERNS].
 * Each BankPattern defines:
 *   - How to identify the bank (keywords in SMS sender or body)
 *   - How to extract the amount
 *   - How to extract the sender/payer name
 *   - How to extract the account number
 *   - How to detect credit vs debit
 *
 * The parser tries each bank pattern in order and uses the first one that matches.
 */
data class BankPattern(
    val bankName: String,
    /** Keywords to match against the SMS sender address or SMS body to identify this bank */
    val identifyKeywords: List<String>,
    /** Regex patterns to extract the amount (first capturing group = amount string) */
    val amountPatterns: List<Regex>,
    /** Regex patterns to extract the sender/payer name (first capturing group = name) */
    val senderNamePatterns: List<Regex>,
    /** Regex patterns to extract the account number (first capturing group = account) */
    val accountPatterns: List<Regex>,
    /** Keywords that indicate a CREDIT transaction */
    val creditKeywords: List<String>,
    /** Keywords that indicate a DEBIT transaction */
    val debitKeywords: List<String>
)

class SmsParser {

    companion object {

        // =====================================================================
        //  BANK PATTERNS — Add new banks here
        //  Each entry defines how to identify and parse SMS from that bank.
        //  The parser tries each pattern in order; first match wins.
        // =====================================================================

        val BANK_PATTERNS: List<BankPattern> = listOf(

            // ── Abay Bank ────────────────────────────────────────────────
            // Example: "Dear Customer, Account to account transfer was made
            //   to account ****1016 from AMINET SEID by ETB 2,000.00 on
            //   2026-04-25. Your current balance is 2,605.35."
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
                debitKeywords = listOf("transfer was made from account", "debited", "sent", "withdrawn")
            ),

            // ── Bank of Abyssinia (BOA) ──────────────────────────────────
            // Example: "Dear Amir, your account 2*65 was credited with
            //   ETB 1,000.00 by Reshid Endris Jibril. Available Balance:
            //   ETB 1,351.11."
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
                debitKeywords = listOf("debited")
            ),

            // ── Dashen Bank ──────────────────────────────────────────────
            // Example: "Dear Customer, your account 2905****811 has been
            //   credited with ETB 40,000.00 from NYAKER JOCK  YUAL on
            //   2026-02-23 at 03:29:35. Your current balance is ETB
            //   742,029.27."
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
                debitKeywords = listOf("debited")
            ),

            // ── Commercial Bank of Ethiopia (CBE) ────────────────────────
            // Example: "Dear Furniture your Account 1*****6499 has been
            //   Credited with ETB 150,000.00 from Firebizu Ayele, on
            //   25/10/2025 at 18:46:27 with Ref No FT252981N5ZG"
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
                debitKeywords = listOf("Debited")
            ),

            // ── Telebirr ────────────────────────────────────────────────
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
                debitKeywords = listOf("sent", "paid", "transferred", "debited")
            ),

            // ── Awash Bank ──────────────────────────────────────────────
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
                debitKeywords = listOf("debited", "sent", "withdrawn")
            ),

            // ── Wegagen Bank ────────────────────────────────────────────
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
                debitKeywords = listOf("debited", "sent", "withdrawn")
            ),

            // ── Cooperative Bank of Oromia ───────────────────────────────
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
                debitKeywords = listOf("debited", "sent", "withdrawn")
            )
        )

        // Generic fallback patterns used when no bank-specific pattern matches
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

        private val TRANSACTION_KEYWORDS = listOf(
            "credited", "debited", "received", "sent", "transferred",
            "deposited", "withdrawn", "payment", "balance", "ETB", "Birr"
        )
    }

    /**
     * Parse an SMS message and extract transaction details.
     *
     * @param smsBody The full text of the SMS message
     * @param smsSender The sender address/number of the SMS
     * @param customRules Optional user-defined bank rules from the database
     * @return ParsedTransaction if the SMS is a valid transaction, null otherwise
     */
    fun parse(smsBody: String, smsSender: String, customRules: List<BankRule> = emptyList()): ParsedTransaction? {
        // 1) Try user-defined custom rules first
        for (rule in customRules.filter { it.isEnabled }) {
            val result = tryCustomRule(smsBody, smsSender, rule)
            if (result != null) return result
        }

        // 2) Try built-in bank patterns
        for (pattern in BANK_PATTERNS) {
            val result = tryBankPattern(smsBody, smsSender, pattern)
            if (result != null) return result
        }

        // 3) Fallback: generic parsing for unknown banks
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
            smsHash = parsed.smsHash
        )
    }

    // ─── Bank pattern matching ───────────────────────────────────────────

    private fun tryBankPattern(smsBody: String, smsSender: String, pattern: BankPattern): ParsedTransaction? {
        val combined = "$smsSender $smsBody"
        val matches = pattern.identifyKeywords.any { combined.contains(it, ignoreCase = true) }
        if (!matches) return null

        val amount = extractFirst(smsBody, pattern.amountPatterns) ?: return null
        val senderName = extractFirst(smsBody, pattern.senderNamePatterns)?.trim() ?: "Unknown"
        val account = extractFirst(smsBody, pattern.accountPatterns) ?: ""
        val type = detectType(smsBody, pattern.creditKeywords, pattern.debitKeywords)

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
            smsHash = generateHash(smsBody, smsSender)
        )
    }

    // ─── Custom rule matching (user-defined via Settings) ────────────────

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
            smsHash = generateHash(smsBody, smsSender)
        )
    }

    // ─── Generic fallback parser ─────────────────────────────────────────

    private fun tryGenericParse(smsBody: String, smsSender: String): ParsedTransaction? {
        val amountStr = extractFirst(smsBody, GENERIC_AMOUNT_PATTERNS) ?: return null
        val senderName = extractFirst(smsBody, GENERIC_NAME_PATTERNS)?.trim() ?: "Unknown"
        val account = extractFirst(smsBody, GENERIC_ACCOUNT_PATTERNS) ?: ""

        val type = detectType(
            smsBody,
            listOf("credited", "received", "deposited", "sent to you"),
            listOf("debited", "sent", "withdrawn", "paid")
        )

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
            smsHash = generateHash(smsBody, smsSender)
        )
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

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

    private fun parseAmount(amountStr: String): Double {
        return amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
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
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
