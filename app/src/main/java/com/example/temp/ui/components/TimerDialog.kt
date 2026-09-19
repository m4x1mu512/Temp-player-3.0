package com.example.temp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimerDialog(
    currentMinutes: Int?,
    onDismiss: () -> Unit,
    onSetTimer: (Int?) -> Unit
) {
    val options = listOf(
        null to "Выключить",
        15 to "15 минут",
        30 to "30 минут",
        45 to "45 минут",
        60 to "60 минут",
        -1 to "Конец трека"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Таймер сна") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { (minutes, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        RadioButton(
                            selected = currentMinutes == minutes,
                            onClick = { onSetTimer(minutes) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}