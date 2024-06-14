package com.example.assa.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.assa.Data.FinanceDataItem
import com.example.assa.Data.OfflineFinanceDataRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FilterFormViewModel(private val repository: OfflineFinanceDataRepository): ViewModel() {
    var firstDate by mutableStateOf("")
    var secondDate by mutableStateOf("")
    var hasFilterDates by mutableStateOf(false)

    var formattedDateOne: LocalDateTime by mutableStateOf(LocalDateTime.MIN)
    var formattedDateTwo: LocalDateTime by mutableStateOf(LocalDateTime.MIN)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun filterDates(): Flow<List<FinanceDataItem>> {
        formattedDateOne = LocalDateTime.parse("${firstDate} 00:00", formatter)
        formattedDateTwo = LocalDateTime.parse("${secondDate} 00:00", formatter)
        return repository.retreiveFinancesBetweenDates(formattedDateOne,formattedDateTwo)
    }
}