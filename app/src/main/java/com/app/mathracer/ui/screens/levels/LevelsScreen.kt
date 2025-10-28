package com.app.mathracer.ui.screens.levels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.app.mathracer.ui.screens.worlds.StarryBackground
import com.app.mathracer.ui.screens.worlds.TopBar

@Composable
fun LevelsScreen(
    worldName: String,
    worldDescription: String,
    levels: List<Level>,
    onLevelClick: (Level) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B032D))
    ) {
        StarryBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopBar(energy = 10)

            Text(
                text = worldName,
                color = Color.Cyan,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            LevelGrid(levels = levels, onLevelClick = onLevelClick)

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .background(Color(0xFF0E043A), RoundedCornerShape(12.dp))
                    .border(2.dp, Color.Cyan, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = worldDescription,
                    color = Color.Cyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun LevelGrid(levels: List<Level>, onLevelClick: (Level) -> Unit) {
    val columns = 3
    val chunkedLevels = levels.chunked(columns)

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        chunkedLevels.forEach { rowLevels ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowLevels.forEach { level ->
                    LevelCard(level = level, onClick = { onLevelClick(level) })
                }
                // Si la última fila no tiene 3 elementos, agregamos espaciadores
                if (rowLevels.size < columns) {
                    repeat(columns - rowLevels.size) {
                        Spacer(modifier = Modifier.size(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: Level, onClick: () -> Unit) {
    val borderColor = if (level.locked) Color.DarkGray else Color(0xFFFFC107)
    val textColor = if (level.locked) Color.Gray else Color(0xFFFFC107)
    val icon = "🏁"

    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF14063D))
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = !level.locked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(text = icon, fontSize = 14.sp)
            }

            Text(
                text = level.number.toString(),
                color = textColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.Center) {
                repeat(3) { index ->
                    val filled = index < level.stars
                    Text(
                        text = if (filled) "★" else "☆",
                        color = if (filled) Color(0xFFFFC107) else Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

data class Level(
    val number: Int,
    val stars: Int,
    val locked: Boolean
)

@Preview(showBackground = true)
@Composable
fun LevelsScreenPreview() {
    val sampleLevels = (1..12).map {
        Level(
            number = it,
            stars = (0..3).random(),
            locked = it > 6
        )
    }

    LevelsScreen(
        worldName = "Mundo 1",
        worldDescription = "Operaciones de suma y resta",
        levels = sampleLevels,
        onLevelClick = {}
    )
}
