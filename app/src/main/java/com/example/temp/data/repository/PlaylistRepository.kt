package com.example.temp.data.repository

import com.example.temp.data.local.dao.PlaylistDao
import com.example.temp.data.local.entity.PlaylistEntity
import com.example.temp.data.local.entity.PlaylistTrackCrossRef
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(
    private val playlistDao: PlaylistDao
) {

    val allPlaylists: Flow<List<PlaylistEntity>> = playlistDao.getAllPlaylists()

    fun getTracksInPlaylist(playlistId: Long): Flow<List<com.example.temp.data.local.entity.TrackEntity>> {
        return playlistDao.getTracksInPlaylist(playlistId)
    }

    suspend fun createPlaylist(name: String, coverUri: String? = null): Long {
        return playlistDao.insertPlaylist(
            PlaylistEntity(name = name, coverUri = coverUri)
        )
    }

    suspend fun deletePlaylist(playlist: PlaylistEntity) {
        playlistDao.deletePlaylist(playlist)
    }

    suspend fun renamePlaylist(playlist: PlaylistEntity, newName: String) {
        playlistDao.updatePlaylist(playlist.copy(name = newName))
    }

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long, position: Int = 0) {
        playlistDao.addTrackToPlaylist(
            PlaylistTrackCrossRef(playlistId = playlistId, trackId = trackId, position = position)
        )
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
    }

    suspend fun clearPlaylist(playlistId: Long) {
        playlistDao.clearPlaylist(playlistId)
    }
}