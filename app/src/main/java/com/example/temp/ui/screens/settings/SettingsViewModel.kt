package com.example.temp.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.temp.data.local.TempDatabase
import com.example.temp.data.repository.MusicRepository
import com.example.temp.data.repository.SettingsRepository
import com.example.temp.data.repository.ThemeMode
import com.example.temp.data.repository.VisualizerType
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository: SettingsRepository
    private val musicRepository: MusicRepository

    init {
        settingsRepository = SettingsRepository(application)
        val database = TempDatabase.getDatabase(application)
        musicRepository = MusicRepository(
            context = application,
            trackDao = database.trackDao(),
            favoriteDao = database.favoriteDao()
        )
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setVisualizerEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setVisualizerEnabled(enabled)
        }
    }

    fun setVisualizerType(type: VisualizerType) {
        viewModelScope.launch {
            settingsRepository.setVisualizerType(type)
        }
    }

    fun setVisualizerSensitivity(sensitivity: Float) {
        viewModelScope.launch {
            settingsRepository.setVisualizerSensitivity(sensitivity)
        }
    }

    fun setVisualizerBars(bars: Int) {
        viewModelScope.launch {
            settingsRepository.setVisualizerBars(bars)
        }
    }

    fun setSleepTimer(minutes: Int?) {
        viewModelScope.launch {
            settingsRepository.setSleepTimer(minutes)
        }
    }

    fun setEqualizerBands(bands: FloatArray) {
        viewModelScope.launch {
            settingsRepository.setEqualizerBands(bands)
        }
    }

    fun resetSettings() {
        viewModelScope.launch {
            settingsRepository.resetSettings()
        }
    }

    fun scanMedia() {
        viewModelScope.launch {
            musicRepository.scanMediaLibrary()
        }
    }
}