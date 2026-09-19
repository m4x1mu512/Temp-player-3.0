package com.example.temp.ui.screens.visualizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.temp.ui.theme.AccentBlue
import com.example.temp.ui.theme.AccentPurple
import com.example.temp.ui.theme.AccentTurquoise

@Composable
fun VisualizerScreen(
    isPlaying: Boolean,
    visualizerType: String = "spectrum",
    sensitivity: Float = 1.0f,
    bars: Int = 64
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (visualizerType) {
            "spectrum" -> SpectrumVisualizer(
                isPlaying = isPlaying,
                sensitivity = sensitivity,
                bars = bars
            )
            "wave" -> WaveVisualizer(
                isPlaying = isPlaying,
                sensitivity = sensitivity
            )
            "circle" -> CircleVisualizer(
                isPlaying = isPlaying,
                sensitivity = sensitivity,
                bars = bars
            )
        }
    }
}

@Composable
private fun SpectrumVisualizer(
    isPlaying: Boolean,
    sensitivity: Float,
    bars: Int
) {
    val barsData = remember { mutableStateListOf<Float>().apply {
        repeat(bars) { add(0.5f) }
    } }

    // Анимация для имитации визуализации
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                kotlinx.coroutines.delay(100)
                barsData.forEachIndexed { index, _ ->
                    barsData[index] = (0.1f..0.9f).random() * sensitivity
                }
            }
        } else {
            barsData.forEachIndexed { index, _ ->
                barsData[index] = 0.1f
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        barsData.forEach { amplitude ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(amplitude)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AccentTurquoise,
                                AccentBlue,
                                AccentPurple
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun WaveVisualizer(
    isPlaying: Boolean,
    sensitivity: Float
) {
    var phase by remember { mutableStateOf(0f) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                kotlinx.coroutines.delay(50)
                phase += 0.1f * sensitivity
            }
        }
    }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2

        val path = androidx.compose.ui.graphics.Path()
        path.moveTo(0f, centerY)

        for (x in 0..width.toInt() step 4) {
            val y = centerY + kotlin.math.sin((x / 20.0) + phase).toFloat() * 50 * sensitivity
            path.lineTo(x.toFloat(), y)
        }

        drawPath(
            path = path,
            color = AccentTurquoise.copy(alpha = 0.7f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
        )
    }
}

@Composable
private fun CircleVisualizer(
    isPlaying: Boolean,
    sensitivity: Float,
    bars: Int
) {
    val barsData = remember { mutableStateListOf<Float>().apply {
        repeat(bars) { add(0.5f) }
    } }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                kotlinx.coroutines.delay(100)
                barsData.forEachIndexed { index, _ ->
                    barsData[index] = (0.3f..0.8f).random() * sensitivity
                }
            }
        }
    }

    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 4

        barsData.forEachIndexed { index, amplitude ->
            val angle = (index / barsData.size.toFloat()) * 360f
            val radian = Math.toRadians(angle.toDouble())

            val x1 = centerX + kotlin.math.cos(radian).toFloat() * radius
            val y1 = centerY + kotlin.math.sin(radian).toFloat() * radius

            val x2 = centerX + kotlin.math.cos(radian).toFloat() * (radius + amplitude * radius)
            val y2 = centerY + kotlin.math.sin(radian).toFloat() * (radius + amplitude * radius)

            drawLine(
                color = when (index % 3) {
                    0 -> AccentTurquoise
                    1 -> AccentBlue
                    else -> AccentPurple
                },
                start = androidx.compose.ui.geometry.Offset(x1, y1),
                end = androidx.compose.ui.geometry.Offset(x2, y2),
                strokeWidth = 4f
            )
        }
    }
}