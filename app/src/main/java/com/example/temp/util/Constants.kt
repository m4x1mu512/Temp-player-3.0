package com.example.temp.util

object Constants {

    const val APP_NAME = "Темп"
    const val PACKAGE_NAME = "com.example.temp"
    const val VERSION_NAME = "1.0.0"
    const val VERSION_CODE = 1

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "temp_playback_channel"
    const val NOTIFICATION_ID = 1

    // Database
    const val DATABASE_NAME = "temp_database"
    const val DATABASE_VERSION = 1

    // Settings
    const val DEFAULT_VISUALIZER_BARS = 64
    const val DEFAULT_VISUALIZER_SENSITIVITY = 1.0f
    const val MIN_SLEEP_TIMER_MINUTES = 15
    const val MAX_SLEEP_TIMER_MINUTES = 60

    // Audio formats supported by Android Media3
    val SUPPORTED_AUDIO_FORMATS = listOf(
        "mp3",
        "m4a",
        "aac",
        "flac",
        "ogg",
        "opus",
        "wav",
        "3gp"
    )
}