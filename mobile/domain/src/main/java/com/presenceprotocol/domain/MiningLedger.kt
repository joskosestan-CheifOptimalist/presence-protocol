package com.presenceprotocol.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

data class ReceiptItem(
    val timestampMs: Long,
    val peerLabel: String,
    val reward: Double
)


fun computeAnchorHash(receipts: List<ReceiptItem>): String {
    val canonical = receipts.joinToString("\n") { "${it.timestampMs}|${it.peerLabel}|${"%.4f".format(it.reward)}" }
    val bytes = MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}

data class LedgerStats(
    val verifiedToday: Int,
    val pending: Int,
    val yieldToday: Double,
    val total: Double,
    val totalEncounters: Int,
    val encountersThisEpoch: Int,
    val currentEpoch: Int,
    val lastReward: Double,
    val tokenSymbol: String,
    val recentReceipts: List<ReceiptItem>,
    val anchorHash: String
)

interface MiningLedger {
    fun observeStats(): Flow<LedgerStats>
    fun recordEncounter(peerId: String, yieldIncrement: Double = 1.0): Boolean
    fun updateEpoch(epoch: Int) {}
}

class InMemoryMiningLedger : MiningLedger {
    private val stats = MutableStateFlow(
        LedgerStats(
            verifiedToday = 0,
            pending = 0,
            yieldToday = 0.0,
            total = 0.0,
            totalEncounters = 0,
            encountersThisEpoch = 0,
            currentEpoch = 0,
            lastReward = 0.0,
            tokenSymbol = "CPOP",
            recentReceipts = emptyList(),
            anchorHash = ""
        )
    )

    private val peerLast = mutableMapOf<String, Long>()

    companion object {
        private const val COOLDOWN = 120_000L
        private const val MAX_DAILY = 100
        private const val MAX_EPOCH = 100000
    }

    override fun observeStats(): Flow<LedgerStats> = stats.asStateFlow()

    override fun recordEncounter(peerId: String, yieldIncrement: Double): Boolean {
        val now = System.currentTimeMillis()
        val s = stats.value

        val last = peerLast[peerId]
        if (last != null && now - last < COOLDOWN) return false
        if (s.verifiedToday >= MAX_DAILY) return false
        // epoch cap temporarily relaxed

        peerLast[peerId] = now

        stats.value = s.copy(
            verifiedToday = s.verifiedToday + 1,
            pending = s.pending + 1,
            yieldToday = s.yieldToday + yieldIncrement,
            total = s.total + yieldIncrement,
            totalEncounters = s.totalEncounters + 1,
            encountersThisEpoch = s.encountersThisEpoch + 1,
            lastReward = yieldIncrement
        )
        return true
    }
}
