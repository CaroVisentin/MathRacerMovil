package com.app.mathracer.ui.screens.multiplayer

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.app.mathracer.data.repository.GameInvitationRepository
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R
import com.app.mathracer.ui.theme.CyanMR

@Composable
fun MultiplayerOptionsScreen(
    onCreateGame: () -> Unit = {},
    onJoinGame: () -> Unit = {},
    onInviteFriend: () -> Unit = {},
    onInvitationInbox: () -> Unit = {},
    onCompetitiveMatch: () -> Unit = {},
    onRanking: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var invitationsCount by remember { mutableStateOf<Int?>(null) }

    // Load invitations count on enter
    LaunchedEffect(Unit) {
        try {
            val resp = GameInvitationRepository.getInbox()
            if (resp.isSuccessful) {
                invitationsCount = resp.body()?.totalInvitations ?: 0
            } else {
                invitationsCount = 0
            }
        } catch (e: Exception) {
            invitationsCount = 0
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            
            androidx.compose.foundation.layout.Box(
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
                    text = "Multijugador",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanMR
                )
            }

             
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, end = 48.dp)
                        .background(Color.Black.copy(alpha = 0.45f), shape = RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onCreateGame,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Crear partida",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanMR,
                            )
                        }

                        TextButton(
                            onClick = onJoinGame,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Unirse a partida",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanMR,
                            )
                        }

                        TextButton(
                            onClick = onInviteFriend,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Invitar amigo",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanMR,
                            )
                        }

                        TextButton(
                            onClick = onInvitationInbox,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Buzon de invitaciones",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanMR,
                                )

                                // show badge if there are invitations
                                invitationsCount?.let { count ->
                                    if (count > 0) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 12.dp)
                                                .size(20.dp)
                                                .background(color = Color.Red, shape = androidx.compose.foundation.shape.CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(text = count.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        TextButton(
                            onClick = onCompetitiveMatch,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Partida competitiva",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanMR,
                            )
                        }

                        TextButton(
                            onClick = onRanking,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Ranking",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanMR,
                            )
                        }
                    }
                }
            }
        }
    }
}
