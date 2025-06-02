package com.example.calorieweighttracker.ui.screens

import android.graphics.Color
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calorieweighttracker.ui.viewmodels.GraphViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Graph screen showing weight trend over time
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphScreen(
    onNavigateBack: () -> Unit,
    viewModel: GraphViewModel = viewModel()
) {
    val entriesWithWeight by viewModel.entriesWithWeight.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weight Trend") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (entriesWithWeight.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No weight data available.\nStart tracking your weight to see trends!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Weight chart
                Card(
                    modifier = Modifier.fillMaxSize(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    WeightLineChart(
                        entries = entriesWithWeight,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Composable wrapper for MPAndroidChart LineChart
 */
@Composable
fun WeightLineChart(
    entries: List<com.example.calorieweighttracker.data.model.DailyEntry>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                // Configure chart appearance
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)

                // Configure axes
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f

                    // Format x-axis labels as dates
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormatter = DateTimeFormatter.ofPattern("MM/dd")
                        override fun getFormattedValue(value: Float): String {
                            val epochDay = value.toLong()
                            return try {
                                LocalDate.ofEpochDay(epochDay).format(dateFormatter)
                            } catch (e: Exception) {
                                ""
                            }
                        }
                    }
                }

                // Configure left y-axis (weight)
                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                }

                // Disable right y-axis
                axisRight.isEnabled = false

                // Configure legend
                legend.apply {
                    isEnabled = true
                    textSize = 12f
                }
            }
        },
        update = { chart ->
            // Convert entries to chart data
            val chartEntries = entries.mapNotNull { entry ->
                entry.weight?.let { weight ->
                    Entry(entry.date.toEpochDay().toFloat(), weight)
                }
            }.sortedBy { it.x }

            if (chartEntries.isNotEmpty()) {
                val dataSet = LineDataSet(chartEntries, "Weight (lbs)").apply {
                    color = Color.parseColor("#1976D2")
                    valueTextColor = Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                    setCircleColor(Color.parseColor("#1976D2"))
                    setDrawCircleHole(false)
                    setDrawValues(true)
                    valueTextSize = 10f

                    // Format values to show 1 decimal place
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return String.format("%.1f", value)
                        }
                    }
                }

                chart.data = LineData(dataSet)
                chart.invalidate()
            } else {
                chart.clear()
            }
        },
        modifier = modifier
    )
}