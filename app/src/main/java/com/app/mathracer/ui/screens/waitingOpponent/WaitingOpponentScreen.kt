package com.app.mathracer.ui.screens.waitingOpponent

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.R
import com.app.mathracer.ui.screens.waitingOpponent.viewmodel.WaitingOpponentViewModel

@Composable
fun WaitingOpponentScreen(
    onNavigateToGame: (gameId: String, playerName: String) -> Unit = { _, _ -> },
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: WaitingOpponentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Navegar al juego cuando esté listo
    LaunchedEffect(uiState.gameStarted, uiState.gameId) {
        if (uiState.gameStarted && uiState.gameId != null && uiState.playerName != null) {
            onNavigateToGame(uiState.gameId!!, uiState.playerName!!)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .width(220.dp)
                    .height(100.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Color.White),
                colors = CardDefaults.cardColors(containerColor = Color(0x80000000)),
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_earth),
                        contentDescription = "Globo terráqueo",
                        modifier = Modifier.size(64.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = when {
                            uiState.isConnecting -> "Conectando al servidor..."
                            uiState.isSearchingMatch -> "Buscando oponente..."
                            uiState.opponentFound -> "¡Oponente encontrado!"
                            else -> "Preparando partida..."
                        },
                        color = Color.Cyan,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (uiState.opponentName != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.opponentName!!,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                    
                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.error!!,
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!uiState.gameStarted) {
                        HourGlassRow()
                    }
                }
            }
        }
    }
}

@Composable
fun HourGlassRow() {
    val infiniteTransition = rememberInfiniteTransition(label = "hourglass")

    val delays = listOf(0, 300, 600)
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        delays.forEach { delay ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 900, easing = LinearEasing, delayMillis = delay),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "alpha"
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_hourglass),
                contentDescription = "Reloj de arena",
                tint = Color.Cyan.copy(alpha = alpha),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Preview
@Composable
fun WaitingOpponentScreenPreview() {
    WaitingOpponentScreen()
}
