package com.app.mathracer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

@Composable
fun StarryBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val stars = 120
        repeat(stars) {
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = 1.5f,
                center = Offset(
                    x = (0..size.width.toInt()).random().toFloat(),
                    y = (0..size.height.toInt()).random().toFloat()
                )
            )
        }
    }
}