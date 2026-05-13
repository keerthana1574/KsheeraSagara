package com.example.ksheerasagara.data

import android.content.Context
import com.example.ksheerasagara.firebase.FirestoreSyncManager
import java.util.Date

// CategoryAmount is defined here only (not in any other file)
data class CategoryAmount(val category: String, val totalAmount: Double)

class FinanceRepository(private val context: Context) {

    private val firestoreSync = FirestoreSyncManager(context)

    // ==================== MILK ENTRIES ====================

    suspend fun addMilkEntry(entry: MilkEntry) {
        firestoreSync.syncMilkEntry(entry)
    }

    suspend fun getAllMilkEntries(): List<MilkEntry> {
        return firestoreSync.loadMilkEntries()
    }

    suspend fun getTotalIncome(start: Date, end: Date): Double {
        return firestoreSync.getTotalIncomeBetween(start, end)
    }

    suspend fun getTotalIncomeForCow(cowId: Int, start: Date, end: Date): Double {
        val allEntries = firestoreSync.loadMilkEntries()
        return allEntries.filter { it.cowId == cowId && it.date in start..end }.sumOf { it.paymentAmount }
    }

    // ==================== EXPENSE ENTRIES ====================

    suspend fun addExpenseEntry(entry: ExpenseEntry) {
        firestoreSync.syncExpenseEntry(entry)
    }

    suspend fun getAllExpenseEntries(): List<ExpenseEntry> {
        return firestoreSync.loadExpenseEntries()
    }

    suspend fun getTotalExpense(start: Date, end: Date): Double {
        return firestoreSync.getTotalExpenseBetween(start, end)
    }

    suspend fun getTotalExpenseForCow(cowId: Int, start: Date, end: Date): Double {
        val allEntries = firestoreSync.loadExpenseEntries()
        return allEntries.filter { it.cowId == cowId && it.date in start..end }.sumOf { it.amount }
    }

    suspend fun getExpenseBreakdown(start: Date, end: Date): List<CategoryAmount> {
        val allEntries = firestoreSync.loadExpenseEntries()
        val filtered = allEntries.filter { it.date in start..end }
        return filtered.groupBy { it.category }
            .map { CategoryAmount(it.key, it.value.sumOf { expense -> expense.amount }) }
    }

    // ==================== COWS ====================

    suspend fun getAllCows(): List<Cow> {
        // For now, return empty list or you can implement cow management in Firestore
        return emptyList()
    }
}