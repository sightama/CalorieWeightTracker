package com.example.calorieweighttracker.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieweighttracker.data.database.AppDatabase
import com.example.calorieweighttracker.data.model.DailyEntry
import com.example.calorieweighttracker.data.repository.DailyEntryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel for the graph screen
 * Provides weight data for visualization
 */
class GraphViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DailyEntryRepository

    // All entries with weight data for graphing
    val entriesWithWeight: StateFlow<List<DailyEntry>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DailyEntryRepository(database.dailyEntryDao())

        entriesWithWeight = repository.getAllEntriesWithWeight()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }
}