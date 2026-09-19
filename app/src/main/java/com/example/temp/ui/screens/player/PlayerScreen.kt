package com.example.temp.ui.screens.player

import android.animation.ValueAnimator
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.temp.ui.theme.AccentBlue
import com.example.temp.ui.theme.AccentPurple
import com.example.temp.ui.theme.AccentTurquoise
import java.util.concurrent.TimeUnit

@Composable
fun PlayerScreen(
    trackId: Long,
    onNavigateBack: () -> Unit,
    viewModel: PlayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()

    var isRotating by remember { mutableStateOf(false) }

    LaunchedEffect(isPlaying) {
        isRotating = isPlaying
    }

    val rotation by animateFloatAsState(
        targetValue = if (isRotating) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 10000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AccentTurquoise.copy(alpha = 0.2f),
                        AccentBlue.copy(alpha = 0.1f),
                        AccentPurple.copy(alpha = 0.15f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Кнопка назад
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Сейчас играет",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /* Открыть меню */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Меню",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Обложка альбома
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .rotate(if (isRotating) rotation else 0f)
            ) {
                if (uiState.track?.coverUri != null) {
                    AsyncImage(
                        model = uiState.track?.coverUri,
                        contentDescription = "Обложка",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(60.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Название трека
            Text(
                text = uiState.track?.title ?: "Неизвестно",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Исполнитель
            Text(
                text = uiState.track?.artist ?: "Неизвестный исполнитель",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Прогресс бар
            Slider(
                value = currentPosition.toFloat(),
                onValueChange = { viewModel.seekTo(it.toLong()) },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.fillMaxWidth()
            )

            // Время
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
                Text(
                    text = formatTime(duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Перемотка назад
                IconButton(onClick = { viewModel.seekRelative(-10000) }) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "На 10 секунд назад",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Предыдущий трек
                IconButton(onClick = { viewModel.skipToPrevious() }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Предыдущий",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Play/Pause
                FilledIconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Пауза" else "Воспроизведение",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Следующий трек
                IconButton(onClick = { viewModel.skipToNext() }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Следующий",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Перемотка вперёд
                IconButton(onClick = { viewModel.seekRelative(10000) }) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "На 10 секунд вперёд",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Дополнительные кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Избранное
                IconButton(onClick = { viewModel.toggleFavorite() }) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Избранное",
                        tint = if (uiState.isFavorite) Color.Red else MaterialTheme.colorScheme.onBackground
                    )
                }

                // Повтор
                IconButton(onClick = { viewModel.cycleRepeatMode() }) {
                    Icon(
                        imageVector = when (uiState.repeatMode) {
                            com.example.temp.data.repository.RepeatMode.ONE -> Icons.Filled.RepeatOne
                            com.example.temp.data.repository.RepeatMode.ALL -> Icons.Filled.Repeat
                            else -> Icons.Outlined.Repeat
                        },
                        contentDescription = "Повтор",
                        tint = if (uiState.repeatMode != com.example.temp.data.repository.RepeatMode.OFF) {
                            AccentTurquoise
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        }
                    )
                }

                // Случайный порядок
                IconButton(onClick = { viewModel.toggleShuffle() }) {
                    Icon(
                        imageVector = if (uiState.shuffleMode) Icons.Filled.Shuffle else Icons.Outlined.Shuffle,
                        contentDescription = "Случайный порядок",
                        tint = if (uiState.shuffleMode) AccentTurquoise else MaterialTheme.colorScheme.onBackground
                    )
                }

                // Плейлист
                IconButton(onClick = { /* Добавить в плейлист */ }) {
                    Icon(
                        imageVector = Icons.Outlined.PlaylistAdd,
                        contentDescription = "Добавить в плейлист",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Эквалайзер
                IconButton(onClick = { /* Открыть эквалайзер */ }) {
                    Icon(
                        imageVector = Icons.Outlined.GraphicEq,
                        contentDescription = "Эквалайзер",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return String.format("%02d:%02d", minutes, seconds)
}