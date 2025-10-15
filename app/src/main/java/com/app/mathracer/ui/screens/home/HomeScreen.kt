package com.app.mathracer.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.R
import com.app.mathracer.ui.screens.home.viewmodel.HomeViewModel
import com.app.mathracer.ui.screens.home.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    onMultiplayerClick: () -> Unit = {},
    onStoryModeClick: () -> Unit = {},
    onFreePracticeClick: () -> Unit = {},
    onShopClick: () -> Unit = {},
    onGarageClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    viewModel: com.app.mathracer.ui.screens.home.viewmodel.HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Observar cuando se debe navegar
    LaunchedEffect(uiState.navigateToWaiting) {
        if (uiState.navigateToWaiting) {
            viewModel.clearNavigation()
            onMultiplayerClick()
        }
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column {
                Scaffold(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    topBar = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 60.dp
                            )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = null,
                                modifier = Modifier.width(150.dp)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            
                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.battery),
                                    contentDescription = null,
                                    modifier = Modifier.width(60.dp)
                                )
                                Row {
                                    Image(
                                        painter = painterResource(id = R.drawable.coin),
                                        contentDescription = null,
                                        modifier = Modifier.width(25.dp)
                                    )
                                    Box(modifier = Modifier.width(8.dp))
                                    Text(
                                        "123.000",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Box(modifier = Modifier.width(24.dp))
                            
                            Image(
                                painter = painterResource(R.drawable.avatar),
                                contentDescription = "avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                ) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.car),
                            contentDescription = null,
                            modifier = Modifier.padding(innerPadding)
                        )
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp, start = 64.dp, end = 64.dp)
                        ) {
                            TextButton(
                                onClick = { 
                                    // Solo navegar, no manejar conexiones aquí
                                    viewModel.navigateToMultiplayer()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(width = 2.dp, color = Color.White)
                            ) {
                                Text(
                                    text = "Multijugador",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            }
                            
                            TextButton(
                                onClick = onStoryModeClick,
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color.Gray)
                                    .border(width = 2.dp, color = Color.White)
                            ) {
                                Text(
                                    text = "Modo Historia",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray,
                                )
                            }
                            
                            TextButton(
                                onClick = onFreePracticeClick,
                                enabled = false,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color.Gray)
                                    .border(width = 2.dp, color = Color.White)
                            ) {
                                Text(
                                    text = "Práctica libre",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray,
                                )
                            }
                        }
                        
                        Box(modifier = Modifier.height(32.dp))
                        
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp)
                        ) {
                            IconButton(
                                onClick = onShopClick,
                                modifier = Modifier
                                    .shadow(4.dp, RoundedCornerShape(16.dp))
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .size(64.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ShoppingCart,
                                    contentDescription = "Tienda",
                                    tint = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(32.dp))
                            
                            IconButton(
                                onClick = onGarageClick,
                                modifier = Modifier
                                    .shadow(4.dp, RoundedCornerShape(16.dp))
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .size(64.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Home,
                                    contentDescription = "Garage",
                                    tint = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(32.dp))
                            
                            IconButton(
                                onClick = onStatsClick,
                                modifier = Modifier
                                    .shadow(4.dp, RoundedCornerShape(16.dp))
                                    .background(Color.White, RoundedCornerShape(16.dp))
                                    .size(64.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = "Estadísticas",
                                    tint = Color.Black,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // SnackbarHost para mostrar errores
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                SnackbarHost(hostState = snackbarHostState)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}