package com.example.temp.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.example.temp.data.local.entity.TrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaScanner(private val context: Context) {

    suspend fun scanAudioFiles(): Result<List<TrackEntity>> = withContext(Dispatchers.IO) {
        try {
            val tracks = mutableListOf<TrackEntity>()
            val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.DATE_ADDED
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"

            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                null
            )?.use { cursor ->
                tracks.addAll(parseCursor(cursor))
            }

            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseCursor(cursor: Cursor): List<TrackEntity> {
        val tracks = mutableListOf<TrackEntity>()
        
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
        val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
        val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val title = cursor.getStringOrNull(titleColumn) ?: "Неизвестно"
            val artist = cursor.getStringOrNull(artistColumn) ?: "Неизвестный исполнитель"
            val album = cursor.getStringOrNull(albumColumn) ?: "Неизвестный альбом"
            val duration = cursor.getLong(durationColumn)
            val dataPath = cursor.getStringOrNull(dataColumn)
            val albumId = cursor.getLong(albumIdColumn)
            val fileSize = cursor.getLong(sizeColumn)
            val dateAdded = cursor.getLong(dateAddedColumn)

            if (dataPath != null && duration > 0) {
                val uri = Uri.parse("content://media/external/audio/media/$id")
                val coverUri = if (albumId > 0) {
                    "content://media/external/audio/albumart/$albumId"
                } else {
                    null
                }

                tracks.add(
                    TrackEntity(
                        id = id,
                        title = title.ifBlank { "Неизвестно" },
                        artist = artist.ifBlank { "Неизвестный исполнитель" },
                        album = album.ifBlank { "Неизвестный альбом" },
                        duration = duration,
                        uri = uri.toString(),
                        coverUri = coverUri,
                        fileSize = fileSize,
                        dateAdded = dateAdded,
                        isFavorite = false
                    )
                )
            }
        }

        return tracks
    }

    private fun Cursor.getStringOrNull(columnIndex: Int): String? {
        return try {
            getString(columnIndex)
        } catch (e: Exception) {
            null
        }
    }
}