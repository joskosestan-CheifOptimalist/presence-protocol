package com.presenceprotocol.app.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log

class PresenceMiningService : Service() {

    private val viewModel: DashboardViewModel by lazy { DashboardViewModelClient.default() }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                Log.e(TAG, "PP_SERVICE START")
                ensureChannel()
                startForeground(NOTIFICATION_ID, buildNotification())
                viewModel.ensureDiscoveryStarted()
            }
            ACTION_STOP -> {
                Log.e(TAG, "PP_SERVICE STOP")
                viewModel.stopDiscovery()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "PP_SERVICE DESTROYED — stopping discovery")
        viewModel.stopDiscovery()
    }

    private fun buildNotification(): Notification {
        val tapIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return android.app.Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Presence Protocol")
            .setContentText("Mining active — scanning for peers")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setOngoing(true)
            .setContentIntent(tapIntent)
            .build()
    }

    private fun ensureChannel() {
        val mgr = getSystemService(NotificationManager::class.java) ?: return
        if (mgr.getNotificationChannel(CHANNEL_ID) != null) return
        val ch = NotificationChannel(
            CHANNEL_ID,
            "Presence Mining",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Keeps BLE mining active in the background"
            setShowBadge(false)
        }
        mgr.createNotificationChannel(ch)
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
