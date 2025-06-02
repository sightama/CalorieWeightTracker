package com.example.calorieweighttracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entity representing a daily entry for calories and weight
 * Uses LocalDate as primary key (stored as Long epoch day)
 */
@Entity(tableName = "daily_entries")
data class DailyEntry(
    @PrimaryKey
    val date: LocalDate,
    val calories: Float?,
    val weight: Float?
)