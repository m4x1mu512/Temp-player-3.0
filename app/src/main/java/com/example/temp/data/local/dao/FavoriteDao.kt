package com.example.temp.data.local.dao

import androidx.room.*
import com.example.temp.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    suspend fun isFavorite(trackId: Long): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE trackId = :trackId)")
    fun isFavoriteFlow(trackId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE trackId = :trackId")
    suspend fun removeFavoriteByTrackId(trackId: Long)
}