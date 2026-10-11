package com.lockin.dashboard.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.ui.theme.LockIn
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelDurationPicker(
    initialHours: Int = 0,
    initialMinutes: Int = 30,
    onDurationChanged: (hours: Int, minutes: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hoursList = remember { (0..12).toList() }
    val minutesList = remember { (0..59).toList() }

    val itemHeight = 44.dp
    val visibleItemsCount = 3

    val coroutineScope = rememberCoroutineScope()

    val hoursState = rememberLazyListState(initialFirstVisibleItemIndex = initialHours.coerceIn(0, 12))
    val minutesState = rememberLazyListState(initialFirstVisibleItemIndex = initialMinutes.coerceIn(0, 59))

    val hoursSnapBehavior = rememberSnapFlingBehavior(lazyListState = hoursState)
    val minutesSnapBehavior = rememberSnapFlingBehavior(lazyListState = minutesState)

    val selectedHours by remember {
        derivedStateOf {
            val layoutInfo = hoursState.layoutInfo
            val center = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - center)
            }?.index?.coerceIn(0, hoursList.lastIndex) ?: initialHours
        }
    }

    val selectedMinutes by remember {
        derivedStateOf {
            val layoutInfo = minutesState.layoutInfo
            val center = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.height / 2
            layoutInfo.visibleItemsInfo.minByOrNull { item ->
                abs((item.offset + item.size / 2) - center)
            }?.index?.coerceIn(0, minutesList.lastIndex) ?: initialMinutes
        }
    }

    LaunchedEffect(selectedHours, selectedMinutes) {
        onDurationChanged(hoursList[selectedHours], minutesList[selectedMinutes])
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * visibleItemsCount)
            .clip(RoundedCornerShape(16.dp))
            .background(LockIn.Card),
        contentAlignment = Alignment.Center
    ) {
        // Highlight center selection window
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(itemHeight)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.08f))
        )

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hours Wheel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    state = hoursState,
                    flingBehavior = hoursSnapBehavior,
                    contentPadding = PaddingValues(vertical = itemHeight),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(hoursList.size) { index ->
                        val isSelected = index == selectedHours
                        Box(
                            modifier = Modifier
                                .height(itemHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${hoursList[index]} hr",
                                fontSize = if (isSelected) 18.sp else 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) LockIn.TextPrimary else LockIn.TextSecondary,
                                modifier = Modifier.alpha(if (isSelected) 1f else 0.4f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Text(
                text = ":",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = LockIn.TextSecondary
            )

            // Minutes Wheel
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                LazyColumn(
                    state = minutesState,
                    flingBehavior = minutesSnapBehavior,
                    contentPadding = PaddingValues(vertical = itemHeight),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(minutesList.size) { index ->
                        val isSelected = index == selectedMinutes
                        Box(
                            modifier = Modifier
                                .height(itemHeight)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${minutesList[index]} min",
                                fontSize = if (isSelected) 18.sp else 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) LockIn.TextPrimary else LockIn.TextSecondary,
                                modifier = Modifier.alpha(if (isSelected) 1f else 0.4f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Top and Bottom gradient overlays for 3D wheel effect
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(LockIn.Card, Color.Transparent)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, LockIn.Card)
                    )
                )
        )
    }
}
