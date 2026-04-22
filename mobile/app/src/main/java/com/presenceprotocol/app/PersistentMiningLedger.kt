package com.presenceprotocol.app

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.presenceprotocol.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

private val Context.ds by preferencesDataStore("ledger")

class PersistentMiningLedger(private val context: Context) : MiningLedger {

    companion object {
        private const val COOLDOWN = 120_000L
        private const val MAX_DAILY = 100
        private const val MAX_EPOCH = 100000
    }

    private object K {
        val V = intPreferencesKey("v")
        val T = doublePreferencesKey("t")
        val YIELD_TODAY = doublePreferencesKey("yield_today")
        val LAST_DAY_KEY = stringPreferencesKey("last_day_key")
        val LAST_REWARD = doublePreferencesKey("last_reward")
        val E = intPreferencesKey("e")
        val P = stringPreferencesKey("p")
        val D = stringPreferencesKey("d")
    }

    override fun observeStats(): Flow<LedgerStats> =
        context.ds.data.map { prefs ->
            val today = LocalDate.now().toString()
            val storedDay = prefs[K.LAST_DAY_KEY] ?: today
            val verifiedToday = if (storedDay == today) (prefs[K.V] ?: 0) else 0
            val yieldToday = if (storedDay == today) (prefs[K.YIELD_TODAY] ?: 0.0) else 0.0

            LedgerStats(
                verifiedToday = verifiedToday,
                pending = 0,
                yieldToday = yieldToday,
                total = prefs[K.T] ?: 0.0,
                totalEncounters = 0,
                encountersThisEpoch = prefs[K.E] ?: 0,
                currentEpoch = 0,
                lastReward = prefs[K.LAST_REWARD] ?: 0.0,
                tokenSymbol = "POP"
            )
        }

    override fun recordEncounter(peerId: String, yieldIncrement: Double): Boolean = runBlocking {
        val prefs = context.ds.data.first()
        val now = System.currentTimeMillis()
        val today = LocalDate.now().toString()
        val storedDay = prefs[K.LAST_DAY_KEY] ?: today
        val verifiedToday = if (storedDay == today) (prefs[K.V] ?: 0) else 0
        val yieldToday = if (storedDay == today) (prefs[K.YIELD_TODAY] ?: 0.0) else 0.0

        val map = (prefs[K.P] ?: "").split("\n")
            .mapNotNull {
                val p = it.split("|")
                if (p.size == 2) p[0] to p[1].toLong() else null
            }.toMap().toMutableMap()

        val last = map[peerId]
        if (last != null && now - last < COOLDOWN) return@runBlocking false
        if ((prefs[K.V] ?: 0) >= MAX_DAILY) return@runBlocking false
        // epoch cap temporarily relaxed

        map[peerId] = now

        context.ds.edit {
            it[K.V] = verifiedToday + 1
            it[K.T] = (prefs[K.T] ?: 0.0) + yieldIncrement
            it[K.YIELD_TODAY] = yieldToday + yieldIncrement
            it[K.LAST_DAY_KEY] = today
            it[K.E] = (prefs[K.E] ?: 0) + 1
            it[K.D] = today
            it[K.P] = map.entries.joinToString("\n") { e -> "${e.key}|${e.value}" }
            it[K.LAST_REWARD] = yieldIncrement
        }
        true
    }
}
