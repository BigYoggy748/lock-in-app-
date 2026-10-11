package com.lockin.dashboard.data.dao

import androidx.room.*
import com.lockin.dashboard.data.model.DayLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DayLogDao {
    @Query("SELECT * FROM day_log WHERE dateKey = :dateKey")
    fun getByDate(dateKey: String): Flow<DayLog?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(dayLog: DayLog)

    @Query("UPDATE day_log SET scratchpad = :text, updatedAt = :updatedAt WHERE dateKey = :dateKey")
    suspend fun updateScratchpad(dateKey: String, text: String, updatedAt: Long)
}
