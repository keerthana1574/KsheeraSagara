package com.example.ksheerasagara.data

import java.util.Date

data class ExpenseEntry(
    val expenseId: Int = 0,
    val date: Date = Date(),
    val category: String = "",
    val amount: Double = 0.0,
    val note: String = "",
    val cowId: Int? = null,
    val cowName: String = "",
    val userId: String = ""
)