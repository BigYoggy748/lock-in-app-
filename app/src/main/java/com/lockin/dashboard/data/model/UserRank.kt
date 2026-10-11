package com.lockin.dashboard.data.model

enum class RankTier(
    val title: String,
    val badge: String,
    val minXp: Int,
    val maxXp: Int,
    val colorHex: Long
) {
    INITIATE("Initiate", "🥉", 0, 500, 0xFF8E8E93),
    OPERATOR("Operator", "🥈", 500, 1500, 0xFF0A84FF),
    DEEP_WORKER("Deep Worker", "🥇", 1500, 4000, 0xFFFF9F0A),
    FLOW_MASTER("Flow Master", "⚡", 4000, 10000, 0xFF30D158),
    TITAN("Locked-In Titan", "👑", 10000, Int.MAX_VALUE, 0xFFFFD700)
}

data class UserRank(
    val currentXp: Int,
    val tier: RankTier,
    val progressToNextTier: Float,
    val xpInCurrentTier: Int,
    val xpNeededForNextTier: Int
) {
    companion object {
        fun fromXp(xp: Int): UserRank {
            val validXp = xp.coerceAtLeast(0)
            val tier = when {
                validXp >= RankTier.TITAN.minXp -> RankTier.TITAN
                validXp >= RankTier.FLOW_MASTER.minXp -> RankTier.FLOW_MASTER
                validXp >= RankTier.DEEP_WORKER.minXp -> RankTier.DEEP_WORKER
                validXp >= RankTier.OPERATOR.minXp -> RankTier.OPERATOR
                else -> RankTier.INITIATE
            }

            if (tier == RankTier.TITAN) {
                return UserRank(
                    currentXp = validXp,
                    tier = tier,
                    progressToNextTier = 1.0f,
                    xpInCurrentTier = validXp - tier.minXp,
                    xpNeededForNextTier = 0
                )
            }

            val range = tier.maxXp - tier.minXp
            val progressInTier = validXp - tier.minXp
            val progress = (progressInTier.toFloat() / range).coerceIn(0f, 1f)
            val remaining = (tier.maxXp - validXp).coerceAtLeast(0)

            return UserRank(
                currentXp = validXp,
                tier = tier,
                progressToNextTier = progress,
                xpInCurrentTier = progressInTier,
                xpNeededForNextTier = remaining
            )
        }
    }
}
