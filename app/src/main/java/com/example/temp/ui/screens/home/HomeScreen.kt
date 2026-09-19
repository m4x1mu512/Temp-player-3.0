package com.example.temp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.temp.data.local.entity.TrackEntity
import com.example.temp.ui.components.MiniPlayer
import com.example.temp.ui.theme.AccentBlue
import com.example.temp.ui.theme.AccentPurple
import com.example.temp.ui.theme.AccentTurquoise
import com.example.temp.ui.theme.LightGreenBackground

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onNavigateToPlayer: (Long) -> Unit,
    onNavigateToLibrary: () -> Unit,
    onNavigateToPlaylists: () -> Unit,
    onNavigateToFolders: () -> Unit,
    onNavigateToAlbums: () -> Unit,
    onNavigateToArtists: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState(pageCount = { 6 })

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
    }

    val tracks by viewModel.tracks.collectAsState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Заголовок
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                AccentTurquoise.copy(alpha = 0.3f),
                                AccentBlue.copy(alpha = 0.3f),
                                AccentPurple.copy(alpha = 0.3f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Темп",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Навигационные значки
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavIcon(
                    icon = if (selectedTab == 0) Icons.Filled.List else Icons.Outlined.List,
                    label = "Список",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavIcon(
                    icon = if (selectedTab == 1) Icons.Filled.Folder else Icons.Outlined.Folder,
                    label = "Папки",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavIcon(
                    icon = if (selectedTab == 2) Icons.Filled.PlaylistPlay else Icons.Outlined.PlaylistPlay,
                    label = "Плейлисты",
                    isSelected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavIcon(
                    icon = if (selectedTab == 3) Icons.Filled.Album else Icons.Outlined.Album,
                    label = "Альбомы",
                    isSelected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavIcon(
                    icon = if (selectedTab == 4) Icons.Filled.Person else Icons.Outlined.Person,
                    label = "Исполнители",
                    isSelected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
                NavIcon(
                    icon = if (selectedTab == 5) Icons.Filled.Search else Icons.Outlined.Search,
                    label = "Поиск",
                    isSelected = selectedTab == 5,
                    onClick = { selectedTab = 5 }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { onNavigateToSettings() }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Меню",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Pager с экранами
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> TrackListScreen(tracks, onNavigateToPlayer)
                    1 -> FoldersPlaceholderScreen(onNavigateToFolders)
                    2 -> PlaylistsPlaceholderScreen(onNavigateToPlaylists)
                    3 -> AlbumsPlaceholderScreen(onNavigateToAlbums)
                    4 -> ArtistsPlaceholderScreen(onNavigateToArtists)
                    5 -> SearchPlaceholderScreen(onNavigateToSearch)
                }
            }

            // Мини-плеер
            if (currentTrack != null) {
                MiniPlayer(
                    track = currentTrack!!,
                    isPlaying = isPlaying,
                    onPlayPause = { viewModel.togglePlayPause() },
                    onTrackClick = { onNavigateToPlayer(currentTrack!!.id) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun NavIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AccentTurquoise else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) AccentTurquoise else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TrackListScreen(
    tracks: List<TrackEntity>,
    onNavigateToPlayer: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Все треки",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(tracks) { track ->
            TrackItem(track = track, onClick = { onNavigateToPlayer(track.id) })
        }
        if (tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
    text = "Музыка не найдена." + "
" +
        "Проверьте разрешения и нажмите «Сканировать» в меню.",
    style = MaterialTheme.typography.bodyMedium,
    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
)
                }
            }
        }
    }
}

@Composable
private fun TrackItem(
    track: TrackEntity,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Обложка
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (track.coverUri != null) {
                AsyncImage(
                    model = track.coverUri,
                    contentDescription = "Обложка",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Информация о треке
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = track.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Длительность
        Text(
            text = formatDuration(track.duration),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

private fun formatDuration(durationMs: Long): String {
    val minutes = durationMs / 60000
    val seconds = (durationMs % 60000) / 1000
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
private fun FoldersPlaceholderScreen(onNavigateToFolders: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Folder,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
            Text(
                text = "Папки",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            TextButton(onClick = onNavigateToFolders) {
                Text("Открыть")
            }
        }
    }
}

@Composable
private fun PlaylistsPlaceholderScreen(onNavigateToPlaylists: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.PlaylistPlay,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
            Text(
                text = "Плейлисты",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            TextButton(onClick = onNavigateToPlaylists) {
                Text("Открыть")
            }
        }
    }
}

@Composable
private fun AlbumsPlaceholderScreen(onNavigateToAlbums: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Album,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
            Text(
                text = "Альбомы",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            TextButton(onClick = onNavigateToAlbums) {
                Text("Открыть")
            }
        }
    }
}

@Composable
private fun ArtistsPlaceholderScreen(onNavigateToArtists: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
            Text(
                text = "Исполнители",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            TextButton(onClick = onNavigateToArtists) {
                Text("Открыть")
            }
        }
    }
}

@Composable
private fun SearchPlaceholderScreen(onNavigateToSearch: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
            Text(
                text = "Поиск",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            TextButton(onClick = onNavigateToSearch) {
                Text("Открыть")
            }
        }
    }
}