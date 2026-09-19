package com.example.temp.ui.screens.folders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FoldersViewModel(application: Application) : AndroidViewModel(application) {

    private val _folders = MutableStateFlow<List<FolderInfo>>(emptyList())
    val folders: StateFlow<List<FolderInfo>> = _folders

    init {
        // Загрузка папок
        _folders.value = listOf(
            FolderInfo("/storage/emulated/0/Music", 0),
            FolderInfo("/storage/emulated/0/Download", 0)
        )
    }
}