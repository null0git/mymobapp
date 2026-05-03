package com.paysms.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.paysms.data.model.BankRule
import com.paysms.data.model.PendingRequest
import com.paysms.data.model.Transaction

@Database(
    entities = [Transaction::class, PendingRequest::class, BankRule::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PaySmsDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun pendingRequestDao(): PendingRequestDao
    abstract fun bankRuleDao(): BankRuleDao

    companion object {
        @Volatile
        private var INSTANCE: PaySmsDatabase? = null

        fun getDatabase(context: Context): PaySmsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaySmsDatabase::class.java,
                    "paysms_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
