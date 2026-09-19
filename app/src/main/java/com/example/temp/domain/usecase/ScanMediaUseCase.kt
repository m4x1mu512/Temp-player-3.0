package com.example.temp.domain.usecase

import com.example.temp.data.repository.MusicRepository
import com.example.temp.data.local.entity.TrackEntity

class ScanMediaUseCase(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(): Result<List<TrackEntity>> {
        return musicRepository.scanMediaLibrary()
    }
}