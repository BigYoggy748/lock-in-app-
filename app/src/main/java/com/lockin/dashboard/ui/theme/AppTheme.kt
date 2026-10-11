package com.lockin.dashboard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// ── Colour Tokens ─────────────────────────────────────────────────────────────
object LockIn {
    val Background     = Color(0xFF0A0A0A)
    val Surface        = Color(0xFF141414)
    val Card           = Color(0xFF1C1C1E)
    val White          = Color(0xFFFFFFFF)
    val TextPrimary    = Color(0xFFFFFFFF)
    val TextSecondary  = Color(0xFF8E8E93)
    val Border         = Color(0xFF2C2C2E)
    val Separator      = Color(0xFF1C1C1E)
    val PriorityHigh   = Color(0xFFFF453A)
    val PriorityMed    = Color(0xFFFF9F0A)
    val PriorityLow    = Color(0xFF30D158)
    val FocusBlue      = Color(0xFF0A84FF)
    val DotActive      = Color(0xFFFFFFFF)
    val DotInactive    = Color(0xFF2C2C2E)
    val Tint           = Color(0xFF48484A)
}

private val DarkColorScheme = darkColorScheme(
    background        = LockIn.Background,
    surface           = LockIn.Surface,
    primary           = LockIn.White,
    onPrimary         = LockIn.Background,
    onBackground      = LockIn.TextPrimary,
    onSurface         = LockIn.TextPrimary,
    surfaceVariant    = LockIn.Card,
    onSurfaceVariant  = LockIn.TextSecondary,
    outline           = LockIn.Border
)

@Composable
fun LockInTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
