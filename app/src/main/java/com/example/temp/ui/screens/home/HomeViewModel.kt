package com.example.temp.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.temp.data.local.TempDatabase
import com.example.temp.data.local.entity.TrackEntity
import com.example.temp.data.repository.MusicRepository
import com.example.temp.service.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val musicRepository: MusicRepository

    private val _tracks = MutableStateFlow<List<TrackEntity>>(emptyList())
    val tracks: StateFlow<List<TrackEntity>> = _tracks

    val currentTrack: StateFlow<TrackEntity?> = PlaybackService.currentTrackFlow
    val isPlaying: StateFlow<Boolean> = PlaybackService.isPlayingFlow

    init {
        val database = TempDatabase.getDatabase(application)
        musicRepository = MusicRepository(
            context = application,
            trackDao = database.trackDao(),
            favoriteDao = database.favoriteDao()
        )

        viewModelScope.launch {
            musicRepository.allTracks.collectLatest { trackList ->
                _tracks.value = trackList
            }
        }
    }

    fun scanMedia() {
        viewModelScope.launch {
            musicRepository.scanMediaLibrary()
        }
    }

    fun togglePlayPause() {
        if (isPlaying.value) {
            PlaybackService().pause()
        } else {
            PlaybackService().play()
        }
    }

    fun playTrack(track: TrackEntity) {
        PlaybackService().setTrack(track)
    }
}