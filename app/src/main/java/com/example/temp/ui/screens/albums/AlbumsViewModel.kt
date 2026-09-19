package com.example.temp.ui.screens.albums

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AlbumsViewModel(application: Application) : AndroidViewModel(application) {

    private val _albums = MutableStateFlow<List<AlbumInfo>>(emptyList())
    val albums: StateFlow<List<AlbumInfo>> = _albums

    init {
        // Загрузка альбомов
    }
}