package com.example.assa.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "financial_data")
data class FinanceDataItem(
    val expense: Boolean,
    val date: LocalDateTime,
    val title: String,
    val category: FinanceCategory,
    val amount: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
