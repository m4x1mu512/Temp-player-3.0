package com.example.temp.domain.usecase

import com.example.temp.data.repository.MusicRepository
import com.example.temp.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

class GetTracksUseCase(
    private val musicRepository: MusicRepository
) {
    operator fun invoke(): Flow<List<TrackEntity>> {
        return musicRepository.allTracks
    }
}