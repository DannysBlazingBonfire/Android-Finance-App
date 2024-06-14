package com.example.assa.Data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface FinanceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(financeDataItem: FinanceDataItem)

    @Update
    suspend fun update(financeDataItem: FinanceDataItem)

    @Delete
    suspend fun delete(financeDataItem: FinanceDataItem)

    @Query("SELECT * FROM financial_data WHERE id = :id")
    fun getFinanceData(id: Int): Flow<FinanceDataItem>

    @Query("SELECT * FROM financial_data ORDER BY date DESC")
    fun getAllFinancialData(): Flow<List<FinanceDataItem>>

    @Query("SELECT * FROM financial_data WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now')")
    fun getMonthsFinancialData(): Flow<List<FinanceDataItem>>

    @Query("SELECT SUM(amount) FROM financial_data WHERE strftime('%Y-%m', date) = strftime('%Y-%m', 'now')")
    fun retreiveTotalAmountForMonth(): Flow<Double>

    @Query("SELECT SUM(amount) FROM financial_data")
    fun retreiveSumForAllFinances(): Flow<Double>

    @Query("SELECT SUM(amount) FROM financial_data WHERE date BETWEEN :startDate AND :endDate")
    fun retreiveFinanceSumFromFilteredDate(startDate: LocalDateTime, endDate: LocalDateTime): Flow<Double>

    @Query("SELECT * FROM financial_data WHERE date BETWEEN :startDate AND :endDate")
    fun retreiveFinancesBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<FinanceDataItem>>

    @Query("DELETE FROM financial_data")
    suspend fun deletaAllFinancialData()
}