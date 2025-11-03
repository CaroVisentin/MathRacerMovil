package com.app.mathracer.ui.screens.multiplayer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.app.mathracer.R
import com.app.mathracer.ui.theme.CyanMR

data class FriendItem(
    val id: String,
    val name: String,
    val points: Int = 0
)

@Composable
fun InviteFriendsScreen(
    friends: List<FriendItem> = listOf(
        FriendItem("f1", "Nico", points = 1240),
        FriendItem("f2", "Luis", points = 980),
        FriendItem("f3", "María", points = 1575)
    ),
    onInvite: (friendId: String, difficulty: String, resultType: String) -> Unit = { _, _, _ -> },
    onBack: () -> Unit = {}
) {
    var selectedFriend by remember { mutableStateOf<FriendItem?>(null) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var difficulty by remember { mutableStateOf("Fácil") }
    var resultType by remember { mutableStateOf("Mayor") }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

        
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver", tint = CyanMR)
                }
                Text(
                    text = "Invitar amigos",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanMR
                )
            }

            
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .background(Color(0xFF07112B).copy(alpha = 0.6f), shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(friends) { friend ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .border(BorderStroke(2.dp, CyanMR), shape = RoundedCornerShape(8.dp))
                                    .background(Color(0xFF07112B).copy(alpha = 0.3f), shape = RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = friend.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Puntos: ${friend.points}", color = Color(0xFFBFDFFF), fontSize = 14.sp)
                                    }

                                    Button(
                                        onClick = {
                                            selectedFriend = friend
                                            // open dialog for difficulty/result selection
                                            showInviteDialog = true
                                        },
                                        modifier = Modifier.widthIn(min = 88.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanMR)
                                    ) { Text(text = "Invitar", color = Color.White) }
                                }
                            }
                        }
                    }
                }
            }

            if (showInviteDialog && selectedFriend != null) {
                androidx.compose.ui.window.Dialog(onDismissRequest = { showInviteDialog = false; selectedFriend = null }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                            .background(Color(0xFF07112B).copy(alpha = 0.95f), shape = RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Invitar a ${selectedFriend?.name}", color = CyanMR, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                            Text(text = "Dificultad", color = Color.White)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = { difficulty = "Fácil" },
                                    modifier = Modifier
                                        .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                        .background(if (difficulty == "Fácil") Color(0xFF0B2A4A) else Color.Transparent, shape = RoundedCornerShape(8.dp))
                                ) { Text(text = "Fácil", color = CyanMR) }
                                TextButton(
                                    onClick = { difficulty = "Medio" },
                                    modifier = Modifier
                                        .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                        .background(if (difficulty == "Medio") Color(0xFF0B2A4A) else Color.Transparent, shape = RoundedCornerShape(8.dp))
                                ) { Text(text = "Medio", color = CyanMR) }
                                TextButton(
                                    onClick = { difficulty = "Difícil" },
                                    modifier = Modifier
                                        .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                        .background(if (difficulty == "Difícil") Color(0xFF0B2A4A) else Color.Transparent, shape = RoundedCornerShape(8.dp))
                                ) { Text(text = "Difícil", color = CyanMR) }
                            }

                            Text(text = "Tipo de resultado", color = Color.White)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = { resultType = "Mayor" },
                                    modifier = Modifier
                                        .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                        .background(if (resultType == "Mayor") Color(0xFF0B2A4A) else Color.Transparent, shape = RoundedCornerShape(8.dp))
                                ) { Text(text = "Mayor", color = CyanMR) }
                                TextButton(
                                    onClick = { resultType = "Menor" },
                                    modifier = Modifier
                                        .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                        .background(if (resultType == "Menor") Color(0xFF0B2A4A) else Color.Transparent, shape = RoundedCornerShape(8.dp))
                                ) { Text(text = "Menor", color = CyanMR) }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(
                                    onClick = { showInviteDialog = false; selectedFriend = null },
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                        .background(Color(0xFF07112B).copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                                ) { Text(text = "Cancelar", color = CyanMR) }

                                TextButton(
                                    onClick = {
                                        selectedFriend?.let { onInvite(it.id, difficulty, resultType) }
                                        showInviteDialog = false
                                        selectedFriend = null
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                        .background(Color(0xFF07112B).copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                                ) { Text(text = "Invitar", color = CyanMR) }
                            }
                        }
                    }
                }
            }
        }
    }
}
