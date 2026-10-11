package com.lockin.dashboard.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.audio.Soundscape
import com.lockin.dashboard.ui.theme.LockIn
import kotlin.random.Random

@Composable
fun SoundscapePicker(
    selectedSoundscape: Soundscape,
    volume: Float,
    isTimerRunning: Boolean,
    onSoundscapeSelected: (Soundscape) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LockIn.Card)
            .border(1.dp, LockIn.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🎧 Flow Soundscape", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = LockIn.TextPrimary)
                if (selectedSoundscape != Soundscape.NONE && isTimerRunning) {
                    Spacer(Modifier.width(8.dp))
                    EqualizerWave(isPlaying = true)
                }
            }
            Text(
                text = selectedSoundscape.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedSoundscape != Soundscape.NONE) LockIn.FocusBlue else LockIn.TextSecondary
            )
        }

        // Soundscape Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Soundscape.entries.forEach { sound ->
                val isSelected = sound == selectedSoundscape
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) LockIn.White else LockIn.Surface)
                        .border(
                            1.dp,
                            if (isSelected) LockIn.White else LockIn.Border,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSoundscapeSelected(sound) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(sound.icon, fontSize = 13.sp)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = sound.title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) LockIn.Background else LockIn.TextPrimary
                        )
                    }
                }
            }
        }

        // Volume Slider (if soundscape active)
        if (selectedSoundscape != Soundscape.NONE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🔈", fontSize = 12.sp)
                Slider(
                    value = volume,
                    onValueChange = onVolumeChanged,
                    colors = SliderDefaults.colors(
                        thumbColor = LockIn.White,
                        activeTrackColor = LockIn.FocusBlue,
                        inactiveTrackColor = LockIn.Border
                    ),
                    modifier = Modifier.weight(1f)
                )
                Text("🔊", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun EqualizerWave(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "eq")
    val anim1 by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(350, easing = LinearEasing), RepeatMode.Reverse),
        label = "b1"
    )
    val anim2 by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(420, easing = LinearEasing), RepeatMode.Reverse),
        label = "b2"
    )
    val anim3 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(tween(280, easing = LinearEasing), RepeatMode.Reverse),
        label = "b3"
    )

    Row(
        modifier = modifier.height(14.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val heights = if (isPlaying) listOf(anim1, anim2, anim3) else listOf(0.2f, 0.2f, 0.2f)
        heights.forEach { h ->
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(h)
                    .clip(RoundedCornerShape(2.dp))
                    .background(LockIn.FocusBlue)
            )
        }
    }
}
