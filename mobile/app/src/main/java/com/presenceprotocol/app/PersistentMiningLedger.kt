package com.presenceprotocol.app

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.presenceprotocol.domain.LedgerStats
import com.presenceprotocol.domain.MiningLedger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.miningLedgerDataStore by preferencesDataStore(name = "mining_ledger")

class PersistentMiningLedger(
    private val context: Context
) : MiningLedger {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private object Keys {
        val VERIFIED_TODAY = intPreferencesKey("verified_today")
        val PENDING = intPreferencesKey("pending")
        val YIELD_TODAY = doublePreferencesKey("yield_today")
        val TOTAL = doublePreferencesKey("total")
        val TOTAL_ENCOUNTERS = intPreferencesKey("total_encounters")
        val ENCOUNTERS_THIS_EPOCH = intPreferencesKey("encounters_this_epoch")
        val CURRENT_EPOCH = intPreferencesKey("current_epoch")
        val LAST_REWARD = doublePreferencesKey("last_reward")
    }

    override fun observeStats(): Flow<LedgerStats> =
        context.miningLedgerDataStore.data.map { prefs ->
            LedgerStats(
                verifiedToday = prefs[Keys.VERIFIED_TODAY] ?: 0,
                pending = prefs[Keys.PENDING] ?: 0,
                yieldToday = prefs[Keys.YIELD_TODAY] ?: 0.0,
                total = prefs[Keys.TOTAL] ?: 0.0,
                totalEncounters = prefs[Keys.TOTAL_ENCOUNTERS] ?: 0,
                encountersThisEpoch = prefs[Keys.ENCOUNTERS_THIS_EPOCH] ?: 0,
                currentEpoch = prefs[Keys.CURRENT_EPOCH] ?: 0,
                lastReward = prefs[Keys.LAST_REWARD] ?: 0.0,
                tokenSymbol = "POP"
            )
        }

    override fun recordEncounter(yieldIncrement: Double) {
        scope.launch {
            context.miningLedgerDataStore.edit { prefs ->
                prefs[Keys.VERIFIED_TODAY] = (prefs[Keys.VERIFIED_TODAY] ?: 0) + 1
                prefs[Keys.PENDING] = (prefs[Keys.PENDING] ?: 0) + 1
                prefs[Keys.YIELD_TODAY] = (prefs[Keys.YIELD_TODAY] ?: 0.0) + yieldIncrement
                prefs[Keys.TOTAL] = (prefs[Keys.TOTAL] ?: 0.0) + yieldIncrement
                prefs[Keys.TOTAL_ENCOUNTERS] = (prefs[Keys.TOTAL_ENCOUNTERS] ?: 0) + 1
                prefs[Keys.ENCOUNTERS_THIS_EPOCH] = (prefs[Keys.ENCOUNTERS_THIS_EPOCH] ?: 0) + 1
                prefs[Keys.LAST_REWARD] = yieldIncrement
            }
        }
    }

    override fun updateEpoch(epoch: Int) {
        scope.launch {
            context.miningLedgerDataStore.edit { prefs ->
                val currentEpoch = prefs[Keys.CURRENT_EPOCH] ?: 0
                if (epoch != currentEpoch) {
                    prefs[Keys.CURRENT_EPOCH] = epoch
                    prefs[Keys.ENCOUNTERS_THIS_EPOCH] = 0
                }
            }
        }
    }
}
