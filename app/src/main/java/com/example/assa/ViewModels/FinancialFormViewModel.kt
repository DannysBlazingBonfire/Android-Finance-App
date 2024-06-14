package com.example.assa.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.assa.Data.FinanceCategory
import com.example.assa.Data.FinanceDataItem
import com.example.assa.Data.OfflineFinanceDataRepository
import java.time.LocalDateTime

class FinancialFormViewModel(private val repository: OfflineFinanceDataRepository) : ViewModel() {

    var expense by mutableStateOf(false)
    var title by mutableStateOf("")
    var amount by mutableStateOf("")
    var financeCategory by mutableStateOf(FinanceCategory.OTHER)

    var date: LocalDateTime = LocalDateTime.now()

    suspend fun submitForm() {
        var value = amount.toDouble()
        date = LocalDateTime.now()
        if (!expense) {
            financeCategory = FinanceCategory.OTHER
            value = Math.abs(value) //if not an expense number is positive
        }
        else {
            if(value >= 0) {
                value *= -1 //make number negative
            }
        }
        repository.insertFinanceData(FinanceDataItem(expense,date,title,financeCategory,value))
        resetVm()
    }

    fun resetVm() {
        expense = false
        title = ""
        amount = ""
        financeCategory = FinanceCategory.OTHER
    }
}