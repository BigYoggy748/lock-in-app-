package com.lockin.dashboard.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lockin.dashboard.ui.components.ProfileDialog
import com.lockin.dashboard.ui.components.ProgressRing
import com.lockin.dashboard.ui.components.QuoteCard
import com.lockin.dashboard.ui.components.StreakCard
import com.lockin.dashboard.ui.theme.LockIn
import com.lockin.dashboard.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(vm: DashboardViewModel) {
    val tasks by vm.tasks.collectAsStateWithLifecycle()
    val streak by vm.streak.collectAsStateWithLifecycle()
    val daySummaries by vm.daySummaries.collectAsStateWithLifecycle()
    val userName by vm.userName.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }

    val completed = tasks.count { it.isCompleted }
    val total = tasks.size
    val progress = if (total == 0) 0f else completed.toFloat() / total

    val dateStr = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.ENGLISH))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LockIn.Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 48.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // ── Top Bar Header with User Profile Avatar ──────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (userName.isNotBlank() && userName != "CHAMP") "LOCK IN, ${userName.uppercase()}." else "LOCK IN.",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.TextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = dateStr,
                    fontSize = 14.sp,
                    color = LockIn.TextSecondary
                )
            }

            // Profile Avatar Button & Streak Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Streak badge pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LockIn.Card)
                        .border(1.dp, LockIn.Border, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🔥 $streak",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LockIn.TextPrimary
                    )
                }

                // Avatar
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LockIn.Card)
                        .border(1.dp, LockIn.White.copy(alpha = 0.4f), CircleShape)
                        .clickable { showProfileDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.trim().take(1).uppercase().ifEmpty { "👤" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = LockIn.White
                    )
                }
            }
        }

        // ── Circular Progress Ring ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            ProgressRing(
                progress = progress,
                completed = completed,
                total = total,
                modifier = Modifier.size(200.dp)
            )
        }

        // ── Quick Stats Row ──────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val weekTotal = daySummaries.takeLast(7).sumOf { it.completed }
            StatChip(label = "Today",      value = "$completed/$total", modifier = Modifier.weight(1f))
            StatChip(label = "Streak",     value = "${streak}d",        modifier = Modifier.weight(1f))
            StatChip(label = "This Week",  value = "$weekTotal",        modifier = Modifier.weight(1f))
        }

        // ── Daily Stoic Mindset Mantra ───────────────────────────────────────
        QuoteCard(modifier = Modifier.fillMaxWidth())

        // ── Streak Banner Card ───────────────────────────────────────────────
        StreakCard(streak = streak, modifier = Modifier.fillMaxWidth())
    }

    // Profile Setup / Edit Dialog
    if (showProfileDialog) {
        ProfileDialog(
            currentName = userName,
            onNameSaved = { vm.setUserName(it) },
            onDismiss = { showProfileDialog = false }
        )
    }
}

@Composable
private fun StatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LockIn.Card)
            .border(1.dp, LockIn.Border, RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LockIn.TextPrimary)
        Spacer(Modifier.height(2.dp))
        Text(text = label, fontSize = 11.sp, color = LockIn.TextSecondary)
    }
}
