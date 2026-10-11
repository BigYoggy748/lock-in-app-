package com.lockin.dashboard.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_session")
data class FocusSession(
    @PrimaryKey val id: String,
    val dateKey: String,
    val durationMinutes: Int,
    val completedAt: Long = System.currentTimeMillis()
)
