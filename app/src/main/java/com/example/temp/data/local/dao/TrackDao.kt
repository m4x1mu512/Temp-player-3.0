package com.example.temp.data.local.dao

import androidx.room.*
import com.example.temp.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks ORDER BY title ASC")
    fun getAllTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: Long): TrackEntity?

    @Query("SELECT * FROM tracks WHERE artist = :artist ORDER BY title ASC")
    fun getTracksByArtist(artist: String): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE album = :album ORDER BY title ASC")
    fun getTracksByAlbum(album: String): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE isFavorite = 1 ORDER BY title ASC")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE title LIKE :query OR artist LIKE :query OR album LIKE :query ORDER BY title ASC")
    fun searchTracks(query: String): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Delete
    suspend fun deleteTrack(track: TrackEntity)

    @Query("DELETE FROM tracks")
    suspend fun deleteAllTracks()

    @Query("UPDATE tracks SET isFavorite = :isFavorite WHERE id = :trackId")
    suspend fun updateFavoriteStatus(trackId: Long, isFavorite: Boolean)

    @Query("SELECT * FROM tracks ORDER BY dateAdded DESC LIMIT :limit")
    fun getRecentlyAddedTracks(limit: Int = 20): Flow<List<TrackEntity>>
}