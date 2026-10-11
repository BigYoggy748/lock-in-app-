package com.lockin.dashboard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.ui.theme.LockIn

@Composable
fun ProgressRing(
    progress: Float,
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 14.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "ring"
    )
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sw = strokeWidth.toPx()
            val diameter = size.minDimension - sw
            val topLeft = Offset(
                x = (size.width - diameter) / 2,
                y = (size.height - diameter) / 2
            )
            val arcSize = Size(diameter, diameter)
            // Track ring
            drawArc(
                color = LockIn.Border,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(sw, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = arcSize
            )
            // Progress ring
            if (animatedProgress > 0f) {
                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    style = Stroke(sw, cap = StrokeCap.Round),
                    topLeft = topLeft,
                    size = arcSize
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progress * 100).toInt()}%",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = LockIn.TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "$completed of $total tasks",
                fontSize = 13.sp,
                color = LockIn.TextSecondary
            )
        }
    }
}
