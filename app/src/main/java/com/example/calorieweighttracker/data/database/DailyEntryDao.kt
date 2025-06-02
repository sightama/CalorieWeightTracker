package com.example.calorieweighttracker.data.database

import androidx.room.*
import com.example.calorieweighttracker.data.model.DailyEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for daily entries
 * Provides CRUD operations and queries
 */
@Dao
interface DailyEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: DailyEntry)

    @Query("SELECT * FROM daily_entries WHERE date = :date")
    suspend fun getEntryByDate(date: LocalDate): DailyEntry?

    @Query("SELECT * FROM daily_entries WHERE date = :date")
    fun observeEntryByDate(date: LocalDate): Flow<DailyEntry?>

    @Query("SELECT * FROM daily_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<DailyEntry>>

    @Query("SELECT * FROM daily_entries WHERE weight IS NOT NULL ORDER BY date ASC")
    fun getAllEntriesWithWeight(): Flow<List<DailyEntry>>

    @Delete
    suspend fun delete(entry: DailyEntry)

    @Query("DELETE FROM daily_entries WHERE date = :date")
    suspend fun deleteByDate(date: LocalDate)
}