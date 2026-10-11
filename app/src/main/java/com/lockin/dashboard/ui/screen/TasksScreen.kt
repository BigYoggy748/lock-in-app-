package com.lockin.dashboard.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lockin.dashboard.data.model.Task
import com.lockin.dashboard.ui.components.AddTaskDialog
import com.lockin.dashboard.ui.components.EditTaskDialog
import com.lockin.dashboard.ui.components.TaskRowItem
import com.lockin.dashboard.ui.theme.LockIn
import com.lockin.dashboard.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TasksScreen(vm: DashboardViewModel) {
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val selectedDate by vm.selectedDate.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    val isToday = selectedDate == LocalDate.now()
    val dateLabel = when {
        isToday -> "Today"
        selectedDate == LocalDate.now().minusDays(1) -> "Yesterday"
        else -> selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH))
    }
    val completed = tasks.count { it.isCompleted }
    val total = tasks.size

    Box(modifier = Modifier.fillMaxSize().background(LockIn.Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 48.dp)
        ) {
            // Header + date nav
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Tasks", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LockIn.TextPrimary)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "$completed of $total completed · Long press to edit",
                        fontSize = 12.sp,
                        color = LockIn.TextSecondary
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { vm.previousDay() }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous Day", tint = LockIn.TextSecondary)
                    }
                    Text(
                        text = dateLabel,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = LockIn.TextPrimary
                    )
                    IconButton(
                        onClick = { vm.nextDay() },
                        enabled = !isToday
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next Day",
                            tint = if (isToday) LockIn.Border else LockIn.TextSecondary
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // Task list
            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("✓", fontSize = 40.sp, color = LockIn.Border)
                        Spacer(Modifier.height(12.dp))
                        Text("Nothing scheduled", fontSize = 16.sp, color = LockIn.TextSecondary)
                        Spacer(Modifier.height(4.dp))
                        Text("Tap + to add a task", fontSize = 13.sp, color = LockIn.TextSecondary.copy(alpha = 0.6f))
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(tasks, key = { _, task -> task.id }) { index, task ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { vm.toggleTask(task) },
                                    onLongClick = { taskToEdit = task }
                                )
                        ) {
                            TaskRowItem(
                                index = index + 1,
                                task = task,
                                onToggle = { vm.toggleTask(it) }
                            )
                        }
                    }
                }
            }
        }

        // FAB positioned lower, comfortably above the bottom navigation bar
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp),
            containerColor = LockIn.White,
            contentColor = LockIn.Background,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add task")
        }
    }

    // Add Task Dialog
    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, priority, mins ->
                vm.addTask(title, priority, mins)
                showAddDialog = false
            }
        )
    }

    // Edit Task Dialog
    taskToEdit?.let { task ->
        EditTaskDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onSave = { updated ->
                vm.updateTask(updated)
                taskToEdit = null
            },
            onDelete = { id ->
                vm.deleteTaskById(id)
                taskToEdit = null
            }
        )
    }
}
