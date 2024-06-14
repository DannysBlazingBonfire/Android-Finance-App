package com.example.assa.ViewModels

import androidx.lifecycle.ViewModel
import com.example.assa.Data.OfflineFinanceDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class TotalFinanceViewModel(private val repository: OfflineFinanceDataRepository): ViewModel() {

    var total: Flow<Double> = repository.retreiveSumForAllFinances().map { it ?: 0.0 }
    lateinit var totalFiltered: Flow<Double>

    fun getFilteredSum(dateOne: LocalDateTime, dateTwo: LocalDateTime): Flow<Double> {
        totalFiltered = repository.retreiveFinanceSumFromFilteredDate(dateOne, dateTwo).map { it ?: 0.0 }
        return totalFiltered
    }
}