package com.example.ksheerasagara.data

import java.util.Date

data class MilkEntry(
    val entryId: Int = 0,
    val date: Date = Date(),
    val cowId: Int = 0,
    val cowName: String = "",
    val quantityLiters: Double = 0.0,
    val fatPercent: Double = 0.0,
    val baseRate: Double = 0.0,
    val paymentAmount: Double = 0.0,
    val userId: String = ""
)