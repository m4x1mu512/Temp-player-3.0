package com.example.temp.ui.screens.playlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.temp.data.local.TempDatabase
import com.example.temp.data.model.Playlist
import com.example.temp.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistsViewModel(application: Application) : AndroidViewModel(application) {

    private val playlistRepository: PlaylistRepository

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    val totalTracksCount: Int = 0
    val favoriteTracksCount: Int = 0

    init {
        val database = TempDatabase.getDatabase(application)
        playlistRepository = PlaylistRepository(database.playlistDao())

        viewModelScope.launch {
            playlistRepository.allPlaylists.collectLatest { entities ->
                _playlists.value = entities.map { entity ->
                    Playlist(
                        id = entity.id,
                        name = entity.name,
                        trackCount = 0,
                        coverUri = entity.coverUri
                    )
                }
            }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            // Удаление плейлиста
        }
    }
}