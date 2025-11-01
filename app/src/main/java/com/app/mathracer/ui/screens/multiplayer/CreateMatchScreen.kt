package com.app.mathracer.ui.screens.multiplayer

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R
import com.app.mathracer.ui.theme.CyanMR

@Composable
fun CreateMatchScreen(
    onCreateMatch: (name: String, privacy: String, difficulty: String, resultType: String) -> Unit = { _, _, _, _ -> },
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    val (name, setName) = remember { mutableStateOf("") }
    val (privacy, setPrivacy) = remember { mutableStateOf("Privada") }
    val (difficulty, setDifficulty) = remember { mutableStateOf("Fácil") }
    val (resultType, setResultType) = remember { mutableStateOf("Mayor") }

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
                    .align(Alignment.TopCenter)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Volver", tint = CyanMR)
                }
                Text(
                    text = "Crear partida",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanMR
                )
            }

             
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp)
                        .background(Color.Black.copy(alpha = 0.45f), shape = RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { setName(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier
                                .fillMaxWidth()
                        )

                        
                        Text(text = "Privacidad", color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier) {
                            TextButton(
                                onClick = { setPrivacy("Privada") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                    .background(if (privacy == "Privada") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                            ) { Text(text = "Privada", color = CyanMR) }

                            TextButton(
                                onClick = { setPrivacy("Pública") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                    .background(if (privacy == "Pública") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                            ) { Text(text = "Pública", color = CyanMR) }
                        }

                        
                        Text(text = "Dificultad", color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier) {
                            val optionModifier = Modifier
                                .weight(1f)
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                            TextButton(
                                onClick = { setDifficulty("Fácil") },
                                modifier = optionModifier.background(if (difficulty == "Fácil") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                            ) { Text(text = "Fácil", color = CyanMR) }
                            TextButton(
                                onClick = { setDifficulty("Medio") },
                                modifier = optionModifier.background(if (difficulty == "Medio") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                            ) { Text(text = "Medio", color = CyanMR) }
                            TextButton(
                                onClick = { setDifficulty("Difícil") },
                                modifier = optionModifier.background(if (difficulty == "Difícil") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                            ) { Text(text = "Difícil", color = CyanMR) }
                        }

                      
                        Text(text = "Tipo de resultado", color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier) {
                            TextButton(
                                onClick = { setResultType("Mayor") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                    .background(if (resultType == "Mayor") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                            ) { Text(text = "Mayor", color = CyanMR) }

                            TextButton(
                                onClick = { setResultType("Menor") },
                                modifier = Modifier
                                    .weight(1f)
                                    .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                    .background(if (resultType == "Menor") Color.Black.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.4f), shape = RoundedCornerShape(8.dp))
                            ) { Text(text = "Menor", color = CyanMR) }
                        }

                        TextButton(
                            onClick = {
                                onCreateMatch(name, privacy, difficulty, resultType)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) { Text(text = "Crear", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = CyanMR) }
                    }
                }
            }
        }
    }
}
