package com.example.temp.ui.screens.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.temp.data.local.TempDatabase
import com.example.temp.data.local.entity.TrackEntity
import com.example.temp.data.repository.MusicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val musicRepository: MusicRepository

    private val _searchResults = MutableStateFlow<List<TrackEntity>>(emptyList())
    val searchResults: StateFlow<List<TrackEntity>> = _searchResults

    init {
        val database = TempDatabase.getDatabase(application)
        musicRepository = MusicRepository(
            context = application,
            trackDao = database.trackDao(),
            favoriteDao = database.favoriteDao()
        )
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            musicRepository.searchTracks(query).collectLatest { results ->
                _searchResults.value = results
            }
        }
    }
}