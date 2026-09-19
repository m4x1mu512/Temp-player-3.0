package com.example.temp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EqualizerDialog(
    bands: FloatArray,
    onDismiss: () -> Unit,
    onSave: (FloatArray) -> Unit
) {
    val bandLabels = listOf("60 Гц", "230 Гц", "910 Гц", "3.6 кГц", "14 кГц")
    val presets = listOf(
        "Обычный" to floatArrayOf(0f, 0f, 0f, 0f, 0f),
        "Рок" to floatArrayOf(5f, 3f, -1f, 2f, 4f),
        "Поп" to floatArrayOf(-1f, 2f, 4f, 4f, 2f),
        "Джаз" to floatArrayOf(3f, 2f, 1f, 2f, 3f),
        "Классика" to floatArrayOf(4f, 3f, 2f, 1f, 0f),
        "Электронная" to floatArrayOf(5f, 4f, 2f, 0f, 3f)
    )

    val sliderValues = remember { mutableStateListOf<Float>().apply { addAll(bands.toList()) } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Эквалайзер") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Пресеты
                Text(
                    text = "Пресеты",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { (name, values) ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                sliderValues.clear()
                                sliderValues.addAll(values.toList())
                            },
                            label = { Text(name) }
                        )
                    }
                }

                // Полосы эквалайзера
                Text(
                    text = "Ручная настройка",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    sliderValues.forEachIndexed { index, value ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = String.format("%.0f", value),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Slider(
                                value = value,
                                onValueChange = { sliderValues[index] = it },
                                valueRange = -10f..10f,
                                steps = 19,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(120.dp)
                            )

                            Text(
                                text = bandLabels[index],
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(sliderValues.toFloatArray())
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}