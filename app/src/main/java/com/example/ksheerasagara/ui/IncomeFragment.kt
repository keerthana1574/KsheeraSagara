package com.example.ksheerasagara.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksheerasagara.data.FinanceRepository
import com.example.ksheerasagara.databinding.FragmentIncomeBinding
import java.text.SimpleDateFormat
import java.util.*

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var recentIncomeAdapter: RecentIncomeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
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

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        binding.tvDate.text = sdf.format(Date())

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculatePayment()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etLiters.addTextChangedListener(textWatcher)
        binding.etFat.addTextChangedListener(textWatcher)
        binding.etBaseRate.addTextChangedListener(textWatcher)

        binding.btnSaveIncome.setOnClickListener {
            val cowName = binding.etCowName.text?.toString() ?: ""
            val liters = binding.etLiters.text?.toString()?.toDoubleOrNull() ?: 0.0
            val fat = binding.etFat.text?.toString()?.toDoubleOrNull() ?: 0.0
            val baseRate = binding.etBaseRate.text?.toString()?.toDoubleOrNull() ?: 0.0
            if (liters <= 0 || fat <= 0 || baseRate <= 0) {
                Toast.makeText(requireContext(), "Enter valid values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.addIncome(0, cowName, liters, fat, baseRate)
            Toast.makeText(requireContext(), "Income saved", Toast.LENGTH_SHORT).show()
            clearFields()
        }

        recentIncomeAdapter = RecentIncomeAdapter()
        binding.recyclerRecentIncome.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecentIncome.adapter = recentIncomeAdapter

        viewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            val incomeList = transactions.filter { it.type == "Income" }
            recentIncomeAdapter.submitList(incomeList)
        }

        viewModel.loadMonthlyData(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH))
    }

    private fun calculatePayment() {
        val liters = binding.etLiters.text?.toString()?.toDoubleOrNull() ?: 0.0
        val fat = binding.etFat.text?.toString()?.toDoubleOrNull() ?: 0.0
        val baseRate = binding.etBaseRate.text?.toString()?.toDoubleOrNull() ?: 0.0
        val payment = liters * fat * baseRate
        binding.tvEstimatedPayment.text = String.format(Locale.US, "₹%.2f", payment)
    }

    private fun clearFields() {
        binding.etCowName.text?.clear()
        binding.etLiters.text?.clear()
        binding.etFat.text?.clear()
        binding.etBaseRate.text?.clear()
        binding.tvEstimatedPayment.text = "₹0.00"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class RecentIncomeAdapter : RecyclerView.Adapter<RecentIncomeAdapter.ViewHolder>() {
        private var items = listOf<Transaction>()
        fun submitList(list: List<Transaction>) { items = list; notifyDataSetChanged() }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.textView.text = "${item.description}   ${String.format(Locale.US, "₹%.2f", item.amount)}"
        }
        override fun getItemCount() = items.size
        class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)
    }
}