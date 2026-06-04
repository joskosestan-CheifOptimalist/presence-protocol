package com.presenceprotocol.domain.tokenomics

import kotlin.math.min

data class CpopIssuanceResult(
    val reward: Double,
    val baseReward: Double,
    val frequencyMultiplier: Double,
    val dampingMultiplier: Double
)

object CpopHarmonicIssuanceEngine {
    fun calculateReward(
        encountersThisEpoch: Int,
        verifiedToday: Int
    ): CpopIssuanceResult {
        val baseReward = 1.0

        val frequencyMultiplier =
            1.0 + min(encountersThisEpoch * 0.02, 0.25)

        val dampingMultiplier =
            if (verifiedToday > 50) 0.75 else 1.0

        val reward =
            baseReward * frequencyMultiplier * dampingMultiplier

        return CpopIssuanceResult(
            reward = reward,
            baseReward = baseReward,
            frequencyMultiplier = frequencyMultiplier,
            dampingMultiplier = dampingMultiplier
        )
    }
}
