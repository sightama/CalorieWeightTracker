package com.example.calorieweighttracker.data.database

import androidx.room.TypeConverter
import java.time.LocalDate

/**
 * Type converters for Room database
 * Converts LocalDate to/from Long for storage
 */
class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        // Store as epoch day for easy comparison and ordering
        return date?.toEpochDay()
    }

    @TypeConverter
    fun toLocalDate(epochDay: Long?): LocalDate? {
        return epochDay?.let { LocalDate.ofEpochDay(it) }
    }
}