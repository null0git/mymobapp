package com.paysms.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.paysms.data.model.BankRule
import kotlinx.coroutines.flow.Flow

@Dao
interface BankRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: BankRule): Long

    @Update
    suspend fun update(rule: BankRule)

    @Delete
    suspend fun delete(rule: BankRule)

    @Query("SELECT * FROM bank_rules ORDER BY bankName ASC")
    fun getAllRules(): Flow<List<BankRule>>

    @Query("SELECT * FROM bank_rules WHERE isEnabled = 1")
    suspend fun getEnabledRules(): List<BankRule>

    @Query("SELECT * FROM bank_rules WHERE id = :id")
    suspend fun getRuleById(id: Long): BankRule?
}
