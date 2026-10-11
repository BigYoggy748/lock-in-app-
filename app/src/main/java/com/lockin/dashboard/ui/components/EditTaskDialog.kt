package com.lockin.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lockin.dashboard.data.model.Priority
import com.lockin.dashboard.data.model.Task
import com.lockin.dashboard.ui.theme.LockIn

@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onSave: (updatedTask: Task) -> Unit,
    onDelete: (taskId: String) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var priority by remember { mutableStateOf(task.priority) }

    val initialH = task.estMinutes / 60
    val initialM = task.estMinutes % 60

    var selectedHours by remember { mutableIntStateOf(initialH) }
    var selectedMinutes by remember { mutableIntStateOf(initialM) }

    val totalEstMinutes = (selectedHours * 60) + selectedMinutes

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(LockIn.Surface)
                .padding(22.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Edit Task",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.TextPrimary
                )

                IconButton(
                    onClick = {
                        onDelete(task.id)
                        onDismiss()
                    }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Task",
                        tint = LockIn.PriorityHigh
                    )
                }
            }

            // Title input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title", color = LockIn.TextSecondary, fontSize = 12.sp) },
                textStyle = TextStyle(color = LockIn.TextPrimary, fontSize = 15.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LockIn.White,
                    unfocusedBorderColor = LockIn.Border,
                    cursorColor = LockIn.White,
                    focusedLabelColor = LockIn.White,
                    unfocusedLabelColor = LockIn.TextSecondary
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Priority Selector
            Column {
                Text("Priority", fontSize = 12.sp, color = LockIn.TextSecondary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.entries.forEach { p ->
                        val sel = p == priority
                        val color = when (p) {
                            Priority.HIGH -> LockIn.PriorityHigh
                            Priority.MED  -> LockIn.PriorityMed
                            Priority.LOW  -> LockIn.PriorityLow
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (sel) color.copy(alpha = 0.2f) else LockIn.Card)
                                .border(1.dp, if (sel) color else LockIn.Border, RoundedCornerShape(10.dp))
                                .clickable { priority = p }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = p.name,
                                fontSize = 13.sp,
                                fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                color = if (sel) color else LockIn.TextSecondary
                            )
                        }
                    }
                }
            }

            // Duration Wheel Picker
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Time Duration", fontSize = 12.sp, color = LockIn.TextSecondary)
                    val durationText = when {
                        selectedHours == 0 && selectedMinutes == 0 -> "0 min"
                        selectedHours == 0 -> "$selectedMinutes min"
                        selectedMinutes == 0 -> "$selectedHours hr"
                        else -> "$selectedHours hr $selectedMinutes min"
                    }
                    Text(
                        text = durationText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = LockIn.White
                    )
                }

                WheelDurationPicker(
                    initialHours = selectedHours,
                    initialMinutes = selectedMinutes,
                    onDurationChanged = { h, m ->
                        selectedHours = h
                        selectedMinutes = m
                    }
                )
            }

            // Save Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = LockIn.TextSecondary)
                }
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val finalEst = if (totalEstMinutes > 0) totalEstMinutes else 30
                            val updated = task.copy(
                                title = title.trim(),
                                priority = priority,
                                estMinutes = finalEst
                            )
                            onSave(updated)
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LockIn.White,
                        contentColor = LockIn.Background
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
