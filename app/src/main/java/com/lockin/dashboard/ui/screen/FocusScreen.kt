package com.lockin.dashboard.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lockin.dashboard.audio.Soundscape
import com.lockin.dashboard.data.FocusSound
import com.lockin.dashboard.ui.components.FocusSettingsDialog
import com.lockin.dashboard.ui.components.SoundscapePicker
import com.lockin.dashboard.ui.theme.LockIn
import com.lockin.dashboard.viewmodel.FocusViewModel
import com.lockin.dashboard.viewmodel.TimerState

@Composable
fun FocusScreen(vm: FocusViewModel = viewModel()) {
    val timerState by vm.timerState.collectAsStateWithLifecycle()
    val remaining by vm.remainingSeconds.collectAsStateWithLifecycle()
    val selectedMins by vm.selectedMinutes.collectAsStateWithLifecycle()
    val sessions by vm.sessions.collectAsStateWithLifecycle()

    val currentSound by vm.focusSound.collectAsStateWithLifecycle()
    val vibrationEnabled by vm.vibrationEnabled.collectAsStateWithLifecycle()
    val soundscape by vm.soundscape.collectAsStateWithLifecycle()
    val soundscapeVolume by vm.soundscapeVolume.collectAsStateWithLifecycle()

    var showSettingsDialog by remember { mutableStateOf(false) }

    val totalSecs = selectedMins * 60
    val progress = if (totalSecs == 0) 0f else 1f - (remaining.toFloat() / totalSecs)
    val animProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(500),
        label = "focus"
    )
    val mins = remaining / 60
    val secs = remaining % 60
    val timeLabel = "%02d:%02d".format(mins, secs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LockIn.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Header with Settings Gear Button ─────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Focus", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LockIn.TextPrimary)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (vibrationEnabled) Icons.Default.Vibration else Icons.Default.Notifications,
                        contentDescription = null,
                        tint = LockIn.TextSecondary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${currentSound.label}${if (vibrationEnabled) " + Vibrate" else ""}",
                        fontSize = 12.sp,
                        color = LockIn.TextSecondary
                    )
                }
            }

            IconButton(
                onClick = { showSettingsDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LockIn.Card)
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Timer Sound & Vibration Settings",
                    tint = LockIn.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // ── Duration Presets ─────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(15, 25, 45, 60, 90).forEach { dur ->
                val sel = dur == selectedMins
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (sel) LockIn.White else LockIn.Card)
                        .border(
                            1.dp,
                            if (sel) LockIn.White else LockIn.Border,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            enabled = timerState == TimerState.IDLE || timerState == TimerState.FINISHED
                        ) { vm.selectDuration(dur) }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = "${dur}m",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (sel) LockIn.Background else LockIn.TextSecondary
                    )
                }
            }
        }

        // ── Circular Countdown Ring Timer ────────────────────────────────────
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val sw = 14.dp.toPx()
                val d = size.minDimension - sw
                val tl = Offset((size.width - d) / 2, (size.height - d) / 2)
                val sz = Size(d, d)
                drawArc(
                    color = LockIn.Border,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(sw, cap = StrokeCap.Round),
                    topLeft = tl,
                    size = sz
                )
                if (animProgress > 0f) {
                    drawArc(
                        color = LockIn.FocusBlue,
                        startAngle = -90f,
                        sweepAngle = 360f * animProgress,
                        useCenter = false,
                        style = Stroke(sw, cap = StrokeCap.Round),
                        topLeft = tl,
                        size = sz
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeLabel,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = when (timerState) {
                        TimerState.RUNNING  -> "Flow state active ⚡"
                        TimerState.PAUSED   -> "Paused"
                        TimerState.FINISHED -> "Session complete 🎉 (+${selectedMins * 10} XP)"
                        TimerState.IDLE     -> "Ready to lock in"
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (timerState == TimerState.FINISHED) LockIn.PriorityLow else LockIn.TextSecondary
                )
            }
        }

        // ── Controls (Reset & Play/Pause) ────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(LockIn.Card)
                    .border(1.dp, LockIn.Border, CircleShape)
                    .clickable { vm.reset() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = LockIn.TextSecondary, modifier = Modifier.size(22.dp))
            }

            // Play / Pause
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(LockIn.White)
                    .clickable { vm.togglePlayPause() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (timerState == TimerState.RUNNING) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (timerState == TimerState.RUNNING) "Pause" else "Start",
                    tint = LockIn.Background,
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        // ── Ambient Soundscape Synthesizer ───────────────────────────────────
        SoundscapePicker(
            selectedSoundscape = soundscape,
            volume = soundscapeVolume,
            isTimerRunning = timerState == TimerState.RUNNING,
            onSoundscapeSelected = { vm.setSoundscape(it) },
            onVolumeChanged = { vm.setSoundscapeVolume(it) }
        )

        // ── Session History Log ──────────────────────────────────────────────
        if (sessions.isNotEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Today's Focus Log (${sessions.size} sessions · ${sessions.sumOf { it.durationMinutes }}m total)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LockIn.TextPrimary
                )
                sessions.take(5).forEach { session ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(LockIn.Card)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⏱ ${session.durationMinutes}m focus session", fontSize = 13.sp, color = LockIn.TextPrimary)
                        Text("+${session.durationMinutes * 10} XP", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = LockIn.FocusBlue)
                    }
                }
            }
        }
    }

    // Settings Dialog
    if (showSettingsDialog) {
        FocusSettingsDialog(
            currentSound = currentSound,
            vibrationEnabled = vibrationEnabled,
            onSoundSelected = { vm.setFocusSound(it) },
            onVibrationToggled = { vm.setVibrationEnabled(it) },
            onPreviewSound = { vm.previewSound(it) },
            onStopPreview = { vm.stopSoundPreview() },
            onDismiss = { showSettingsDialog = false }
        )
    }
}
