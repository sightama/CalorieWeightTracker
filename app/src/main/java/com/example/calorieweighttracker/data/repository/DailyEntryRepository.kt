package com.example.calorieweighttracker.data.repository

import com.example.calorieweighttracker.data.database.DailyEntryDao
import com.example.calorieweighttracker.data.model.DailyEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository pattern implementation for clean architecture
 * Provides a clean API for data access to the rest of the app
 */
class DailyEntryRepository(private val dao: DailyEntryDao) {

    suspend fun insertOrUpdate(entry: DailyEntry) {
        dao.insertOrUpdate(entry)
    }

    suspend fun getEntryByDate(date: LocalDate): DailyEntry? {
        return dao.getEntryByDate(date)
    }

    fun observeEntryByDate(date: LocalDate): Flow<DailyEntry?> {
        return dao.observeEntryByDate(date)
    }

    fun getAllEntries(): Flow<List<DailyEntry>> {
        return dao.getAllEntries()
    }

    fun getAllEntriesWithWeight(): Flow<List<DailyEntry>> {
        return dao.getAllEntriesWithWeight()
    }

    suspend fun deleteByDate(date: LocalDate) {
        dao.deleteByDate(date)
    }
}