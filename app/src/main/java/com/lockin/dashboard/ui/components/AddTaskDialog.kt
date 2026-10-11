package com.lockin.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.lockin.dashboard.ui.theme.LockIn

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, priority: Priority, estMinutes: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MED) }

    var selectedHours by remember { mutableIntStateOf(0) }
    var selectedMinutes by remember { mutableIntStateOf(30) }

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
            Text(
                "New Task",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = LockIn.TextPrimary
            )

            // ── Title Input ──────────────────────────────────────────────────
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("What needs to be done?", color = LockIn.TextSecondary, fontSize = 14.sp) },
                textStyle = TextStyle(color = LockIn.TextPrimary, fontSize = 15.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LockIn.White,
                    unfocusedBorderColor = LockIn.Border,
                    cursorColor = LockIn.White
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // ── Priority Selector ────────────────────────────────────────────
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

            // ── Duration Wheel Picker (Hours & Minutes) ──────────────────────
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

                // Rotating Drum / Wheel Picker
                WheelDurationPicker(
                    initialHours = selectedHours,
                    initialMinutes = selectedMinutes,
                    onDurationChanged = { h, m ->
                        selectedHours = h
                        selectedMinutes = m
                    }
                )

                // Quick preset duration pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        (0 to 15) to "15m",
                        (0 to 30) to "30m",
                        (0 to 45) to "45m",
                        (1 to 0) to "1h",
                        (1 to 30) to "1.5h",
                        (2 to 0) to "2h"
                    ).forEach { (dur, label) ->
                        val (h, m) = dur
                        val isSelected = selectedHours == h && selectedMinutes == m
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) LockIn.White else LockIn.Card)
                                .clickable {
                                    selectedHours = h
                                    selectedMinutes = m
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) LockIn.Background else LockIn.TextSecondary
                            )
                        }
                    }
                }
            }

            // ── Action Buttons ───────────────────────────────────────────────
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
                            onConfirm(title.trim(), priority, finalEst)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LockIn.White,
                        contentColor = LockIn.Background
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add Task", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
