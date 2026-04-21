package com.presenceprotocol.app

import android.app.Application
import android.content.Context
import com.presenceprotocol.app.BleConfig
import com.presenceprotocol.app.BleRole
import com.presenceprotocol.data.ble.FileEncounterStore
import com.presenceprotocol.data.ble.PresenceDiscoveryController
import com.presenceprotocol.data.ble.PresenceHandshakeCoordinator
import com.presenceprotocol.data.ble.gatt.PresenceGattServer
import com.presenceprotocol.domain.InMemoryMiningLedger
import com.presenceprotocol.domain.SyncCoordinator
import com.presenceprotocol.domain.encounter.EncounterIdGenerator
import com.presenceprotocol.domain.encounter.EncounterLifecycleStateMachine
import com.presenceprotocol.domain.encounter.InMemoryEncounterCooldownPolicy
import java.time.Duration

class PresenceApp : Application() {

    // --- Singleton domain graph ---
    val ledger by lazy { PersistentMiningLedger(this) }
    val encounterStore by lazy { FileEncounterStore(this) }
    val syncCoordinator by lazy { SyncCoordinator() }

    val encounterStateMachine by lazy {
        EncounterLifecycleStateMachine(
            idGenerator = EncounterIdGenerator(),
            cooldownPolicy = InMemoryEncounterCooldownPolicy(Duration.ofMinutes(2))
        )
    }

    val handshakeCoordinator by lazy {
        PresenceHandshakeCoordinator(
            bluetoothAdapter = null,
            miningLedger = ledger,
            encounterStore = encounterStore,
            encounterStateMachine = encounterStateMachine
        )
    }

    val gattServer by lazy {
        PresenceGattServer(this, handshakeCoordinator)
    }

    val discoveryController by lazy {
        PresenceDiscoveryController(
            context = this,
            presenceGattServer = gattServer,
            miningLedger = ledger,
            encounterStore = encounterStore,
            allowInitiation = (BleConfig.BLE_ROLE == BleRole.CLIENT_ONLY || BleConfig.BLE_ROLE == BleRole.BOTH),
            providedHandshakeCoordinator = handshakeCoordinator
        )
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
        android.util.Log.d("PresenceApp", "ENCOUNTER_STORE_STARTUP_COUNT count=${encounterStore.count()}")
    }

    companion object {
        lateinit var appContext: Context
            private set

        // Convenience accessor from anywhere
        val instance: PresenceApp get() = appContext as PresenceApp
    }
}
