package com.lockin.dashboard.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.lockin.dashboard.data.FocusSound
import com.lockin.dashboard.ui.theme.LockIn

@Composable
fun FocusSettingsDialog(
    currentSound: FocusSound,
    vibrationEnabled: Boolean,
    onSoundSelected: (FocusSound) -> Unit,
    onVibrationToggled: (Boolean) -> Unit,
    onPreviewSound: (FocusSound) -> Unit,
    onStopPreview: () -> Unit,
    onDismiss: () -> Unit
) {
    var isPreviewing by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            onStopPreview()
        }
    }

    Dialog(onDismissRequest = {
        onStopPreview()
        onDismiss()
    }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(LockIn.Surface)
                .padding(22.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Timer Alerts & Sound",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.TextPrimary
                )
            }

            // ── Section 1: Completion Sound Selection ────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Completion Sound",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LockIn.TextSecondary
                    )
                    // Test / Preview Button
                    if (currentSound != FocusSound.NONE) {
                        TextButton(
                            onClick = {
                                if (isPreviewing) {
                                    onStopPreview()
                                    isPreviewing = false
                                } else {
                                    onPreviewSound(currentSound)
                                    isPreviewing = true
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                if (isPreviewing) Icons.Default.Stop else Icons.Default.PlayArrow,
                                contentDescription = "Preview Sound",
                                tint = LockIn.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (isPreviewing) "Stop" else "Test Sound",
                                fontSize = 12.sp,
                                color = LockIn.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Sound list items
                FocusSound.entries.forEach { sound ->
                    val isSelected = sound == currentSound
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) LockIn.Card else LockIn.Background)
                            .border(
                                1.dp,
                                if (isSelected) LockIn.White.copy(alpha = 0.5f) else LockIn.Border,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                onSoundSelected(sound)
                                isPreviewing = false
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = if (isSelected) LockIn.White else LockIn.TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = sound.label,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) LockIn.TextPrimary else LockIn.TextSecondary
                            )
                        }

                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                onSoundSelected(sound)
                                isPreviewing = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = LockIn.White,
                                unselectedColor = LockIn.Border
                            )
                        )
                    }
                }
            }

            // ── Section 2: Vibration Toggle (Default ON) ─────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(LockIn.Card)
                    .border(1.dp, LockIn.Border, RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Vibration,
                        contentDescription = null,
                        tint = if (vibrationEnabled) LockIn.White else LockIn.TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Vibrate on Finish",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LockIn.TextPrimary
                        )
                        Text(
                            "Vibrates when timer hits 00:00",
                            fontSize = 11.sp,
                            color = LockIn.TextSecondary
                        )
                    }
                }

                Switch(
                    checked = vibrationEnabled,
                    onCheckedChange = onVibrationToggled,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LockIn.Background,
                        checkedTrackColor = LockIn.White,
                        uncheckedThumbColor = LockIn.TextSecondary,
                        uncheckedTrackColor = LockIn.Border
                    )
                )
            }

            // ── Done Button ──────────────────────────────────────────────────
            Button(
                onClick = {
                    onStopPreview()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = LockIn.White,
                    contentColor = LockIn.Background
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
