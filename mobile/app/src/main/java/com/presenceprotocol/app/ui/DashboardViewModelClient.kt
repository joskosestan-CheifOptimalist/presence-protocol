package com.presenceprotocol.app.ui

import com.presenceprotocol.app.PresenceApp

object DashboardViewModelClient {
    @Volatile private var instance: DashboardViewModel? = null

    fun default(): DashboardViewModel =
        instance ?: synchronized(this) { instance ?: create().also { instance = it } }

    private fun create(): DashboardViewModel {
        val app = PresenceApp.instance
        return DashboardViewModel(
            ledger              = app.ledger,
            gattServer          = app.gattServer,
            syncCoordinator     = app.syncCoordinator,
            discoveryController = app.discoveryController
        )
    }
}
