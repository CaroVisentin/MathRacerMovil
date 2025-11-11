package com.app.mathracer.ui.screens.multiplayer

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.app.mathracer.ui.theme.CyanMR

data class MatchItem(
    val id: String,
    val name: String,
    val difficulty: String,
    val privacy: String,
    val requiresPassword: Boolean
)

@Composable
fun JoinMatchesScreen(
    matches: List<MatchItem> = listOf(
        MatchItem("m1","Partida 1","Fácil","Pública", false),
        MatchItem("m2","Partida Privada","Difícil","Privada", true),
        MatchItem("m3","Rápida","Medio","Pública", false)
    ),
    onJoinConfirmed: (matchId: String, password: String?) -> Unit = { _, _ -> },
    onBack: () -> Unit = {}
) {
    var query by remember { mutableStateOf("") }
    var difficultyFilter by remember { mutableStateOf("") }
    var privacyFilter by remember { mutableStateOf("") }

    var showPasswordDialog by remember { mutableStateOf(false) }
    var selectedMatch by remember { mutableStateOf<MatchItem?>(null) }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            Image(
                painter = painterResource(id = com.app.mathracer.R.drawable.background),
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
                    text = "Lista de partidas",
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
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Buscar nombre") },
                        modifier = Modifier.fillMaxWidth(),

                    )

                 
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = { difficultyFilter = if (difficultyFilter == "Fácil") "" else "Fácil" },
                            modifier = Modifier
                                .weight(1f)
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(if (difficultyFilter == "Fácil") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp))
                        ) { Text(text = "Fácil", color = CyanMR) }

                        TextButton(
                            onClick = { difficultyFilter = if (difficultyFilter == "Medio") "" else "Medio" },
                            modifier = Modifier
                                .weight(1f)
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(if (difficultyFilter == "Medio") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp))
                        ) { Text(text = "Medio", color = CyanMR) }

                        TextButton(
                            onClick = { difficultyFilter = if (difficultyFilter == "Difícil") "" else "Difícil" },
                            modifier = Modifier
                                .weight(1f)
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(if (difficultyFilter == "Difícil") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp))
                        ) { Text(text = "Difícil", color = CyanMR) }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = { privacyFilter = if (privacyFilter == "Pública") "" else "Pública" },
                            modifier = Modifier
                                .weight(1f)
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(if (privacyFilter == "Pública") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp))
                        ) { Text(text = "Pública", color = CyanMR) }

                        TextButton(
                            onClick = { privacyFilter = if (privacyFilter == "Privada") "" else "Privada" },
                            modifier = Modifier
                                .weight(1f)
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(if (privacyFilter == "Privada") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp))
                        ) { Text(text = "Privada", color = CyanMR) }
                    }

                  
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        val filtered = matches.filter {
                            (query.isBlank() || it.name.contains(query, ignoreCase = true)) &&
                                    (difficultyFilter.isBlank() || it.difficulty == difficultyFilter) &&
                                    (privacyFilter.isBlank() || it.privacy == privacyFilter)
                        }

                        items(filtered) { match ->
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
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = match.name, color = Color.White, modifier = Modifier.weight(0.45f))
                                    Text(text = match.difficulty, color = Color.White, modifier = Modifier.weight(0.2f))
                                    Text(text = match.privacy, color = Color.White, modifier = Modifier.weight(0.2f))
                                    Button(
                                        onClick = {
                                            selectedMatch = match
                                            password = ""
                                            showPasswordDialog = true
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

            if (showPasswordDialog && selectedMatch != null) {
                    androidx.compose.ui.window.Dialog(onDismissRequest = { showPasswordDialog = false; selectedMatch = null }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp)
                                .background(Color(0xFF07112B).copy(alpha = 0.95f), shape = RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Contraseña", color = CyanMR, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                

                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Contraseña") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    visualTransformation = PasswordVisualTransformation(),
                                    modifier = Modifier.fillMaxWidth(),

                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    TextButton(
                                        onClick = { showPasswordDialog = false; selectedMatch = null },
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                            .background(Color(0xFF07112B).copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                                    ) { Text(text = "Cancelar", color = CyanMR) }

                                    TextButton(
                                        onClick = {
                                            showPasswordDialog = false
                                            selectedMatch?.let { onJoinConfirmed(it.id, password) }
                                            selectedMatch = null
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                            .background(Color(0xFF07112B).copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                                    ) { Text(text = "Unirse", color = CyanMR) }
                                }
                            }
                        }
                    }
            }
        }
    }
}
