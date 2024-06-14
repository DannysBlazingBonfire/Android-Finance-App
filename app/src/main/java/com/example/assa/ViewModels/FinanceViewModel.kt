package com.example.assa.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.assa.Data.FinanceDataItem
import com.example.assa.Data.OfflineFinanceDataRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class FinanceViewModel(private val repository: OfflineFinanceDataRepository): ViewModel() {
    var allItems: Flow<List<FinanceDataItem>> = repository.getAllFinanceDataStream()
    lateinit var filteredList: Flow<List<FinanceDataItem>>
    lateinit var incomeList: Flow<List<FinanceDataItem>>
    lateinit var expenseList: Flow<List<FinanceDataItem>>
    var filtered by mutableStateOf(false)
    var incomeOnly by mutableStateOf(false)
    var expenseOnly by mutableStateOf(false)

    suspend fun saveItem(item: FinanceDataItem) {
        repository.insertFinanceData(item)
    }

    suspend fun deleteAllItems() {
        repository.deleteAllFinancialData()
    }
}