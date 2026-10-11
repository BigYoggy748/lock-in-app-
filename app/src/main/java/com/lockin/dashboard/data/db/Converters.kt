package com.lockin.dashboard.data.db

import androidx.room.TypeConverter
import com.lockin.dashboard.data.model.Priority

class Converters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)
}
