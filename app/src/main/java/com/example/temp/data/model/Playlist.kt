package com.example.temp.data.model

data class Playlist(
    val id: Long,
    val name: String,
    val trackCount: Int = 0,
    val coverUri: String? = null
)