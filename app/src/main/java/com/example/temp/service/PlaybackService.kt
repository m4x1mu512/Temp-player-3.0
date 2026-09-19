package com.example.temp.service

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.temp.TempApplication
import com.example.temp.data.local.entity.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaybackService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        val currentTrackFlow: MutableStateFlow<TrackEntity?> = MutableStateFlow(null)
        val isPlayingFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val positionFlow: MutableStateFlow<Long> = MutableStateFlow(0L)
        val durationFlow: MutableStateFlow<Long> = MutableStateFlow(0L)
    }

    override fun onCreate() {
        super.onCreate()
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        mediaSession = MediaSession.Builder(this, player).build()
        
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isPlayingFlow.value = player.isPlaying
                
                if (playbackState == Player.STATE_ENDED) {
                    // Трек завершился
                }
            }

            override fun onMediaMetadataChanged(metadata: MediaMetadata) {
                // Обновление метаданных
            }
        })

        // Обновление позиции воспроизведения
        serviceScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                positionFlow.value = player.currentPosition
                durationFlow.value = player.duration
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Сервис продолжит работу в фоне
        super.onTaskRemoved(rootIntent)
    }

    fun setTrack(track: TrackEntity) {
        val player = mediaSession?.player ?: return
        
        currentTrackFlow.value = track
        
        val mediaItem = MediaItem.Builder()
            .setUri(track.uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artist)
                    .setAlbumTitle(track.album)
                    .setArtworkUri(android.net.Uri.parse(track.coverUri))
                    .build()
            )
            .build()

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun play() {
        mediaSession?.player?.play()
    }

    fun pause() {
        mediaSession?.player?.pause()
    }

    fun seekTo(position: Long) {
        mediaSession?.player?.seekTo(position)
    }

    fun seekRelative(ms: Long) {
        val player = mediaSession?.player ?: return
        val newPosition = (player.currentPosition + ms).coerceIn(0L, player.duration)
        player.seekTo(newPosition)
    }

    fun skipToNext() {
        mediaSession?.player?.seekToNext()
    }

    fun skipToPrevious() {
        mediaSession?.player?.seekToPrevious()
    }

    fun setRepeatMode(mode: Int) {
        mediaSession?.player?.repeatMode = mode
    }

    fun setShuffleModeEnabled(enabled: Boolean) {
        mediaSession?.player?.shuffleModeEnabled = enabled
    }
}