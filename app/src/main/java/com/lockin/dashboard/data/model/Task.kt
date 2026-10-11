package com.lockin.dashboard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class Task(
    @PrimaryKey val id: String,           // UUID String
    val dateKey: String,                   // Foreign key -> DayLog.dateKey
    val orderIndex: Int,
    val title: String,
    val priority: Priority,
    val estMinutes: Int,
    val isCompleted: Boolean = false,
    val durationDays: Int = 1,
    val endDateKey: String = dateKey
)

