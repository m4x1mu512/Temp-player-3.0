package com.example.temp.ui.screens.player

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.temp.data.local.TempDatabase
import com.example.temp.data.local.entity.TrackEntity
import com.example.temp.data.repository.MusicRepository
import com.example.temp.data.repository.RepeatMode
import com.example.temp.data.repository.SettingsRepository
import com.example.temp.service.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class PlayerUiState(
    val track: TrackEntity? = null,
    val isFavorite: Boolean = false,
    val shuffleMode: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)

class PlayerViewModel(application: Application) : AndroidViewModel(application) {

    private val musicRepository: MusicRepository
    private val settingsRepository: SettingsRepository

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    val isPlaying = PlaybackService.isPlayingFlow
    val currentPosition = PlaybackService.positionFlow
    val duration = PlaybackService.durationFlow

    init {
        val database = TempDatabase.getDatabase(application)
        musicRepository = MusicRepository(
            context = application,
            trackDao = database.trackDao(),
            favoriteDao = database.favoriteDao()
        )
        settingsRepository = SettingsRepository(application)

        viewModelScope.launch {
            PlaybackService.currentTrackFlow.collectLatest { track ->
                _uiState.value = _uiState.value.copy(track = track)
                if (track != null) {
                    val isFavorite = musicRepository.isFavorite(track.id)
                    _uiState.value = _uiState.value.copy(isFavorite = isFavorite)
                }
            }
        }

        viewModelScope.launch {
            settingsRepository.uiState.collectLatest { settings ->
                _uiState.value = _uiState.value.copy(
                    shuffleMode = settings.shuffleMode,
                    repeatMode = settings.repeatMode
                )
            }
        }
    }

    fun togglePlayPause() {
        if (isPlaying.value) {
            PlaybackService().pause()
        } else {
            PlaybackService().play()
        }
    }

    fun seekTo(position: Long) {
        PlaybackService().seekTo(position)
    }

    fun seekRelative(ms: Long) {
        PlaybackService().seekRelative(ms)
    }

    fun skipToNext() {
        PlaybackService().skipToNext()
    }

    fun skipToPrevious() {
        PlaybackService().skipToPrevious()
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val track = _uiState.value.track ?: return@launch
            musicRepository.toggleFavorite(track.id)
            _uiState.value = _uiState.value.copy(
                isFavorite = !_uiState.value.isFavorite
            )
        }
    }

    fun cycleRepeatMode() {
        viewModelScope.launch {
            val newMode = when (_uiState.value.repeatMode) {
                RepeatMode.OFF -> RepeatMode.ONE
                RepeatMode.ONE -> RepeatMode.ALL
                RepeatMode.ALL -> RepeatMode.OFF
            }
            settingsRepository.setRepeatMode(newMode)
            PlaybackService().setRepeatMode(
                when (newMode) {
                    RepeatMode.OFF -> com.example.temp.service.PlaybackService.REPEAT_MODE_OFF
                    RepeatMode.ONE -> com.example.temp.service.PlaybackService.REPEAT_MODE_ONE
                    RepeatMode.ALL -> com.example.temp.service.PlaybackService.REPEAT_MODE_ALL
                }
            )
        }
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            val newShuffle = !_uiState.value.shuffleMode
            settingsRepository.setShuffleMode(newShuffle)
            PlaybackService().setShuffleModeEnabled(newShuffle)
        }
    }
}