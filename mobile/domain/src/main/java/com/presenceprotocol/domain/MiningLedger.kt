package com.presenceprotocol.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LedgerStats(
    val verifiedToday: Int,
    val pending: Int,
    val yieldToday: Double,
    val total: Double,
    val totalEncounters: Int,
    val encountersThisEpoch: Int,
    val currentEpoch: Int,
    val lastReward: Double,
    val tokenSymbol: String
)

interface MiningLedger {
    fun observeStats(): Flow<LedgerStats>
    fun recordEncounter(yieldIncrement: Double = 1.0)
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
            tokenSymbol = "POP"
        )
    )

    override fun observeStats(): Flow<LedgerStats> = stats.asStateFlow()

    override fun recordEncounter(yieldIncrement: Double) {
        val current = stats.value
        stats.value = current.copy(
            verifiedToday = current.verifiedToday + 1,
            pending = current.pending + 1,
            yieldToday = current.yieldToday + yieldIncrement,
            total = current.total + yieldIncrement,
            totalEncounters = current.totalEncounters + 1,
            encountersThisEpoch = current.encountersThisEpoch + 1,
            lastReward = yieldIncrement
        )
    }

    override fun updateEpoch(epoch: Int) {
        val current = stats.value
        if (epoch != current.currentEpoch) {
            stats.value = current.copy(
                currentEpoch = epoch,
                encountersThisEpoch = 0
            )
        }
    }
}

class StubMiningLedger : MiningLedger {
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
            tokenSymbol = "POP"
        )
    )

    override fun observeStats(): Flow<LedgerStats> = stats.asStateFlow()
    override fun recordEncounter(yieldIncrement: Double) = Unit
}
