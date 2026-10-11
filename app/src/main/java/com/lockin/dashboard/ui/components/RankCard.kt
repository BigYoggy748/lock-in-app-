package com.lockin.dashboard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lockin.dashboard.data.model.UserRank
import com.lockin.dashboard.ui.theme.LockIn

@Composable
fun RankCard(
    userRank: UserRank,
    modifier: Modifier = Modifier
) {
    val tierColor = Color(userRank.tier.colorHex)
    val animatedProgress by animateFloatAsState(
        targetValue = userRank.progressToNextTier,
        animationSpec = tween(800),
        label = "xp_progress"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(LockIn.Card)
            .border(1.dp, LockIn.Border, RoundedCornerShape(16.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = userRank.tier.badge,
                    fontSize = 22.sp
                )
                Column {
                    Text(
                        text = userRank.tier.title.uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = LockIn.TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "RANK TIER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = tierColor,
                        letterSpacing = 1.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${userRank.currentXp} XP",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.White
                )
                Text(
                    text = if (userRank.xpNeededForNextTier > 0) "${userRank.xpNeededForNextTier} XP to next rank" else "MAX TIER ACHIEVED",
                    fontSize = 11.sp,
                    color = LockIn.TextSecondary
                )
            }
        }

        // Sleek Animated XP Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(LockIn.Border)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(tierColor.copy(alpha = 0.7f), tierColor)
                        )
                    )
            )
        }
    }
}
