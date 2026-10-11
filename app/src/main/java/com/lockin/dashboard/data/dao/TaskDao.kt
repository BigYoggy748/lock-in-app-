package com.lockin.dashboard.data.dao

import androidx.room.*
import com.lockin.dashboard.data.model.DayTaskSummary
import com.lockin.dashboard.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM task WHERE (dateKey = :dateKey OR (dateKey <= :dateKey AND endDateKey >= :dateKey)) ORDER BY isCompleted ASC, orderIndex ASC")
    fun getTasksForDate(dateKey: String): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE NOT (endDateKey < :from OR dateKey > :to) ORDER BY dateKey ASC, isCompleted ASC, orderIndex ASC")
    suspend fun getTasksForRange(from: String, to: String): List<Task>

    @Query("""
        SELECT dateKey,
               COUNT(*) as total,
               SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) as completed
        FROM task
        WHERE dateKey >= :fromDate
        GROUP BY dateKey
        ORDER BY dateKey ASC
    """)
    suspend fun getDaySummariesFrom(fromDate: String): List<DayTaskSummary>

    @Query("SELECT DISTINCT dateKey FROM task WHERE isCompleted = 1 ORDER BY dateKey DESC")
    suspend fun getDatesWithCompletions(): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Query("UPDATE task SET isCompleted = :completed WHERE id = :id")
    suspend fun setCompleted(id: String, completed: Boolean)

    @Query("SELECT COUNT(*) FROM task WHERE (dateKey = :dateKey OR (dateKey <= :dateKey AND endDateKey >= :dateKey))")
    suspend fun countForDate(dateKey: String): Int

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun deleteById(id: String)

    @Update
    suspend fun update(task: Task)
}

