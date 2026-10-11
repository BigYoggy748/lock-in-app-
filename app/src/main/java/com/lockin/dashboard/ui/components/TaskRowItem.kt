package com.lockin.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.data.model.Priority
import com.lockin.dashboard.data.model.Task
import com.lockin.dashboard.ui.theme.LockIn

@Composable
fun TaskRowItem(
    index: Int,
    task: Task,
    onToggle: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val done = task.isCompleted
    val alpha = if (done) 0.45f else 1f
    val priorityColor = when (task.priority) {
        Priority.HIGH -> LockIn.PriorityHigh
        Priority.MED  -> LockIn.PriorityMed
        Priority.LOW  -> LockIn.PriorityLow
    }
    val estLabel = if (task.estMinutes < 60) "${task.estMinutes}m"
    else "${task.estMinutes / 60}h${if (task.estMinutes % 60 != 0) " ${task.estMinutes % 60}m" else ""}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LockIn.Card)
            .border(
                1.dp,
                if (done) LockIn.Border.copy(alpha = 0.5f) else LockIn.Border,
                RoundedCornerShape(16.dp)
            )
            .clickable { onToggle(task) }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Checkbox is purely visual so the row handles the click exclusively
        Checkbox(
            checked = done,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkedColor = LockIn.White,
                uncheckedColor = LockIn.Border,
                checkmarkColor = LockIn.Background
            ),
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(14.dp))

        // Task info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = LockIn.TextPrimary.copy(alpha = alpha),
                textDecoration = if (done) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "⏱ $estLabel",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = LockIn.TextSecondary.copy(alpha = alpha)
            )
        }

        Spacer(Modifier.width(10.dp))

        // Priority badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(priorityColor.copy(alpha = 0.15f))
                .border(1.dp, priorityColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = task.priority.name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = priorityColor.copy(alpha = alpha)
            )
        }
    }
}
