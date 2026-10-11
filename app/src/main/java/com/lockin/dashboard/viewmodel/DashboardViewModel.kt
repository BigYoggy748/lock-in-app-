package com.lockin.dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lockin.dashboard.data.PreferencesManager
import com.lockin.dashboard.data.db.LockInDatabase
import com.lockin.dashboard.data.model.DayLog
import com.lockin.dashboard.data.model.DayTaskSummary
import com.lockin.dashboard.data.model.Priority
import com.lockin.dashboard.data.model.Task
import com.lockin.dashboard.data.model.UserRank
import com.lockin.dashboard.data.repository.LockInRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

sealed class SaveState { object Idle : SaveState(); object Saving : SaveState(); object Saved : SaveState() }

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LockInDatabase.getDatabase(application)
    private val repository = LockInRepository(db.dayLogDao(), db.taskDao(), db.focusSessionDao(), db.noteDao())
    private val preferencesManager = PreferencesManager.getInstance(application)

    val userName: StateFlow<String> = preferencesManager.userName
    fun setUserName(name: String) = preferencesManager.setUserName(name)

    val userRank: StateFlow<UserRank> = preferencesManager.totalXp
        .map { UserRank.fromXp(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserRank.fromXp(preferencesManager.totalXp.value))

    val todayKey: String = LocalDate.now().toString()

    // ── Selected date for Tasks tab ──────────────────────────────────────────
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    val selectedDateKey: StateFlow<String> = _selectedDate
        .map { it.toString() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), todayKey)

    fun selectDate(date: LocalDate) { _selectedDate.value = date }
    fun previousDay() { _selectedDate.value = _selectedDate.value.minusDays(1) }
    fun nextDay() {
        val next = _selectedDate.value.plusDays(1)
        if (!next.isAfter(LocalDate.now().plusMonths(3))) _selectedDate.value = next
    }

    // ── Tasks for selected date ──────────────────────────────────────────────
    val tasks: StateFlow<List<Task>> = selectedDateKey
        .flatMapLatest { repository.getTasks(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Today's log (notes) ──────────────────────────────────────────────────
    val dayLog: StateFlow<DayLog?> = repository.getDayLog(todayKey)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // ── Save state for scratchpad ────────────────────────────────────────────
    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()
    private var debounceJob: Job? = null

    // ── Home screen stats ────────────────────────────────────────────────────
    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _daySummaries = MutableStateFlow<List<DayTaskSummary>>(emptyList())
    val daySummaries: StateFlow<List<DayTaskSummary>> = _daySummaries.asStateFlow()

    // ── Calendar: tasks by date for dot indicators ───────────────────────────
    private val _calendarTasks = MutableStateFlow<Map<String, Int>>(emptyMap())
    val calendarTasks: StateFlow<Map<String, Int>> = _calendarTasks.asStateFlow()

    private val _calendarMonth = MutableStateFlow(LocalDate.now().withDayOfMonth(1))
    val calendarMonth: StateFlow<LocalDate> = _calendarMonth.asStateFlow()

    fun previousMonth() { _calendarMonth.value = _calendarMonth.value.minusMonths(1) }
    fun nextMonth() { _calendarMonth.value = _calendarMonth.value.plusMonths(1) }

    init {
        viewModelScope.launch { repository.ensureDayLog(todayKey) }
        refreshStats()
        observeCalendarTasks()
    }

    private fun refreshStats() {
        viewModelScope.launch {
            _streak.value = repository.getStreak()
            _daySummaries.value = repository.getDaySummaries(30)
        }
    }

    private fun observeCalendarTasks() {
        viewModelScope.launch {
            _calendarMonth.collect { month ->
                val from = month.toString()
                val to   = month.plusMonths(1).minusDays(1).toString()
                val tasks = repository.getTasksForRange(from, to)
                _calendarTasks.value = tasks.groupBy { it.dateKey }
                    .mapValues { (_, list) -> list.size }
            }
        }
    }

    // ── Actions ──────────────────────────────────────────────────────────────
    fun onScratchpadChange(text: String) {
        _saveState.value = SaveState.Saving
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(350L)
            repository.saveScratchpad(todayKey, text)
            _saveState.value = SaveState.Saved
        }
    }

    fun addTask(
        title: String,
        priority: Priority,
        estMinutes: Int,
        durationDays: Int = 1,
        startDate: LocalDate = _selectedDate.value
    ) {
        viewModelScope.launch {
            val startKey = startDate.toString()
            val endKey = startDate.plusDays((durationDays - 1).coerceAtLeast(0).toLong()).toString()
            val task = Task(
                id           = UUID.randomUUID().toString(),
                dateKey      = startKey,
                orderIndex   = tasks.value.size,
                title        = title,
                priority     = priority,
                estMinutes   = estMinutes,
                durationDays = durationDays,
                endDateKey   = endKey
            )
            repository.addTask(task)
            preferencesManager.addXp(10) // Small XP boost for planning/adding task
            refreshStats()
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            val newCompleted = !task.isCompleted
            repository.toggleTask(task.id, newCompleted)
            if (newCompleted) {
                preferencesManager.addXp(50) // +50 XP for completing task
            }
            refreshStats()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            refreshStats()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task.id)
            refreshStats()
        }
    }

    fun deleteTaskById(id: String) {
        viewModelScope.launch {
            repository.deleteTask(id)
            refreshStats()
        }
    }
}
