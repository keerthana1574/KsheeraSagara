package com.example.ksheerasagara.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksheerasagara.R
import com.example.ksheerasagara.data.FinanceRepository
import com.example.ksheerasagara.databinding.FragmentExpensesBinding
import java.text.SimpleDateFormat
import java.util.*

class ExpensesFragment : Fragment() {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var recentExpenseAdapter: RecentExpenseAdapter
    private var selectedDate = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = FinanceRepository(requireContext())
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(repository) as T
            }
        })[MainViewModel::class.java]

        val categories = resources.getStringArray(R.array.expense_categories)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding.tvSelectedDate.text = sdf.format(selectedDate.time)

        binding.btnDatePicker.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                selectedDate.set(year, month, day)
                binding.tvSelectedDate.text = sdf.format(selectedDate.time)
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnSaveExpense.setOnClickListener {
            val category = binding.spinnerCategory.selectedItem.toString()
            val amount = binding.etAmount.text?.toString()?.toDoubleOrNull() ?: 0.0
            val cowIdStr = binding.etCowId.text?.toString()
            val cowId = if (cowIdStr.isNullOrEmpty()) null else cowIdStr.toIntOrNull()
            val cowName = binding.etCowName.text?.toString()
            val description = binding.etDescription.text?.toString() ?: ""

            if (amount <= 0 || category.isEmpty()) {
                Toast.makeText(requireContext(), "Enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.addExpense(category, amount, description, cowId, cowName, selectedDate.time)
            Toast.makeText(requireContext(), "Expense saved", Toast.LENGTH_SHORT).show()
            binding.etAmount.text?.clear()
            binding.etCowId.text?.clear()
            binding.etCowName.text?.clear()
            binding.etDescription.text?.clear()
        }

        recentExpenseAdapter = RecentExpenseAdapter()
        binding.recyclerRecentExpenses.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecentExpenses.adapter = recentExpenseAdapter

        viewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            val expenseList = transactions.filter { it.type == "Expense" }
            recentExpenseAdapter.submitList(expenseList)
        }

        viewModel.loadMonthlyData(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class RecentExpenseAdapter : RecyclerView.Adapter<RecentExpenseAdapter.ViewHolder>() {
        private var items = listOf<Transaction>()

        fun submitList(list: List<Transaction>) {
            items = list
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.textView.text = "${item.description} - ${String.format(Locale.US, "₹%.2f", item.amount)}"
        }

        override fun getItemCount() = items.size

        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }
}