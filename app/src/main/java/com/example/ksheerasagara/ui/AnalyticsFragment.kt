package com.example.ksheerasagara.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ksheerasagara.R
import com.example.ksheerasagara.data.FinanceRepository
import com.example.ksheerasagara.databinding.FragmentAnalyticsBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
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

        val calendar = java.util.Calendar.getInstance()
        viewModel.loadMonthlyData(calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH))

        // Expense pie chart
        viewModel.expensePieData.observe(viewLifecycleOwner) { breakdown ->
            if (breakdown.isNotEmpty()) {
                val entries = breakdown.map { PieEntry(it.totalAmount.toFloat(), it.category) }
                val dataSet = PieDataSet(entries, "Expenses")
                val typedArray = resources.obtainTypedArray(R.array.pieColors)
                val colors = (0 until typedArray.length()).map { typedArray.getColor(it, 0) }
                typedArray.recycle()
                dataSet.colors = colors
                binding.pieChart.data = PieData(dataSet)
                binding.pieChart.invalidate()
                binding.pieChart.visibility = View.VISIBLE
            } else {
                binding.pieChart.clear()
                binding.pieChart.visibility = View.GONE
            }
        }

        // Cow-wise bar chart (shows sample data)
        viewModel.cowProfitList.observe(viewLifecycleOwner) { cowProfits ->
            if (cowProfits.isNotEmpty()) {
                val entries = cowProfits.mapIndexed { index, cp -> BarEntry(index.toFloat(), cp.profit.toFloat()) }
                val dataSet = BarDataSet(entries, "Profit/Loss per Cow")
                dataSet.color = android.graphics.Color.rgb(76, 175, 80)
                binding.barChart.data = BarData(dataSet)
                binding.barChart.invalidate()
                binding.barChart.visibility = View.VISIBLE
            } else {
                binding.barChart.clear()
                binding.barChart.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}