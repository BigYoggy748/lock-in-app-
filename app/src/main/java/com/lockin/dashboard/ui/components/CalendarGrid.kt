package com.lockin.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.ui.theme.LockIn
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarGrid(
    month: LocalDate,       // first day of month
    taskCountByDate: Map<String, Int>,
    selectedDate: LocalDate?,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val yearMonth = YearMonth.of(month.year, month.month)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDow = month.dayOfWeek.value % 7  // 0 = Sunday

    Column(modifier = modifier.fillMaxWidth()) {
        // Day-of-week headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Su","Mo","Tu","We","Th","Fr","Sa").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    color = LockIn.TextSecondary
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        // Day cells
        val totalCells = firstDow + daysInMonth
        val rows = (totalCells + 6) / 7
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIdx = row * 7 + col
                    val dayNum = cellIdx - firstDow + 1
                    if (dayNum < 1 || dayNum > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = month.withDayOfMonth(dayNum)
                        val dateKey = date.toString()
                        val isToday = date == today
                        val isSelected = date == selectedDate
                        val hasTasks = (taskCountByDate[dateKey] ?: 0) > 0
                        val isFuture = date.isAfter(today)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> LockIn.White
                                        isToday    -> LockIn.Card
                                        else       -> androidx.compose.ui.graphics.Color.Transparent
                                    }
                                )
                                .clickable(enabled = !isFuture) { onDayClick(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = dayNum.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = when {
                                        isSelected -> LockIn.Background
                                        isFuture   -> LockIn.TextSecondary.copy(alpha = 0.4f)
                                        else       -> LockIn.TextPrimary
                                    }
                                )
                                if (hasTasks) {
                                    Spacer(Modifier.height(1.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) LockIn.Background
                                                else LockIn.White
                                            )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}
