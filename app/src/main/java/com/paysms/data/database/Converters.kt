package com.paysms.data.database

import androidx.room.TypeConverter
import com.paysms.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType =
        try {
            TransactionType.valueOf(value)
        } catch (_: IllegalArgumentException) {
            TransactionType.UNKNOWN
        }
}
