package com.lockin.dashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import com.lockin.dashboard.ui.screen.MainScreen
import com.lockin.dashboard.ui.screen.SplashScreen
import com.lockin.dashboard.ui.theme.LockInTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LockInTheme {
                var isSplashFinished by remember { mutableStateOf(false) }

                Crossfade(
                    targetState = isSplashFinished,
                    animationSpec = tween(500),
                    label = "splashCrossfade"
                ) { showMain ->
                    if (showMain) {
                        MainScreen()
                    } else {
                        SplashScreen(onSplashComplete = { isSplashFinished = true })
                    }
                }
            }
        }
    }
}

