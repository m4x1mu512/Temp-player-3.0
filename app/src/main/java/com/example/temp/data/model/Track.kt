package com.example.temp.data.model

import android.net.Uri

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: Uri,
    val coverUri: Uri?,
    val fileSize: Long,
    val dateAdded: Long,
    val isFavorite: Boolean = false
) {
    val formattedDuration: String
        get() {
            val minutes = duration / 60000
            val seconds = (duration % 60000) / 1000
            return String.format("%02d:%02d", minutes, seconds)
        }
}