package com.example.hastakalashop.ui.analytics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.hastakalashop.databinding.FragmentAnalyticsBinding
import com.example.hastakalashop.ui.ShopViewModel
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class AnalyticsFragment : Fragment() {
    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.allProducts.observe(viewLifecycleOwner) { products ->
            setupPieChart(products)
            setupBarChart(products)
        }

        viewModel.allSales.observe(viewLifecycleOwner) { sales ->
            setupLineChart(sales)
        }
    }

    private fun setupPieChart(products: List<com.example.hastakalashop.data.Product>) {
        val entries = products.filter { it.soldCount > 0 }.map {
            PieEntry(it.soldCount.toFloat(), it.name)
        }

        if (entries.isEmpty()) {
            binding.pieChart.setNoDataText("No sales data yet")
            return
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#800000"), // Maroon
            Color.parseColor("#D4AF37"), // Gold
            Color.parseColor("#2E7D32"), // Green
            Color.parseColor("#00BFA5"), // Teal
            Color.parseColor("#795548")  // Brown
        )
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f
        dataSet.sliceSpace = 3f

        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.centerText = "Sales Share"
        binding.pieChart.setCenterTextSize(16f)
        binding.pieChart.setHoleColor(Color.TRANSPARENT)
        binding.pieChart.animateXY(1000, 1000)
        binding.pieChart.legend.isEnabled = true
        binding.pieChart.legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
        binding.pieChart.invalidate()
    }

    private fun setupLineChart(sales: List<com.example.hastakalashop.data.Sale>) {
        val entries = sales.take(10).reversed().mapIndexed { index, sale ->
            Entry(index.toFloat(), sale.totalPrice.toFloat())
        }

        if (entries.isEmpty()) {
            binding.lineChart.setNoDataText("Awaiting sales data...")
            return
        }

        val dataSet = LineDataSet(entries, "Revenue (₹)")
        dataSet.color = Color.parseColor("#D4AF37") // Gold
        dataSet.setCircleColor(Color.parseColor("#800000"))
        dataSet.lineWidth = 4f
        dataSet.circleRadius = 6f
        dataSet.setDrawCircleHole(true)
        dataSet.valueTextSize = 10f
        dataSet.setDrawFilled(true)
        dataSet.fillColor = Color.parseColor("#D4AF37")
        dataSet.fillAlpha = 30
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        binding.lineChart.description.isEnabled = false
        binding.lineChart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        binding.lineChart.xAxis.setDrawGridLines(false)
        binding.lineChart.axisRight.isEnabled = false
        binding.lineChart.animateX(1200)
        binding.lineChart.invalidate()
    }

    private fun setupBarChart(products: List<com.example.hastakalashop.data.Product>) {
        val sortedProducts = products.sortedByDescending { it.soldCount }
        val entries = sortedProducts.mapIndexed { index, product ->
            BarEntry(index.toFloat(), product.soldCount.toFloat())
        }

        if (entries.isEmpty() || entries.all { it.y == 0f }) {
            binding.barChart.setNoDataText("No sales data")
            return
        }

        val dataSet = BarDataSet(entries, "Units Sold")
        
        // Specific color mapping as requested by user
        val colors = sortedProducts.map { product ->
            when {
                product.name.contains("Blue Bag", ignoreCase = true) -> Color.parseColor("#2196F3") // Blue
                product.name.contains("Red Bag", ignoreCase = true) -> Color.parseColor("#F44336") // Red
                product.name.contains("Basket", ignoreCase = true) -> Color.parseColor("#795548") // Brown
                product.name.contains("Keychain", ignoreCase = true) -> Color.parseColor("#FFEB3B") // Yellow
                product.name.contains("Bamboo", ignoreCase = true) -> Color.parseColor("#4CAF50") // Green
                else -> Color.parseColor("#D4AF37") // Gold/Default
            }
        }
        
        dataSet.colors = colors
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barData.barWidth = 0.7f
        binding.barChart.data = barData
        binding.barChart.description.isEnabled = false
        
        val xAxis = binding.barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(sortedProducts.map { it.name })
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -30f
        xAxis.setDrawGridLines(false)
        
        binding.barChart.axisRight.isEnabled = false
        binding.barChart.axisLeft.axisMinimum = 0f
        binding.barChart.animateY(1500)
        binding.barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
