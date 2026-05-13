package com.example.ksheerasagara.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ksheerasagara.data.FinanceRepository
import com.example.ksheerasagara.databinding.FragmentReportsBinding
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
    private var selectedMonth = Calendar.getInstance().get(Calendar.MONTH) // 0-based

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
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

        updateMonthYearText()

        binding.btnSelectMonth.setOnClickListener {
            showMonthYearPicker()
        }

        binding.btnDownloadReport.setOnClickListener {
            generateMonthlyReport()
        }
    }

    private fun updateMonthYearText() {
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth, 1)
        val monthName = android.text.format.DateFormat.format("MMMM", calendar).toString()
        binding.tvSelectedMonth.text = "$monthName $selectedYear"
    }

    private fun showMonthYearPicker() {
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, _ ->
                selectedYear = year
                selectedMonth = month
                updateMonthYearText()
            },
            selectedYear,
            selectedMonth,
            1 // day of month (ignored)
        )
        // Try to hide the day field to make it a month/year picker
        try {
            val dayFieldId = requireContext().resources.getIdentifier("day", "id", "android")
            if (dayFieldId != 0) {
                dialog.datePicker.findViewById<View>(dayFieldId)?.visibility = View.GONE
            }
        } catch (e: Exception) {
            // Ignore – the picker still works
        }
        dialog.show()
    }

    private fun generateMonthlyReport() {
        viewModel.loadMonthlyData(selectedYear, selectedMonth)

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
                val profit = income - expense
                val fileName = "Ksheera_Sagara_${selectedYear}_${selectedMonth + 1}.pdf"
                val file = java.io.File(requireContext().getExternalFilesDir(null), fileName)
                try {
                    val writer = PdfWriter(FileOutputStream(file))
                    val pdfDoc = PdfDocument(writer)
                    val document = Document(pdfDoc)
                    document.add(Paragraph("Ksheera Sagara Monthly Report"))
                    document.add(Paragraph("Month: ${selectedYear}-${selectedMonth + 1}"))
                    document.add(Paragraph(String.format(Locale.US, "Total Income: ₹%.2f", income)))
                    document.add(Paragraph(String.format(Locale.US, "Total Expenses: ₹%.2f", expense)))
                    document.add(Paragraph(String.format(Locale.US, "Net Profit/Loss: ₹%.2f", profit)))
                    document.close()
                    Toast.makeText(requireContext(), "PDF saved at ${file.absolutePath}", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
                // Remove observers to avoid repeated PDF generation
                viewModel.totalIncome.removeObservers(viewLifecycleOwner)
                viewModel.totalExpense.removeObservers(viewLifecycleOwner)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}