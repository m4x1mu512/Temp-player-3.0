package com.example.temp.ui.screens.artists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ArtistsViewModel(application: Application) : AndroidViewModel(application) {

    private val _artists = MutableStateFlow<List<ArtistInfo>>(emptyList())
    val artists: StateFlow<List<ArtistInfo>> = _artists

    init {
        // Загрузка исполнителей
    }
}