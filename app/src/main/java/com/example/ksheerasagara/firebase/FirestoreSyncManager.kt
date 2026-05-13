package com.example.ksheerasagara.firebase

import android.content.Context
import android.util.Log
import com.example.ksheerasagara.data.ExpenseEntry
import com.example.ksheerasagara.data.MilkEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirestoreSyncManager(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Get current logged-in user ID
    private fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // ==================== MILK ENTRIES (User-specific) ====================

    suspend fun syncMilkEntry(entry: MilkEntry) {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e("FirestoreSync", "Cannot sync: No user logged in")
            return
        }
        try {
            val entryMap = mapOf(
                "entryId" to entry.entryId,
                "date" to entry.date.time,
                "cowId" to entry.cowId,
                "cowName" to entry.cowName,
                "quantityLiters" to entry.quantityLiters,
                "fatPercent" to entry.fatPercent,
                "baseRate" to entry.baseRate,
                "paymentAmount" to entry.paymentAmount,
                "userId" to userId
            )
            // Store in user-specific sub-collection
            db.collection("users")
                .document(userId)
                .collection("milk_entries")
                .document(entry.entryId.toString())
                .set(entryMap, SetOptions.merge())
                .await()
            Log.d("FirestoreSync", "Milk entry synced for user $userId")
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error syncing milk entry: ${e.message}", e)
        }
    }

    // Load milk entries for CURRENT USER ONLY
    suspend fun loadMilkEntries(): List<MilkEntry> {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.w("FirestoreSync", "No user logged in, returning empty list")
            return emptyList()
        }
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("milk_entries")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                MilkEntry(
                    entryId = (data["entryId"] as? Long)?.toInt() ?: 0,
                    date = Date(data["date"] as? Long ?: 0),
                    cowId = (data["cowId"] as? Long)?.toInt() ?: 0,
                    cowName = data["cowName"] as? String ?: "",
                    quantityLiters = data["quantityLiters"] as? Double ?: 0.0,
                    fatPercent = data["fatPercent"] as? Double ?: 0.0,
                    baseRate = data["baseRate"] as? Double ?: 0.0,
                    paymentAmount = data["paymentAmount"] as? Double ?: 0.0,
                    userId = data["userId"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error loading milk entries: ${e.message}", e)
            emptyList()
        }
    }

    // Get total income for current user in date range
    suspend fun getTotalIncomeBetween(start: Date, end: Date): Double {
        val entries = loadMilkEntries()
        return entries.filter { it.date in start..end }.sumOf { it.paymentAmount }
    }

    // ==================== EXPENSE ENTRIES (User-specific) ====================

    suspend fun syncExpenseEntry(entry: ExpenseEntry) {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.e("FirestoreSync", "Cannot sync: No user logged in")
            return
        }
        try {
            val entryMap = mapOf(
                "expenseId" to entry.expenseId,
                "date" to entry.date.time,
                "category" to entry.category,
                "amount" to entry.amount,
                "note" to entry.note,
                "cowId" to entry.cowId,
                "cowName" to entry.cowName,
                "userId" to userId
            )
            // Store in user-specific sub-collection
            db.collection("users")
                .document(userId)
                .collection("expense_entries")
                .document(entry.expenseId.toString())
                .set(entryMap, SetOptions.merge())
                .await()
            Log.d("FirestoreSync", "Expense entry synced for user $userId")
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error syncing expense entry: ${e.message}", e)
        }
    }

    // Load expense entries for CURRENT USER ONLY
    suspend fun loadExpenseEntries(): List<ExpenseEntry> {
        val userId = getCurrentUserId()
        if (userId == null) {
            Log.w("FirestoreSync", "No user logged in, returning empty list")
            return emptyList()
        }
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("expense_entries")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                ExpenseEntry(
                    expenseId = (data["expenseId"] as? Long)?.toInt() ?: 0,
                    date = Date(data["date"] as? Long ?: 0),
                    category = data["category"] as? String ?: "",
                    amount = data["amount"] as? Double ?: 0.0,
                    note = data["note"] as? String ?: "",
                    cowId = (data["cowId"] as? Long)?.toInt(),
                    cowName = data["cowName"] as? String ?: "",
                    userId = data["userId"] as? String ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("FirestoreSync", "Error loading expense entries: ${e.message}", e)
            emptyList()
        }
    }

    // Get total expense for current user in date range
    suspend fun getTotalExpenseBetween(start: Date, end: Date): Double {
        val entries = loadExpenseEntries()
        return entries.filter { it.date in start..end }.sumOf { it.amount }
    }

    fun signOut() {
        auth.signOut()
    }
}