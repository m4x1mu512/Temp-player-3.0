package com.example.temp.ui.screens.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.temp.data.local.TempDatabase
import com.example.temp.data.local.entity.TrackEntity
import com.example.temp.data.repository.MusicRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val musicRepository: MusicRepository

    val tracks: StateFlow<List<TrackEntity>> = musicRepository.allTracks

    init {
        val database = TempDatabase.getDatabase(application)
        musicRepository = MusicRepository(
            context = application,
            trackDao = database.trackDao(),
            favoriteDao = database.favoriteDao()
        )

        viewModelScope.launch {
            musicRepository.allTracks.collectLatest { }
        }
    }
}