package com.example.calorieweighttracker.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calorieweighttracker.data.database.AppDatabase
import com.example.calorieweighttracker.data.model.DailyEntry
import com.example.calorieweighttracker.data.repository.DailyEntryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * ViewModel for the main screen
 * Handles date navigation and entry management
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DailyEntryRepository = DailyEntryRepository(
        AppDatabase.getDatabase(application).dailyEntryDao()
    )

    // Current date being viewed
    private val _currentDate = MutableStateFlow(LocalDate.now())
    val currentDate: StateFlow<LocalDate> = _currentDate.asStateFlow()

    // Current entry for the selected date
    val currentEntry: StateFlow<DailyEntry?> = _currentDate
        .flatMapLatest { date ->
            repository.observeEntryByDate(date)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    /**
     * Navigate to previous day
     */
    fun navigateToPreviousDay() {
        _currentDate.value = _currentDate.value.minusDays(1)
    }

    /**
     * Navigate to next day (limited to today)
     */
    fun navigateToNextDay() {
        val tomorrow = _currentDate.value.plusDays(1)
        if (!tomorrow.isAfter(LocalDate.now())) {
            _currentDate.value = tomorrow
        }
    }

    /**
     * Check if can navigate to next day
     */
    fun canNavigateToNext(): Boolean {
        return _currentDate.value.isBefore(LocalDate.now())
    }

    /**
     * Save entry for current date
     */
    fun saveEntry(calories: Float?, weight: Float?) {
        viewModelScope.launch {
            // Only save if at least one field has data
            if (calories != null || weight != null) {
                val entry = DailyEntry(
                    date = _currentDate.value,
                    calories = calories,
                    weight = weight
                )
                repository.insertOrUpdate(entry)
            }
        }
    }

    /**
     * Delete entry for current date
     */
    fun deleteEntry() {
        viewModelScope.launch {
            repository.deleteByDate(_currentDate.value)
        }
    }
}