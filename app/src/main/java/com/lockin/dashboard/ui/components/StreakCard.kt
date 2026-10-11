package com.lockin.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.ui.theme.LockIn

@Composable
fun StreakCard(streak: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LockIn.Card)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Current Streak",
                fontSize = 12.sp,
                color = LockIn.TextSecondary
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "\uD83D\uDD25",
                    fontSize = 22.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "$streak",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.TextPrimary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = if (streak == 1) "day" else "days",
                    fontSize = 14.sp,
                    color = LockIn.TextSecondary
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (streak > 0) "Keep it up!" else "Start today",
                fontSize = 12.sp,
                color = if (streak > 0) LockIn.PriorityLow else LockIn.TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "LOCKED IN",
                fontSize = 10.sp,
                color = LockIn.TextSecondary,
                letterSpacing = 1.sp
            )
        }
    }
}
