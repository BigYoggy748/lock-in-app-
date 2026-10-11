package com.lockin.dashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.data.model.DayTaskSummary
import com.lockin.dashboard.ui.theme.LockIn
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyChart(
    summaries: List<DayTaskSummary>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val completionMap = summaries.associate { it.dateKey to it.completed }
    val values = days.map { (completionMap[it.toString()] ?: 0).toFloat() }
    val maxVal = values.maxOrNull()?.takeIf { it > 0f } ?: 1f
    val labels = days.map { it.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).take(1) }

    val barColor = LockIn.White
    val trackColor = LockIn.Border

    Row(
        modifier = modifier.fillMaxWidth().height(80.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.indices.forEach { i ->
            val fraction = (values[i] / maxVal).coerceIn(0f, 1f)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 4.dp)) {
                    val barW = size.width
                    val maxH = size.height
                    val filledH = maxH * fraction
                    // Track
                    drawRoundRect(
                        color = trackColor,
                        topLeft = Offset(0f, 0f),
                        size = Size(barW, maxH),
                        cornerRadius = CornerRadius(4.dp.toPx())
                    )
                    // Bar
                    if (filledH > 0f) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(0f, maxH - filledH),
                            size = Size(barW, filledH),
                            cornerRadius = CornerRadius(4.dp.toPx())
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = labels[i],
                    fontSize = 10.sp,
                    color = if (days[i] == today) LockIn.White else LockIn.TextSecondary
                )
            }
        }
    }
}
