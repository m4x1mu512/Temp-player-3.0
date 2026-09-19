package com.example.temp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode {
    SYSTEM, LIGHT, DARK
}

enum class VisualizerType {
    SPECTRUM, WAVE, CIRCLE
}

data class UiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isVisualizerEnabled: Boolean = false,
    val visualizerType: VisualizerType = VisualizerType.SPECTRUM,
    val visualizerSensitivity: Float = 1.0f,
    val visualizerBars: Int = 64,
    val equalizerBands: FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f),
    val sleepTimerMinutes: Int? = null,
    val shuffleMode: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF
)

enum class RepeatMode {
    OFF, ONE, ALL
}

class SettingsRepository(private val context: Context) {

    val uiState: Flow<UiState> = context.dataStore.data.map { preferences ->
        UiState(
            themeMode = ThemeMode.valueOf(
                preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
            ),
            isVisualizerEnabled = preferences[VISUALIZER_ENABLED_KEY] ?: false,
            visualizerType = VisualizerType.valueOf(
                preferences[VISUALIZER_TYPE_KEY] ?: VisualizerType.SPECTRUM.name
            ),
            visualizerSensitivity = preferences[VISUALIZER_SENSITIVITY_KEY] ?: 1.0f,
            visualizerBars = preferences[VISUALIZER_BARS_KEY] ?: 64,
            equalizerBands = preferences[EQUALIZER_BANDS_KEY]?.let { parseFloatArray(it) } ?: floatArrayOf(0f, 0f, 0f, 0f, 0f),
            sleepTimerMinutes = preferences[SLEEP_TIMER_KEY],
            shuffleMode = preferences[SHUFFLE_MODE_KEY] ?: false,
            repeatMode = RepeatMode.valueOf(
                preferences[REPEAT_MODE_KEY] ?: RepeatMode.OFF.name
            )
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode.name
        }
    }

    suspend fun setVisualizerEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VISUALIZER_ENABLED_KEY] = enabled
        }
    }

    suspend fun setVisualizerType(type: VisualizerType) {
        context.dataStore.edit { preferences ->
            preferences[VISUALIZER_TYPE_KEY] = type.name
        }
    }

    suspend fun setVisualizerSensitivity(sensitivity: Float) {
        context.dataStore.edit { preferences ->
            preferences[VISUALIZER_SENSITIVITY_KEY] = sensitivity
        }
    }

    suspend fun setVisualizerBars(bars: Int) {
        context.dataStore.edit { preferences ->
            preferences[VISUALIZER_BARS_KEY] = bars
        }
    }

    suspend fun setEqualizerBands(bands: FloatArray) {
        context.dataStore.edit { preferences ->
            preferences[EQUALIZER_BANDS_KEY] = bands.joinToString(",")
        }
    }

    suspend fun setSleepTimer(minutes: Int?) {
        context.dataStore.edit { preferences ->
            preferences[SLEEP_TIMER_KEY] = minutes
        }
    }

    suspend fun setShuffleMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHUFFLE_MODE_KEY] = enabled
        }
    }

    suspend fun setRepeatMode(mode: RepeatMode) {
        context.dataStore.edit { preferences ->
            preferences[REPEAT_MODE_KEY] = mode.name
        }
    }

    suspend fun resetSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun parseFloatArray(string: String): FloatArray {
        return string.split(",").map { it.toFloat() }.toFloatArray()
    }

    companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val VISUALIZER_ENABLED_KEY = booleanPreferencesKey("visualizer_enabled")
        val VISUALIZER_TYPE_KEY = stringPreferencesKey("visualizer_type")
        val VISUALIZER_SENSITIVITY_KEY = floatPreferencesKey("visualizer_sensitivity")
        val VISUALIZER_BARS_KEY = intPreferencesKey("visualizer_bars")
        val EQUALIZER_BANDS_KEY = stringPreferencesKey("equalizer_bands")
        val SLEEP_TIMER_KEY = intPreferencesKey("sleep_timer")
        val SHUFFLE_MODE_KEY = booleanPreferencesKey("shuffle_mode")
        val REPEAT_MODE_KEY = stringPreferencesKey("repeat_mode")
    }
}