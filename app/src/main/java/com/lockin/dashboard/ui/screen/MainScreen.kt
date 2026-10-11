package com.lockin.dashboard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lockin.dashboard.ui.components.FloatingTimerPill
import com.lockin.dashboard.ui.theme.LockIn
import com.lockin.dashboard.viewmodel.DashboardViewModel
import com.lockin.dashboard.viewmodel.FocusViewModel
import com.lockin.dashboard.viewmodel.NotesViewModel
import com.lockin.dashboard.viewmodel.TimerState

private enum class Tab(val label: String, val icon: ImageVector) {
    HOME("Home",     Icons.Default.Home),
    TASKS("Tasks",   Icons.Default.CheckCircleOutline),
    CALENDAR("Calendar", Icons.Default.CalendarMonth),
    FOCUS("Focus",   Icons.Default.Timer),
    NOTES("Notes",   Icons.Default.EditNote)
}

@Composable
fun MainScreen(
    dashVm: DashboardViewModel = viewModel(),
    focusVm: FocusViewModel = viewModel(),
    notesVm: NotesViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(Tab.HOME) }

    val timerState by focusVm.timerState.collectAsStateWithLifecycle()
    val remainingSeconds by focusVm.remainingSeconds.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = LockIn.Background,
        bottomBar = {
            NavigationBar(
                containerColor = LockIn.Surface,
                tonalElevation = 0.dp
            ) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(tab.icon, contentDescription = tab.label)
                        },
                        label = {
                            Text(tab.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LockIn.White,
                            selectedTextColor = LockIn.White,
                            unselectedIconColor = LockIn.TextSecondary,
                            unselectedTextColor = LockIn.TextSecondary,
                            indicatorColor = LockIn.Card
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Main tab screen content
            when (selectedTab) {
                Tab.HOME     -> HomeScreen(vm = dashVm)
                Tab.TASKS    -> TasksScreen(vm = dashVm)
                Tab.CALENDAR -> CalendarScreen(vm = dashVm)
                Tab.FOCUS    -> FocusScreen(vm = focusVm)
                Tab.NOTES    -> NotesScreen(vm = notesVm)
            }

            // Global Floating Dynamic Island Timer Pill (visible when browsing non-focus tabs while timer runs)
            if (selectedTab != Tab.FOCUS && (timerState == TimerState.RUNNING || timerState == TimerState.PAUSED)) {
                FloatingTimerPill(
                    remainingSeconds = remainingSeconds,
                    timerState = timerState,
                    onPillClicked = { selectedTab = Tab.FOCUS },
                    onTogglePlayPause = { focusVm.togglePlayPause() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }
    }
}
