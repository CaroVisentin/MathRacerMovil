package com.app.mathracer.ui.screens.multiplayer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.R
import com.app.mathracer.data.network.ApiService
import com.app.mathracer.ui.theme.CyanMR
import com.app.mathracer.ui.screens.multiplayer.viewmodel.InvitationsViewModel

@Composable
fun InvitationsScreen(
    onJoinAndWait: () -> Unit = {},
    onBack: () -> Unit = {},
    viewModel: InvitationsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadInbox() }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .background(Color(0xFF07112B).copy(alpha = 0.6f), shape = RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when {
                            uiState.loading -> {
                                Box(modifier = Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = CyanMR)
                                }
                            }
                            uiState.invitations.isEmpty() -> {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text(text = "No hay invitaciones", color = Color.White, fontSize = 16.sp)
                                }
                            }
                            else -> {
                                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                                    items(uiState.invitations) { inv ->
                                        // Reuse layout style from InviteFriendsScreen: name on the left, action button on the right
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp)
                                                .border(BorderStroke(2.dp, CyanMR), shape = RoundedCornerShape(8.dp))
                                                .background(Color(0xFF07112B).copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                                                .padding(12.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = inv.inviterPlayerName ?: inv.inviterName ?: "Invitador",
                                                        color = Color.White,
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    // Show the game name or difficulty as secondary text (similar style to points in friends list)
                                                    Text(
                                                        text = inv.gameName?.let { "Partida: $it" } ?: inv.difficulty?.let { "Dificultad: $it" } ?: "",
                                                        color = Color(0xFFBFDFFF),
                                                        fontSize = 14.sp
                                                    )
                                                }

                                                Button(
                                                    onClick = {
                                                        viewModel.acceptAndGetGameId(inv.id) { ok, gameId ->
                                                            if (ok) {
                                                                try {
                                                                    gameId?.let { com.app.mathracer.ui.screens.waitingOpponent.viewmodel.PendingJoinHolder.pendingGameId = it }
                                                                } catch (e: Exception) {
                                                                    android.util.Log.w("Invitations", "Failed to store pending gameId: ${e.message}")
                                                                }
                                                                onJoinAndWait()
                                                            } else android.util.Log.e("Invitations", "Failed to accept invitation")
                                                        }
                                                    },
                                                    modifier = Modifier.widthIn(min = 88.dp),
                                                    colors = ButtonDefaults.buttonColors(containerColor = CyanMR)
                                                ) { Text(text = "Unirse", color = Color.White) }
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
}
