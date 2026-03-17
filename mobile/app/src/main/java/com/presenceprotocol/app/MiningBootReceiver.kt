package com.presenceprotocol.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.presenceprotocol.app.ui.PresenceMiningService

class MiningBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.e("MiningBootReceiver", "BOOT_COMPLETED — restarting mining service")
            PresenceMiningService.start(context)
        }
    }
}
