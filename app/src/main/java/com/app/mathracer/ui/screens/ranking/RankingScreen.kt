package com.app.mathracer.ui.screens.ranking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R
import com.app.mathracer.ui.screens.ranking.viewmodel.PlayerRanking
import com.app.mathracer.ui.screens.ranking.viewmodel.RankingViewModel

@Composable
fun RankingScreen(viewModel: RankingViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B032D))
            .padding(16.dp)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = Color.Magenta,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage ?: "Error desconocido",
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "RANKING",
                    color = Color(0xFFFF4DF1),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                TopThreeSection(players = uiState.topPlayers.take(3))

                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.topPlayers) { player ->
                        RankingItem(player)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))


                val positionText = uiState.userPosition?.toString() ?: "–"
                Text(
                    text = "Tu posición actual: $positionText",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun TopThreeSection(players: List<PlayerRanking>) {
    if (players.size < 3) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        PodiumItem(
            player = players[1],
            color = Color(0xFFFFD700),
            position = 2,
            podiumHeight = 80.dp
        )
        PodiumItem(
            player = players[0],
            color = Color(0xFF00FFFF),
            position = 1,
            podiumHeight = 110.dp
        )
        PodiumItem(
            player = players[2],
            color = Color(0xFFFF0033),
            position = 3,
            podiumHeight = 70.dp
        )
    }
}

@Composable
fun PodiumItem(
    player: PlayerRanking,
    color: Color,
    position: Int,
    podiumHeight: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            painter = painterResource(id = R.drawable.avatar),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .offset(y = (-8).dp)
        )

        Box(
            modifier = Modifier
                .width(70.dp)
                .height(podiumHeight)
                .background(Color(0xFF0B032D))
                .border(width = 3.dp, color = color, shape = RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = position.toString(),
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RankingItem(player: PlayerRanking) {
    val borderColor = when (player.position) {
        1 -> Color(0xFF00FFFF)
        2 -> Color(0xFFFFD700)
        3 -> Color(0xFFFF0033)
        else -> Color(0xFF4A4A9E)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(6.dp))
            .background(Color(0xFF0B032D))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(borderColor, shape = RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.position.toString(),
                        color = Color(0xFF0B032D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.avatar), // reemplazar con la imagen real o URL
                    contentDescription = "Avatar de ${player.username}",
                    modifier = Modifier
                        .size(28.dp)
                        .border(width = 1.5.dp, color = borderColor, shape = CircleShape)
                        .background(Color.Transparent, shape = CircleShape)
                        .padding(2.dp)
                        .clip(CircleShape)
                )

                Text(
                    text = player.username,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Puntaje
            Text(
                text = player.score.toString(),
                color = borderColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
