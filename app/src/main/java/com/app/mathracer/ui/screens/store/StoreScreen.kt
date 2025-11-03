package com.app.mathracer.ui.screens.store

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun StoreScreen(
    viewModel: StoreViewModel = StoreViewModel(),
    onBack: () -> Unit = {}
) {
    val coins by viewModel.coins.collectAsState()
    val items by viewModel.items.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<StoreItem?>(null) }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF111111)) {
        Column(modifier = Modifier
            .fillMaxSize()
            
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 16.dp, vertical = 8.dp)) {

            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
        Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
            .padding(4.dp)
            .clickable { onBack() }
                )
                Text(
                    text = "TIENDA",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    textAlign = TextAlign.Center
                )
              
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFFD54F), shape = CircleShape), contentAlignment = Alignment.Center) {
                        Text("🪙", fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = String.format("%,d", coins), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Box(modifier = Modifier
                    .background(brush = Brush.horizontalGradient(listOf(Color(0xFF0F4C75), Color(0xFF2B2D42))))
                    .fillMaxSize()
                    .padding(12.dp)) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text("OFERTA ESPECIAL", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("MARIO + AUTO ESTILO\n\"MATH RACER\"", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFFFD54F), shape = CircleShape), contentAlignment = Alignment.Center) {
                                Text("🪙")
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Text("100.000", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    
                    Box(modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0B2948)))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("AUTOS", color = Color.White, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))

            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                content = {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .height(150.dp)
                                .clickable {
                                    selectedItem = item
                                    showDialog = true
                                },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF1E1E1E))) {
                               
                                Box(modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .size(80.dp)
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF2E3A59)))

                               
                                Row(modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(modifier = Modifier
                                        .size(28.dp)
                                        .background(Color(0xFFFFD54F), shape = CircleShape), contentAlignment = Alignment.Center) {
                                        Text("🪙", fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text(text = String.format("%,d", item.price), color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                     
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)) {
                            Box(modifier = Modifier
                                .background(brush = Brush.horizontalGradient(listOf(Color(0xFF0F4C75), Color(0xFF2B2D42))))
                                .fillMaxSize()
                                .padding(12.dp)) {
                                Column(modifier = Modifier.align(Alignment.CenterStart)) {
                                    Text("OFERTA ESPECIAL", color = Color.White, fontWeight = FontWeight.Bold)
                                    Text("MARIO + AUTO ESTILO\n\"MATH RACER\"", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier
                                            .size(36.dp)
                                            .background(Color(0xFFFFD54F), shape = CircleShape), contentAlignment = Alignment.Center) {
                                            Text("🪙")
                                        }
                                        Spacer(modifier = Modifier.size(8.dp))
                                        Text("100.000", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                })

            
            if (showDialog && selectedItem != null) {
                androidx.compose.ui.window.Dialog(onDismissRequest = {
                    showDialog = false
                    selectedItem = null
                }) {
                     
                    Box(modifier = Modifier
                        .widthIn(max = 360.dp)
                        .padding(16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF111111))
                        .border(width = 3.dp, color = Color.White, shape = RoundedCornerShape(14.dp))
                        .padding(18.dp)) {
                        
                        Text(
                            text = "X",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable {
                                    showDialog = false
                                    selectedItem = null
                                }
                        )

                        
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)) {
                            Text(
                                text = "¿Deseas comprar este\nartículo por ${selectedItem?.price?.let { String.format("%,d", it) }} monedas?",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)) {
                                Box(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Transparent)
                                    .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedItem?.let { viewModel.buyItem(it.id) }
                                        showDialog = false
                                        selectedItem = null
                                    },
                                    contentAlignment = Alignment.Center) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier
                                            .size(28.dp)
                                            .background(Color(0xFFFFD54F), shape = CircleShape), contentAlignment = Alignment.Center) {
                                            Text("🪙", fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = "COMPRAR", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun StoreScreenPreview() {
    StoreScreen()
}
