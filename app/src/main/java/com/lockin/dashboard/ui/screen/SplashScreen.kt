package com.lockin.dashboard.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.R
import com.lockin.dashboard.ui.theme.LockIn
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Pulsing logo animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoScale"
    )

    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(700),
        label = "alphaAnim"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(1800L) // 1.8 seconds splash duration
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)), // Pitch pure black
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // App Logo
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .scale(if (startAnimation) scale else 0.8f)
                    .clip(CircleShape)
                    .background(Color(0xFF0D1117)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "Lock In Logo",
                    modifier = Modifier.size(110.dp)
                )
            }

            Spacer(Modifier.height(28.dp))

            // App Name
            Text(
                text = "LOCK IN.",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = LockIn.White,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(8.dp))

            // Tagline
            Text(
                text = "DISCIPLINE • FOCUS • RESULTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = LockIn.TextSecondary.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(48.dp))

            // Minimalist Glowing Loading bar
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF161B22))
            ) {
                LinearProgressIndicator(
                    color = LockIn.FocusBlue,
                    trackColor = Color(0xFF161B22),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
