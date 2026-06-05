package com.presenceprotocol.domain.anchor

import com.presenceprotocol.domain.LedgerStats

data class CpopAnchorPayload(
    val protocol: String = "Presence Protocol",
    val token: String = "CPOP",
    val mode: String = "local-accounting-anchor",
    val privacyModel: String = "public-proof-private-encounter",
    val epoch: Int,
    val anchorHash: String,
    val rewardClass: String,
    val validityStatus: String,
    val verifiedToday: Int,
    val totalEncounters: Int,
    val timestampMs: Long
) {
    fun toJson(): String =
        "{\"protocol\":\"$protocol\"," +
            "\"token\":\"$token\"," +
            "\"mode\":\"$mode\"," +
            "\"privacyModel\":\"$privacyModel\"," +
            "\"epoch\":$epoch," +
            "\"anchorHash\":\"$anchorHash\"," +
            "\"rewardClass\":\"$rewardClass\"," +
            "\"validityStatus\":\"$validityStatus\"," +
            "\"verifiedToday\":$verifiedToday," +
            "\"totalEncounters\":$totalEncounters," +
            "\"timestampMs\":$timestampMs}"
}

object CpopAnchorPayloadExporter {
    fun fromLedgerStats(
        stats: LedgerStats,
        timestampMs: Long = System.currentTimeMillis()
    ): CpopAnchorPayload {
        val rewardClass = when {
            stats.lastReward <= 1.0 -> "base"
            stats.lastReward <= 1.25 -> "harmonic_bonus"
            else -> "damped_or_exceptional"
        }

        val validityStatus =
            if (stats.anchorHash.isNotBlank() && stats.verifiedToday > 0) {
                "valid_local_receipt_batch"
            } else {
                "pending_or_empty"
            }

        return CpopAnchorPayload(
            epoch = stats.currentEpoch,
            anchorHash = stats.anchorHash,
            rewardClass = rewardClass,
            validityStatus = validityStatus,
            verifiedToday = stats.verifiedToday,
            totalEncounters = stats.totalEncounters,
            timestampMs = timestampMs
        )
    }
}
