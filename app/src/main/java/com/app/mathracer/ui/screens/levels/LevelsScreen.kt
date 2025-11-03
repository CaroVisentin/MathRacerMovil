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
import com.app.mathracer.data.model.LevelDto
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    itemsIndexed(uiState.levels.orEmpty()) { index, level ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .padding(horizontal = 4.dp),
                            contentAlignment = if (index % 2 == 0)
                                Alignment.CenterStart else Alignment.CenterEnd
                        ) {
                            LevelCard(level, uiState.lastCompletedLevelId)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color(0xFF0E043A), RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Cyan, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "descripcion q no llega del back",// uiState.worldDescription,
                        color = Color.Cyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun LevelCard(level: LevelDto, lastCompletedLevelId: Int) {
    val gold = Color(0xFFFFC107)
    val darkTransparent = Color(0xAA0A031F)
    val colorCard = when {
        level.id == lastCompletedLevelId + 1 -> Color.Magenta
        level.id <= lastCompletedLevelId -> gold
        else -> Color.DarkGray
    }
    val iconCard = when {
        level.id == lastCompletedLevelId + 1 -> "🔓"
        level.id <= lastCompletedLevelId-> "🏁"
        else -> "🔒"
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(darkTransparent)
            .border(2.dp, colorCard, RoundedCornerShape(12.dp)),
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
                    text = iconCard,
                    fontSize = 16.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = level.number.toString(),
                    color = colorCard,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            if (level.id > lastCompletedLevelId + 1) {
                Text(
                    text = "BLOQUEADO",
                    color = Color.Gray.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(10.dp))
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
