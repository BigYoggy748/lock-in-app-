package com.lockin.dashboard.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.ui.theme.LockIn
import com.lockin.dashboard.viewmodel.TimerState

@Composable
fun FloatingTimerPill(
    remainingSeconds: Int,
    timerState: TimerState,
    onPillClicked: () -> Unit,
    onTogglePlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVisible = timerState == TimerState.RUNNING || timerState == TimerState.PAUSED
    val mins = remainingSeconds / 60
    val secs = remainingSeconds % 60
    val timeLabel = "%02d:%02d".format(mins, secs)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(LockIn.Card)
                .border(1.dp, LockIn.FocusBlue.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                .clickable { onPillClicked() }
                .padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Pulsing status dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (timerState == TimerState.RUNNING) LockIn.FocusBlue.copy(alpha = pulseAlpha)
                        else LockIn.TextSecondary
                    )
            )

            // Timer text
            Text(
                text = "FOCUS $timeLabel",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = LockIn.TextPrimary,
                letterSpacing = 0.5.sp
            )

            // Mini play/pause icon
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(LockIn.White)
                    .clickable { onTogglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (timerState == TimerState.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = LockIn.Background,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
