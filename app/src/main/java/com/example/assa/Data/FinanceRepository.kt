package com.example.assa.Data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

// Class implementing this uses Dao to access database
interface FinanceRepository {

    fun getAllFinanceDataStream(): Flow<List<FinanceDataItem>>

    fun getFinanceDataStream(id: Int): Flow<FinanceDataItem?>

    fun getMonthsFinancialData(): Flow<List<FinanceDataItem?>>

    fun retreiveTotalAmountForMonth(): Flow<Double>

    fun retreiveFinancesBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<FinanceDataItem>>

    fun retreiveFinanceSumFromFilteredDate(startDate: LocalDateTime, endDate: LocalDateTime): Flow<Double>

    fun retreiveSumForAllFinances(): Flow<Double>

    suspend fun insertFinanceData(financeDataItem: FinanceDataItem)

    suspend fun deleteFinanceData(financeDataItem: FinanceDataItem)

    suspend fun updateFinanceData(financeDataItem: FinanceDataItem)

    suspend fun deleteAllFinancialData()
}