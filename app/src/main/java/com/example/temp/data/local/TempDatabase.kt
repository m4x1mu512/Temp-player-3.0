package com.example.temp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.temp.data.local.dao.FavoriteDao
import com.example.temp.data.local.dao.PlaylistDao
import com.example.temp.data.local.dao.TrackDao
import com.example.temp.data.local.entity.FavoriteEntity
import com.example.temp.data.local.entity.PlaylistEntity
import com.example.temp.data.local.entity.PlaylistTrackCrossRef
import com.example.temp.data.local.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class TempDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: TempDatabase? = null

        fun getDatabase(context: Context): TempDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TempDatabase::class.java,
                    "temp_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}