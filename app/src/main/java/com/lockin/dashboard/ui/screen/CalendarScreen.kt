package com.lockin.dashboard.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lockin.dashboard.data.model.Task
import com.lockin.dashboard.ui.components.CalendarGrid
import com.lockin.dashboard.ui.components.TaskRowItem
import com.lockin.dashboard.ui.theme.LockIn
import com.lockin.dashboard.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(vm: DashboardViewModel) {
    val calendarMonth by vm.calendarMonth.collectAsStateWithLifecycle()
    val calendarTasks by vm.calendarTasks.collectAsStateWithLifecycle()
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val selectedDate by vm.selectedDate.collectAsStateWithLifecycle()

    val monthLabel = calendarMonth.format(
        DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LockIn.Background)
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text("Calendar", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LockIn.TextPrimary)

        // Month nav
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { vm.previousMonth() }) {
                Icon(Icons.Default.KeyboardArrowLeft, null, tint = LockIn.TextSecondary)
            }
            Text(
                text = monthLabel,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = LockIn.TextPrimary
            )
            IconButton(onClick = { vm.nextMonth() }) {
                Icon(Icons.Default.KeyboardArrowRight, null, tint = LockIn.TextSecondary)
            }
        }

        // Calendar grid
        CalendarGrid(
            month = calendarMonth,
            taskCountByDate = calendarTasks,
            selectedDate = selectedDate,
            onDayClick = { date ->
                vm.selectDate(date)
            }
        )

        // Tasks for selected day
        val selectedDateLabel = when (selectedDate) {
            LocalDate.now() -> "Today's Tasks"
            else -> selectedDate.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)) + "'s Tasks"
        }

        if (tasks.isNotEmpty()) {
            Text(
                text = selectedDateLabel,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = LockIn.TextPrimary
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskRowItem(
                        index = tasks.indexOf(task) + 1,
                        task = task,
                        onToggle = { vm.toggleTask(it) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No tasks for this day",
                    fontSize = 14.sp,
                    color = LockIn.TextSecondary
                )
            }
        }
    }
}
