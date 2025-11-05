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
    // Obtenemos el estado desde el ViewModel
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
            // Espacio superior extra
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
        WorldCard(world = world, onClick = { onWorldClick(world) }, lastAvailableWorldId = lastAvailableWorldId)
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
    lastAvailableWorldId: Int
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
                text = world.difficulty,
                color = textColor,
                fontSize = 20.sp
            )

            // Barra de progreso
            if(world.id > lastAvailableWorldId) {
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
                Text(
                    text = "BLOQUEADO",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            } else {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(15) { index ->
                        Box(
                            modifier = Modifier
                                .size(12.dp, 24.dp)
                                .padding(horizontal = 1.dp)
                                .background(
                                   if (world.id < lastAvailableWorldId) Color.Yellow else Color.DarkGray, //me tengo q traer los niveles y setearlo con eso
                                    RoundedCornerShape(2.dp)
                                )
                        )
                    }
                }
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

// Modelo de datos
data class World(
    val id: Int,
    val title: String,
    val topic: String,
    val completedLevels: Int,
    val totalLevels: Int,
    val locked: Boolean
)

@Preview(showBackground = true)
@Composable
fun WorldsScreenPreview() {
    val sampleWorlds = listOf(
        World(1, "Mundo 1", "Suma y Resta", 10, 10, false),
        World(2, "Mundo 2", "Multiplicación", 6, 15, false),
        World(3, "Mundo 3", "División", 0, 10, true),
        World(4, "Mundo 4", "Operaciones Mixtas", 0, 10, true)
    )
//    WorldsScreen(worlds = sampleWorlds, onWorldClick = {})
}
