package com.example.temp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class TempApplication : Application() {

    companion object {
        const val PLAYBACK_CHANNEL_ID = "temp_playback_channel"
        const val PLAYBACK_NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            PLAYBACK_CHANNEL_ID,
            "Воспроизведение музыки",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Управление воспроизведением музыки"
            setShowBadge(false)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}