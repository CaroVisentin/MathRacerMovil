package com.app.mathracer.ui.screens.levels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.ui.screens.levels.viewmodel.LevelUiModel
import com.app.mathracer.ui.screens.levels.viewmodel.LevelsViewModel

@Composable
fun LevelsScreen(viewModel: LevelsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B032D))
    ) {
        StarryBackground()

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Cyan)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 46.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.worldName,
                    color = Color.Cyan,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(uiState.levels) { index, level ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .padding(horizontal = 4.dp),
                            contentAlignment = if (index % 2 == 0)
                                Alignment.CenterStart else Alignment.CenterEnd
                        ) {
                            LevelCard(level)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LevelCard(level: LevelUiModel) {
    val gold = Color(0xFFFFC107)
    val darkTransparent = Color(0xAA0A031F)
    val borderColor = if (level.isUnlocked) gold else Color.DarkGray
    val textColor = if (level.isUnlocked) gold else Color.Gray
    val flag = "🏁"

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(darkTransparent)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp)),
//            .clickable(enabled = level.isUnlocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = flag,
                    fontSize = 16.sp
                )
            }

            Text(
                text = level.name.replace("Nivel ", ""), // Muestra solo el número
                color = textColor,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(3) { index ->
                    val filled = index < level.stars
                    Text(
                        text = if (filled) "★" else "☆",
                        color = if (filled) gold else Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            }

            if (!level.isUnlocked) {
                Text(
                    text = "BLOQUEADO",
                    color = Color.Gray.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

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
