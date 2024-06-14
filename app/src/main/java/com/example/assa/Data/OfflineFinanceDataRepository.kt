package com.example.assa.Data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class OfflineFinanceDataRepository(private val financeDao: FinanceDao): FinanceRepository {
    override fun getAllFinanceDataStream(): Flow<List<FinanceDataItem>> = financeDao.getAllFinancialData()

    override fun getFinanceDataStream(id: Int): Flow<FinanceDataItem?> = financeDao.getFinanceData(id)

    override fun getMonthsFinancialData(): Flow<List<FinanceDataItem>> = financeDao.getMonthsFinancialData()

    override fun retreiveTotalAmountForMonth(): Flow<Double> = financeDao.retreiveTotalAmountForMonth()

    override fun retreiveFinancesBetweenDates(startDate: LocalDateTime, endDate: LocalDateTime) : Flow<List<FinanceDataItem>> = financeDao.retreiveFinancesBetweenDates(startDate, endDate)

    override fun retreiveFinanceSumFromFilteredDate(startDate: LocalDateTime, endDate: LocalDateTime): Flow<Double> = financeDao.retreiveFinanceSumFromFilteredDate(startDate, endDate)

    override fun retreiveSumForAllFinances(): Flow<Double> = financeDao.retreiveSumForAllFinances()

    override suspend fun insertFinanceData(financeDataItem: FinanceDataItem) = financeDao.insert(financeDataItem)

    override suspend fun deleteFinanceData(financeDataItem: FinanceDataItem) = financeDao.delete(financeDataItem)

    override suspend fun updateFinanceData(financeDataItem: FinanceDataItem) = financeDao.update(financeDataItem)

    override suspend fun deleteAllFinancialData() = financeDao.deletaAllFinancialData()
}