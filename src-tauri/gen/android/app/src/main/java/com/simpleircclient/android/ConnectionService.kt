package com.simpleircclient.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder

/**
 * Foreground service that keeps the app process alive while the app is
 * backgrounded, so neither the WebView-hosted IRC kernel nor the Rust socket
 * (network-rs) is torn down under Doze / low-memory pressure.
 *
 * This is "approach A" from the Android plan: it keeps the process resident and
 * relies on the WebView continuing to answer server PINGs in the background. If
 * on-device testing shows Android throttles the backgrounded WebView's JS
 * timers (so PONGs stop and the server times us out), connection liveness must
 * move into the Rust transport instead — see the plan's Phase-3 escalation.
 *
 * Started/stopped from [MainActivity] for the whole app-task lifetime. A future
 * refinement is to start it only while an IRC connection is actually active,
 * which needs a JS/Rust -> native bridge.
 */
class ConnectionService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        // Re-create the service if the system kills it so the connection keeps
        // being supervised.
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        createChannel()
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
        return builder
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Keeping your IRC connection active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Connection",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Shown while the app keeps your IRC connection active"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "irc_connection"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, ConnectionService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, ConnectionService::class.java))
        }
    }
}
