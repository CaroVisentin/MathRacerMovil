package com.app.mathracer.ui.screens.worlds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.model.WorldDto
import com.app.mathracer.ui.screens.worlds.viewmodel.WorldsViewModel

@Composable
fun WorldsScreenRoute(
    viewModel: WorldsViewModel = hiltViewModel(),
    onWorldClick: (WorldDto) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando...", color = Color.White)
            }
        }
        uiState.errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(uiState.errorMessage, color = Color.Red)
            }
        }
        else -> {
            WorldsScreen(
                worlds = uiState.worlds,
                lastAvailableWorldId = uiState.lastAvailableWorldId,
                worldProgress = uiState.worldProgress,
                onWorldClick = { world ->
                    viewModel.onWorldClicked(world, onWorldClick)
                }
            )
        }
    }
}

@Composable
fun WorldsScreen(
    worlds: List<WorldDto>?,
    lastAvailableWorldId: Int,
    worldProgress: Map<Int, Pair<Int, Int>> = emptyMap(),
    onWorldClick: (WorldDto) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B032D))
    ) {
        StarryBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            //TopBar(energy = 3)

            Text(
                text = "Mundos",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Cyan,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(48.dp)
            ) {
                itemsIndexed(worlds.orEmpty()) { index, world ->
                    ZigZagWorldItem(
                        world = world,
                        lastAvailableWorldId = lastAvailableWorldId,
                        index = index,
                        worldProgress = worldProgress,
                        onWorldClick = onWorldClick
                    )
                }
            }
        }
    }
}


@Composable
fun ZigZagWorldItem(
    world: WorldDto,
    lastAvailableWorldId: Int,
    index: Int,
    worldProgress: Map<Int, Pair<Int, Int>> = emptyMap(),
    onWorldClick: (WorldDto) -> Unit
) {
    val isEven = index % 2 == 0
    val alignment = if (isEven) Arrangement.Start else Arrangement.End

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = alignment
    ) {
        WorldCard(
            world = world,
            onClick = { onWorldClick(world) },
            lastAvailableWorldId = lastAvailableWorldId,
            worldProgressMap = worldProgress
        )
    }
}

@Composable
fun TopBar(energy: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.End
    ) {
        repeat(energy) {
            Box(
                modifier = Modifier
                    .size(16.dp, 32.dp)
                    .padding(horizontal = 2.dp)
                    .background(Color.Yellow, RoundedCornerShape(4.dp))
            )
        }
    }
}

@Composable
fun WorldCard(
    world: WorldDto,
    onClick: () -> Unit,
    lastAvailableWorldId: Int,
    worldProgressMap: Map<Int, Pair<Int, Int>> = emptyMap()
) {
    val borderColor = if (world.id > lastAvailableWorldId) Color.Gray else Color.Magenta
    val textColor = if (world.id > lastAvailableWorldId) Color.Gray else Color.White
    val titleColor = if (world.id > lastAvailableWorldId) Color.Gray else Color.Cyan

    Box(
        modifier = Modifier
            .width(250.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A0633))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = world.id <= lastAvailableWorldId) { onClick() }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = world.name,
                color = titleColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = operationsToText(world.operations),
                color = textColor,
                fontSize = 18.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            val progressPair = (worldProgressMap[world.id])
            val completed = progressPair?.first ?: 0
            val total = progressPair?.second ?: 15

            if (world.id > lastAvailableWorldId) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Blocked",
                        tint = Color.Gray
                    )
                }
                /*
                Text(
                    text = "BLOQUEADO",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )*/
                Spacer(modifier = Modifier.height(4.dp))
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val segments = total.coerceAtLeast(1)
                    repeat(segments) { index ->
                        val segColor = when {
                            world.id < lastAvailableWorldId -> Color.Yellow
                            world.id == lastAvailableWorldId -> if (index < completed) Color.Yellow else Color.DarkGray
                            else -> if (index < completed) Color.Yellow else Color.DarkGray
                        }
                        Box(
                            modifier = Modifier
                                .size(12.dp, 24.dp)
                                .padding(horizontal = 1.dp)
                                .background(
                                    segColor,
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

fun operationsToText(ops: List<String>?): String {
    if (ops.isNullOrEmpty()) return ""
    val seen = linkedSetOf<String>()
    ops.forEach {
        when (it.trim()) {
            "+" -> seen.add("Suma")
            "-" -> seen.add("Resta")
            "*", "x", "X" -> seen.add("Multiplicación")
            "/" -> seen.add("División")
            else -> seen.add(it)
        }
    }
    return seen.joinToString(" - ")
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