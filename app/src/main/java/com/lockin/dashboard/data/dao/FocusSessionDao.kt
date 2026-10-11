package com.lockin.dashboard.data.dao

import androidx.room.*
import com.lockin.dashboard.data.model.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_session WHERE dateKey = :dateKey ORDER BY completedAt DESC")
    fun getSessionsForDate(dateKey: String): Flow<List<FocusSession>>

    @Insert
    suspend fun insert(session: FocusSession)

    @Query("SELECT COUNT(*) FROM focus_session WHERE dateKey = :dateKey")
    suspend fun countForDate(dateKey: String): Int
}
