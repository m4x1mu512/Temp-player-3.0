package com.example.temp.data.repository

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.example.temp.data.local.dao.FavoriteDao
import com.example.temp.data.local.dao.TrackDao
import com.example.temp.data.local.entity.FavoriteEntity
import com.example.temp.data.local.entity.TrackEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MusicRepository(
    private val context: Context,
    private val trackDao: TrackDao,
    private val favoriteDao: FavoriteDao
) {

    val allTracks: Flow<List<TrackEntity>> = trackDao.getAllTracks()
    val favoriteTracks: Flow<List<TrackEntity>> = trackDao.getFavoriteTracks()

    fun getTracksByArtist(artist: String): Flow<List<TrackEntity>> {
        return trackDao.getTracksByArtist(artist)
    }

    fun getTracksByAlbum(album: String): Flow<List<TrackEntity>> {
        return trackDao.getTracksByAlbum(album)
    }

    fun searchTracks(query: String): Flow<List<TrackEntity>> {
        return trackDao.searchTracks("%$query%")
    }

    suspend fun scanMediaLibrary(): Result<List<TrackEntity>> = withContext(Dispatchers.IO) {
        try {
            val tracks = mutableListOf<TrackEntity>()
            val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

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
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val coverUri = getAlbumArtUri(albumId.toString())

                        val track = TrackEntity(
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
                        tracks.add(track)
                    }
                }
            }

            trackDao.insertTracks(tracks)
            Result.success(tracks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getAlbumArtUri(albumId: String): String? {
        return Uri.parse("content://media/external/audio/albumart/$albumId").toString()
    }

    private fun Cursor.getStringOrNull(columnIndex: Int): String? {
        return try {
            getString(columnIndex)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun toggleFavorite(trackId: Long) {
        val isFavorite = favoriteDao.isFavorite(trackId)
        if (isFavorite) {
            favoriteDao.removeFavoriteByTrackId(trackId)
            trackDao.updateFavoriteStatus(trackId, false)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(trackId = trackId))
            trackDao.updateFavoriteStatus(trackId, true)
        }
    }

    suspend fun isFavorite(trackId: Long): Boolean {
        return favoriteDao.isFavorite(trackId)
    }

    fun isFavoriteFlow(trackId: Long): Flow<Boolean> {
        return favoriteDao.isFavoriteFlow(trackId)
    }
}