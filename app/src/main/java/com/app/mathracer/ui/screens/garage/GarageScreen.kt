package com.app.mathracer.ui.screens.garage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Image as ImageIcon
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import com.app.mathracer.ui.theme.CyanMR
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R
import com.app.mathracer.data.CurrentUser

@Composable
fun GarageScreen(viewModel: GarageViewModel, onBack: () -> Unit = {}) {
    val state by viewModel.uiState.collectAsState()

    
    LaunchedEffect(Unit) {
        var playerId = com.app.mathracer.data.CurrentUser.user?.id ?: 0
        var attempts = 0
        while (playerId <= 0 && attempts < 10) {
            delay(300)
            playerId = com.app.mathracer.data.CurrentUser.user?.id ?: 0
            attempts++
        }
        if (playerId > 0) {
            viewModel.loadAll(playerId)
        }
    }

   
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val maxHeightPx = screenHeightPx * 0.85f
    val minHeightPx = with(density) { 160.dp.toPx() }

    var sheetHeightPx by remember { mutableStateOf(maxHeightPx * 0.45f) }
    val sheetHeightDp by remember { derivedStateOf { with(density) { sheetHeightPx.toDp() } } }

    Box(modifier = Modifier.fillMaxSize()) {
        
        Image(painter = painterResource(id = R.drawable.garage_background), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())

         
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver", tint = CyanMR)
            }
            Text(
                text = "Garage",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = CyanMR
            )
        }

         
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    val charRes = if (state.activeCharacter != null) R.drawable.avatar else R.drawable.avatar
                    Image(painter = painterResource(id = charRes), contentDescription = null, modifier = Modifier.size(120.dp))
                }

                Spacer(modifier = Modifier.width(18.dp))

                 
                Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                    val carRes = if (state.activeCar != null) R.drawable.car else R.drawable.car
                    Image(painter = painterResource(id = carRes), contentDescription = null, modifier = Modifier.size(260.dp))
                }
            }
        }

         
        var selectedTab by remember { mutableStateOf(0) }
         
        var activatingId by remember { mutableStateOf<Int?>(null) }

        
        LaunchedEffect(state.activeCar, state.activeCharacter, state.activeBackground) {
            val activeId = when (selectedTab) {
                0 -> state.activeCar?.productId
                1 -> state.activeCharacter?.productId
                else -> state.activeBackground?.productId
            }
            if (activatingId != null && activeId != null && activatingId == activeId) {
                activatingId = null
            }
        }

        Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).height(sheetHeightDp).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)).background(Color(0xFF161616))) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Box(modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp).size(width = 64.dp, height = 6.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFF2A2A2A)).pointerInput(Unit) { detectVerticalDragGestures { change, dragAmount -> change.consume(); sheetHeightPx = (sheetHeightPx - dragAmount).coerceIn(minHeightPx, maxHeightPx) } }.clickable { sheetHeightPx = if (sheetHeightPx > (maxHeightPx + minHeightPx) / 2f) minHeightPx else maxHeightPx })

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                    TabIcon(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = Icons.Filled.DirectionsCar, label = "Autos")
                    TabIcon(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = Icons.Filled.Person, label = "Personajes")
                    TabIcon(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = Icons.Filled.Image, label = "Fondos")
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        val header = when (selectedTab) { 0 -> "Tus autos"; 1 -> "Tus personajes"; else -> "Tus fondos" }
                        Text(text = header, color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
                    }

                   
                    val longList = when (selectedTab) { 0 -> state.cars; 1 -> state.characters; else -> state.backgrounds }
                   
                    val rows = longList.chunked(3)
                    items(rows) { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            for (i in 0 until 3) {
                                if (i < row.size) {
                                    val item = row[i]
                                
                                    Box(modifier = Modifier.weight(1f)) {
                                        val isSelected = item.isActive || when (selectedTab) {
                                            0 -> state.activeCar?.productId == item.productId
                                            1 -> state.activeCharacter?.productId == item.productId
                                            else -> state.activeBackground?.productId == item.productId
                                        } || activatingId == item.productId

                                        GarageCard(item = item, selected = isSelected, modifier = Modifier.fillMaxWidth()) {
                                            val playerId = CurrentUser.user?.id ?: 0
                                            if (playerId > 0 && item.isOwned) {
                                                val type = when (selectedTab) { 0 -> "Auto"; 1 -> "Personaje"; else -> "Fondo" }
                                                activatingId = item.productId
                                                viewModel.activate(playerId, item.productId, type)
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TabIcon(selected: Boolean, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    val tint = if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.7f)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Card(colors = CardDefaults.cardColors(containerColor = if (selected) Color(0xFF2A2A2A) else Color.Transparent), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
            Box(modifier = Modifier.size(56.dp), contentAlignment = Alignment.Center) { Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(28.dp)) }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, color = Color.White.copy(alpha = if (selected) 1f else 0.7f), fontSize = 12.sp)
    }
}

@Composable
private fun GarageCard(item: com.app.mathracer.data.network.GarageItemDto, selected: Boolean = false, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val borderColor = if (selected) Color.White else Color.Transparent
    Card(
        modifier = modifier
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
            val res = when (item.productType) { "Auto" -> R.drawable.car; "Personaje" -> R.drawable.avatar; else -> R.drawable.background }
            androidx.compose.foundation.layout.Box(modifier = Modifier.size(88.dp).clip(RoundedCornerShape(8.dp)).background(Color.Transparent).padding(0.dp), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = res), contentDescription = item.name, modifier = Modifier.size(80.dp), contentScale = ContentScale.Crop)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.name, color = Color.White, fontSize = 12.sp, maxLines = 1)
        }
    }
}
