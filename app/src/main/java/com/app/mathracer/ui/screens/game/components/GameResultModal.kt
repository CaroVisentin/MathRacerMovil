package com.app.mathracer.ui.screens.game.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.mathracer.R

@Composable
fun GameResultModal(
    isWinner: Boolean,
    userName: String,
    userNameRival: String,
    onDismiss: () -> Unit,
    onPlayAgain: () -> Unit,
    onBackToHome: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = Color(0xE62C2C2C),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(2.dp, Color(0x66FFFFFF)),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 300.dp)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isWinner) "¡GANASTE!" else "PERDISTE",
                    color = if (isWinner) Color.Green else Color.Red,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "RESULTADOS", color = Color.White, fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp
                )
                Spacer(Modifier.height(18.dp))
                // Primer puesto
                ResultRow(
                    user = if(isWinner) userName else userNameRival,
                    carImageRes = R.drawable.car,
                    medalRes = R.drawable.medal_gold
                )
                Spacer(Modifier.height(12.dp))
                // Segundo puesto
                ResultRow(
                    user = if(isWinner) userNameRival else userName,
                    carImageRes = R.drawable.car,
                    medalRes = R.drawable.medal_silver
                )

                Spacer(Modifier.height(10.dp))
                Divider(thickness = 1.dp, color = Color.White.copy(alpha = 0.15f))

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackToHome,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color(0xE62C2C2C),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(2.dp, Color(0x66FFFFFF)),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("REGRESAR", fontWeight = FontWeight.Bold) }

                    Button(
                        onClick = onPlayAgain,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2EB7A7),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            "VOLVER A JUGAR",
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ResultRow(
    user: String,
    carImageRes: Int,
    medalRes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(medalRes),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
           // Text("Puntos:  ${result.points}", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
        }

        Image(
            painter = painterResource(carImageRes),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            contentScale = ContentScale.Fit
        )
    }
}