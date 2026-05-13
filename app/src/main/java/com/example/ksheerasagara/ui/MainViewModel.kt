package com.example.ksheerasagara.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ksheerasagara.data.*
import kotlinx.coroutines.launch
import java.util.*

data class Transaction(
    val description: String,
    val amount: Double,
    val date: Date,
    val type: String // "Income" or "Expense"
)

data class CowProfit(val cowName: String, val profit: Double)

class MainViewModel(private val repo: FinanceRepository) : ViewModel() {

    private val _profitLoss = MutableLiveData<Double>()
    val profitLoss: LiveData<Double> = _profitLoss

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpense = MutableLiveData<Double>()
    val totalExpense: LiveData<Double> = _totalExpense

    private val _expensePieData = MutableLiveData<List<CategoryAmount>>()
    val expensePieData: LiveData<List<CategoryAmount>> = _expensePieData

    private val _recentTransactions = MutableLiveData<List<Transaction>>()
    val recentTransactions: LiveData<List<Transaction>> = _recentTransactions

    private val _cowProfitList = MutableLiveData<List<CowProfit>>()
    val cowProfitList: LiveData<List<CowProfit>> = _cowProfitList

    fun loadMonthlyData(year: Int, month: Int) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.set(year, month, 1, 0, 0, 0)
            val start = calendar.time
            calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            val end = calendar.time

            val income = repo.getTotalIncome(start, end)
            val expense = repo.getTotalExpense(start, end)
            _totalIncome.postValue(income)
            _totalExpense.postValue(expense)
            _profitLoss.postValue(income - expense)

            val breakdown = repo.getExpenseBreakdown(start, end)
            _expensePieData.postValue(breakdown)

            loadRecentTransactions()
            loadCowProfit(start, end)
        }
    }

    private suspend fun loadRecentTransactions() {
        val milkEntries = repo.getAllMilkEntries()
        val expenseEntries = repo.getAllExpenseEntries()
        val all = mutableListOf<Transaction>()
        milkEntries.forEach {
            all.add(Transaction("Milk Sale (${it.quantityLiters}L - ${it.cowName})", it.paymentAmount, it.date, "Income"))
        }
        expenseEntries.forEach {
            all.add(Transaction("${it.category}${if (it.cowName.isNotEmpty()) " - ${it.cowName}" else ""}", it.amount, it.date, "Expense"))
        }
        all.sortByDescending { it.date }
        _recentTransactions.postValue(all.take(5))
    }

    private suspend fun loadCowProfit(start: Date, end: Date) {
        val cows = repo.getAllCows()
        val profits = mutableListOf<CowProfit>()
        for (cow in cows) {
            val income = repo.getTotalIncomeForCow(cow.cowId, start, end)
            val expense = repo.getTotalExpenseForCow(cow.cowId, start, end)
            profits.add(CowProfit(cow.name, income - expense))
        }
        _cowProfitList.postValue(profits.sortedByDescending { it.profit })
    }

    fun addIncome(cowId: Int, cowName: String, liters: Double, fat: Double, baseRate: Double) {
        viewModelScope.launch {
            val payment = liters * fat * baseRate
            val entry = MilkEntry(
                date = Date(),
                cowId = cowId,
                cowName = cowName,
                quantityLiters = liters,
                fatPercent = fat,
                baseRate = baseRate,
                paymentAmount = payment
            )
            repo.addMilkEntry(entry)
            refreshAfterChange()
        }
    }

    fun addExpense(category: String, amount: Double, note: String, cowId: Int?, cowName: String?, date: Date) {
        viewModelScope.launch {
            val expense = ExpenseEntry(
                date = date,
                category = category,
                amount = amount,
                note = note,
                cowId = cowId,
                cowName = cowName ?: ""
            )
            repo.addExpenseEntry(expense)
            refreshAfterChange()
        }
    }

    private fun refreshAfterChange() {
        val calendar = Calendar.getInstance()
        loadMonthlyData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))
    }
}