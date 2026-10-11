package com.lockin.dashboard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_log")
data class DayLog(
    @PrimaryKey val dateKey: String, // YYYY-MM-DD
    val scratchpad: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
