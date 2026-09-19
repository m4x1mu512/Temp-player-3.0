package com.example.temp.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.temp.data.repository.SettingsRepository
import com.example.temp.data.repository.ThemeMode
import com.example.temp.data.repository.VisualizerType

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by settingsRepository.uiState.collectAsState(initial = null)
    var showThemeDialog by remember { mutableStateOf(false) }
    var showVisualizerTypeDialog by remember { mutableStateOf(false) }
    var showEqualizerDialog by remember { mutableStateOf(false) }
    var showTimerDialog by remember { mutableStateOf(false) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Тема
            SettingsCategory(title = "Внешний вид") {
                SettingsItem(
                    icon = Icons.Default.Palette,
                    title = "Тема",
                    subtitle = when (uiState?.themeMode) {
                        ThemeMode.LIGHT -> "Светлая"
                        ThemeMode.DARK -> "Тёмная"
                        else -> "Системная"
                    },
                    onClick = { showThemeDialog = true }
                )
            }

            // Визуализация
            SettingsCategory(title = "Визуализация") {
                var isVisualizerEnabled by remember { mutableStateOf(uiState?.isVisualizerEnabled ?: false) }
                
                SettingsItem(
                    icon = Icons.Default.GraphicEq,
                    title = "Визуализация",
                    subtitle = if (isVisualizerEnabled) "Включена" else "Выключена",
                    onClick = {
                        isVisualizerEnabled = !isVisualizerEnabled
                        viewModel.setVisualizerEnabled(isVisualizerEnabled)
                    }
                )

                if (isVisualizerEnabled) {
                    SettingsItem(
                        icon = Icons.Default.Waves,
                        title = "Тип визуализации",
                        subtitle = when (uiState?.visualizerType) {
                            VisualizerType.SPECTRUM -> "Спектр"
                            VisualizerType.WAVE -> "Волна"
                            VisualizerType.CIRCLE -> "Круг"
                            else -> "Спектр"
                        },
                        onClick = { showVisualizerTypeDialog = true }
                    )

                    var sensitivity by remember { mutableStateOf(uiState?.visualizerSensitivity ?: 1.0f) }
                    
                    SettingsSliderItem(
                        icon = Icons.Default.Tune,
                        title = "Чувствительность",
                        value = sensitivity,
                        valueRange = 0.5f..2.0f,
                        onValueChange = {
                            sensitivity = it
                            viewModel.setVisualizerSensitivity(it)
                        }
                    )

                    var bars by remember { mutableStateOf(uiState?.visualizerBars ?: 64) }
                    
                    SettingsSliderItem(
                        icon = Icons.Default.ViewArray,
                        title = "Количество полос",
                        value = bars.toFloat(),
                        valueRange = 16f..128f,
                        steps = 111,
                        onValueChange = {
                            bars = it.toInt()
                            viewModel.setVisualizerBars(it.toInt())
                        }
                    )
                }
            }

            // Звук
            SettingsCategory(title = "Звук") {
                SettingsItem(
                    icon = Icons.Default.GraphicEq,
                    title = "Эквалайзер",
                    subtitle = "5-полосный эквалайзер",
                    onClick = { showEqualizerDialog = true }
                )

                SettingsItem(
                    icon = Icons.Default.Bedtime,
                    title = "Таймер сна",
                    subtitle = uiState?.sleepTimerMinutes?.let { "$it мин" } ?: "Выключен",
                    onClick = { showTimerDialog = true }
                )
            }

            // Медиатека
            SettingsCategory(title = "Медиатека") {
                SettingsItem(
                    icon = Icons.Default.Refresh,
                    title = "Сканировать медиатеку",
                    subtitle = "Обновить список треков",
                    onClick = { viewModel.scanMedia() }
                )
            }

            // О приложении
            SettingsCategory(title = "О приложении") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Версия",
                    subtitle = "1.0.0"
                )

                SettingsItem(
                    icon = Icons.Default.MusicNote,
                    title = "О приложении",
                    subtitle = "Темп — локальный музыкальный плеер",
                    onClick = { }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Информация",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Приложение воспроизводит только локальные аудиофайлы, хранящиеся на вашем устройстве. Никакие данные не отправляются на серверы.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                SettingsItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Сброс настроек",
                    subtitle = "Вернуть настройки по умолчанию",
                    onClick = { showResetConfirmDialog = true },
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Диалог выбора темы
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Выберите тему") },
            text = {
                Column {
                    listOf(
                        ThemeMode.SYSTEM to "Системная",
                        ThemeMode.LIGHT to "Светлая",
                        ThemeMode.DARK to "Тёмная"
                    ).forEach { (mode, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = uiState?.themeMode == mode,
                                onClick = {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Закрыть")
                }
            }
        )
    }

    // Диалог выбора типа визуализации
    if (showVisualizerTypeDialog) {
        AlertDialog(
            onDismissRequest = { showVisualizerTypeDialog = false },
            title = { Text("Тип визуализации") },
            text = {
                Column {
                    listOf(
                        VisualizerType.SPECTRUM to "Спектр",
                        VisualizerType.WAVE to "Волна",
                        VisualizerType.CIRCLE to "Круг"
                    ).forEach { (type, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setVisualizerType(type)
                                    showVisualizerTypeDialog = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = uiState?.visualizerType == type,
                                onClick = {
                                    viewModel.setVisualizerType(type)
                                    showVisualizerTypeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVisualizerTypeDialog = false }) {
                    Text("Закрыть")
                }
            }
        )
    }

    // Диалог подтверждения сброса
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Сброс настроек") },
            text = { Text("Вы уверены? Все настройки будут возвращены к значениям по умолчанию.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSettings()
                        showResetConfirmDialog = false
                    }
                ) {
                    Text("Сбросить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог таймера сна
    if (showTimerDialog) {
        TimerDialog(
            currentMinutes = uiState?.sleepTimerMinutes,
            onDismiss = { showTimerDialog = false },
            onSetTimer = { minutes ->
                viewModel.setSleepTimer(minutes)
                showTimerDialog = false
            }
        )
    }

    // Диалог эквалайзера
    if (showEqualizerDialog) {
        EqualizerDialog(
            bands = uiState?.equalizerBands ?: floatArrayOf(0f, 0f, 0f, 0f, 0f),
            onDismiss = { showEqualizerDialog = false },
            onSave = { bands ->
                viewModel.setEqualizerBands(bands)
                showEqualizerDialog = false
            }
        )
    }
}

@Composable
private fun SettingsCategory(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        content()
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String? = null,
    tint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }

        if (onClick != null) {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun SettingsSliderItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = String.format("%.1f", value),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}