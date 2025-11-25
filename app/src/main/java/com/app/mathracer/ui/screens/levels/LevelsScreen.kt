package com.app.mathracer.ui.screens.levels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import com.app.mathracer.ui.theme.CyanMR
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.data.model.LevelDto
import java.net.URLDecoder
import android.util.Base64
import com.app.mathracer.ui.screens.levels.viewmodel.LevelsViewModel

@Composable
fun LevelsScreen(
    viewModel: LevelsViewModel,
    worldId: Int = 0,
    worldOperationsEncoded: String = "",
    onLevelClick: (Int, String) -> Unit = { _, _ -> },
    onObtenerRecompensaClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val claimedKey = "world_reward_claimed_$worldId"
    val claimed = prefs.getBoolean(claimedKey, false)

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
                    fontSize = 28.sp,
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
                            LevelCard(
                                level = level,
                                lastCompletedLevelId = uiState.lastCompletedLevelId,
                                onClick = {
                                    if (level.id <= uiState.lastCompletedLevelId + 1) {
                                        onLevelClick(level.id, level.resultType)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

               
                val levelsList = uiState.levels.orEmpty()
                val completedCount = levelsList.count { it.id <= uiState.lastCompletedLevelId }
                val allCompleted = levelsList.isNotEmpty() && completedCount >= levelsList.size

                val buttonEnabled = allCompleted && !claimed
                val buttonBorderColor = if (buttonEnabled) CyanMR else Color.Gray
                val buttonBgColor = if (buttonEnabled) Color.Black.copy(alpha = 0.6f) else Color.DarkGray
                val buttonTextColor = if (buttonEnabled) CyanMR else Color.LightGray

                TextButton(
                    onClick = { onObtenerRecompensaClick(worldId) },
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .border(
                            width = 2.dp,
                            color = buttonBorderColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            buttonBgColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Text(
                        text = if (claimed) "Recompensa obtenida" else "Obtener recompensa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = buttonTextColor,
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color(0xFF0E043A), RoundedCornerShape(12.dp))
                        .border(2.dp, Color.Cyan, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = operationsEncodedToText(worldOperationsEncoded),
                        color = Color.Cyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun LevelCard(
    level: LevelDto,
    lastCompletedLevelId: Int,
    onClick: () -> Unit = {}
) {
    val gold = Color(0xFFFFC107)
    val darkTransparent = Color(0xAA0A031F)
    val isUnlocked = level.id <= lastCompletedLevelId + 1

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
            .border(2.dp, colorCard, RoundedCornerShape(12.dp))
            .then(
                if (isUnlocked) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
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
                    fontSize = 20.sp
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
                    fontSize = 18.sp,
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

fun operationsEncodedToText(encoded: String): String {
    if (encoded.isBlank()) return ""
    val decoded = try {
        val bytes = Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        String(bytes, Charsets.UTF_8)
    } catch (e: Exception) {
        try {
            URLDecoder.decode(encoded, "UTF-8")
        } catch (e2: Exception) {
            ""
        }
    }

    if (decoded.isBlank()) return ""

    val opsList = decoded.split(Regex("[,\\s]+"))
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    if (opsList.isEmpty()) return ""

    val seen = linkedSetOf<String>()
    opsList.forEach { op ->
        when (op) {
            "+" -> seen.add("Suma")
            "-" -> seen.add("Resta")
            "*", "x", "X" -> seen.add("Multiplicación")
            "/" -> seen.add("División")
            else -> if (op.isNotBlank()) seen.add(op)
        }
    }

    return seen.joinToString(" - ")
}
