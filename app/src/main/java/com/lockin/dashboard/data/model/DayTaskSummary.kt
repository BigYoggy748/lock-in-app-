package com.lockin.dashboard.data.model

import androidx.room.ColumnInfo

data class DayTaskSummary(
    @ColumnInfo(name = "dateKey") val dateKey: String,
    @ColumnInfo(name = "total") val total: Int,
    @ColumnInfo(name = "completed") val completed: Int
)
