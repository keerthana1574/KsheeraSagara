package com.example.ksheerasagara.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ksheerasagara.R
import com.example.ksheerasagara.data.FinanceRepository
import com.example.ksheerasagara.databinding.FragmentDashboardBinding
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private lateinit var recentAdapter: RecentTransactionsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
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

        val calendar = Calendar.getInstance()
        viewModel.loadMonthlyData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))

        viewModel.profitLoss.observe(viewLifecycleOwner) { profit ->
            binding.tvProfitLoss.text = String.format(Locale.US, "₹ %.2f", profit)
            val colorRes = if (profit >= 0) android.R.color.holo_green_light else android.R.color.holo_red_light
            binding.profitLossCard.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), colorRes))
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvTotalIncome.text = String.format(Locale.US, "₹ %.2f", income)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvTotalExpense.text = String.format(Locale.US, "₹ %.2f", expense)
        }

        recentAdapter = RecentTransactionsAdapter()
        binding.recyclerRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerRecentTransactions.adapter = recentAdapter

        viewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            recentAdapter.submitList(transactions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class RecentTransactionsAdapter : RecyclerView.Adapter<RecentTransactionsAdapter.ViewHolder>() {
        private var items = listOf<Transaction>()
        fun submitList(list: List<Transaction>) { items = list; notifyDataSetChanged() }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.desc.text = "${item.description}  ${android.text.format.DateFormat.format("dd MMM yyyy", item.date)}"
            holder.amount.text = String.format(Locale.US, "₹%.2f", item.amount)
            holder.amount.setTextColor(if (item.type == "Income") android.graphics.Color.parseColor("#4CAF50") else android.graphics.Color.RED)
        }
        override fun getItemCount() = items.size
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val desc: TextView = itemView.findViewById(R.id.tvDesc)
            val amount: TextView = itemView.findViewById(R.id.tvAmount)
        }
    }
}