package com.lockin.dashboard.data.repository

import com.lockin.dashboard.data.dao.DayLogDao
import com.lockin.dashboard.data.dao.FocusSessionDao
import com.lockin.dashboard.data.dao.NoteDao
import com.lockin.dashboard.data.dao.TaskDao
import com.lockin.dashboard.data.model.DayLog
import com.lockin.dashboard.data.model.DayTaskSummary
import com.lockin.dashboard.data.model.FocusSession
import com.lockin.dashboard.data.model.Note
import com.lockin.dashboard.data.model.Task
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class LockInRepository(
    private val dayLogDao: DayLogDao,
    private val taskDao: TaskDao,
    private val focusSessionDao: FocusSessionDao,
    private val noteDao: NoteDao
) {
    // ── Day Log ──────────────────────────────────────────────────────────────
    fun getDayLog(dateKey: String): Flow<DayLog?> = dayLogDao.getByDate(dateKey)

    suspend fun ensureDayLog(dateKey: String) {
        dayLogDao.insertIfAbsent(DayLog(dateKey = dateKey))
    }

    suspend fun saveScratchpad(dateKey: String, text: String) {
        dayLogDao.updateScratchpad(dateKey, text, System.currentTimeMillis())
    }

    // ── Tasks ────────────────────────────────────────────────────────────────
    fun getTasks(dateKey: String): Flow<List<Task>> = taskDao.getTasksForDate(dateKey)

    suspend fun addTask(task: Task) = taskDao.insert(task)

    suspend fun toggleTask(id: String, completed: Boolean) = taskDao.setCompleted(id, completed)

    suspend fun deleteTask(id: String) = taskDao.deleteById(id)

    suspend fun updateTask(task: Task) = taskDao.update(task)

    suspend fun countTasks(dateKey: String): Int = taskDao.countForDate(dateKey)

    /** Returns task summaries for the last [days] days (for dot grid & chart) */
    suspend fun getDaySummaries(days: Int = 30): List<DayTaskSummary> {
        val from = LocalDate.now().minusDays(days.toLong()).toString()
        return taskDao.getDaySummariesFrom(from)
    }

    /** Returns streak: consecutive days ending today where >= 1 task was completed */
    suspend fun getStreak(): Int {
        val datesWithCompletions = taskDao.getDatesWithCompletions().toSet()
        var streak = 0
        var date = LocalDate.now()
        while (datesWithCompletions.contains(date.toString())) {
            streak++
            date = date.minusDays(1)
        }
        return streak
    }

    suspend fun getTasksForRange(from: String, to: String): List<Task> =
        taskDao.getTasksForRange(from, to)

    // ── Notes ─────────────────────────────────────────────────────────────────
    fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    suspend fun getNoteById(id: String): Note? = noteDao.getNoteById(id)

    fun searchNotes(query: String): Flow<List<Note>> = noteDao.searchNotes(query)

    fun getNotesByCategory(category: String): Flow<List<Note>> =
        if (category.equals("All", ignoreCase = true)) noteDao.getAllNotes()
        else noteDao.getNotesByCategory(category)

    suspend fun saveNote(note: Note) = noteDao.insertOrUpdate(note)

    suspend fun deleteNote(id: String) = noteDao.deleteById(id)

    suspend fun togglePinNote(id: String, isPinned: Boolean) = noteDao.setPinned(id, isPinned)

    // ── Focus Sessions ───────────────────────────────────────────────────────
    fun getFocusSessions(dateKey: String): Flow<List<FocusSession>> =
        focusSessionDao.getSessionsForDate(dateKey)

    suspend fun saveFocusSession(session: FocusSession) = focusSessionDao.insert(session)
}

