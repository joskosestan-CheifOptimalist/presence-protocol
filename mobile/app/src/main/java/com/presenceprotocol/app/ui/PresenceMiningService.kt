package com.presenceprotocol.app.ui

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import com.presenceprotocol.app.PresenceApp

class PresenceMiningService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                Log.e(TAG, "PP_SERVICE START")
                ensureChannel()
                startForeground(NOTIFICATION_ID, buildNotification())
                acquireWakeLock()
                PresenceApp.instance.gattServer.start()
                PresenceApp.instance.discoveryController.start()
            }
            ACTION_STOP -> {
                Log.e(TAG, "PP_SERVICE STOP")
                PresenceApp.instance.discoveryController.stop()
                PresenceApp.instance.gattServer.stop()
                releaseWakeLock()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "PP_SERVICE DESTROYED")
        PresenceApp.instance.discoveryController.stop()
        PresenceApp.instance.gattServer.stop()
        releaseWakeLock()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun acquireWakeLock() {
        val pm = getSystemService(PowerManager::class.java)
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PresenceProtocol::MiningLock")
            .also { it.acquire(10 * 60 * 1000L) }
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
    }

    private fun buildNotification(): Notification {
        val tap = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply { flags = Intent.FLAG_ACTIVITY_SINGLE_TOP },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Presence Protocol")
            .setContentText("Mining active — scanning for peers")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setContentIntent(tap)
            .build()
    }

    private fun ensureChannel() {
        val mgr = getSystemService(NotificationManager::class.java) ?: return
        if (mgr.getNotificationChannel(CHANNEL_ID) != null) return
        mgr.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Presence Mining", NotificationManager.IMPORTANCE_LOW)
                .apply { description = "Keeps BLE mining active in background"; setShowBadge(false) }
        )
    }

    companion object {
        private const val TAG = "PresenceMiningService"
        const val CHANNEL_ID = "presence_mining"
        private const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.presenceprotocol.app.ACTION_MINING_START"
        const val ACTION_STOP  = "com.presenceprotocol.app.ACTION_MINING_STOP"

        fun start(context: Context) {
            context.startForegroundService(
                Intent(context, PresenceMiningService::class.java).apply { action = ACTION_START }
            )
        }

        fun stop(context: Context) {
            context.startService(
                Intent(context, PresenceMiningService::class.java).apply { action = ACTION_STOP }
            )
        }
    }
}
