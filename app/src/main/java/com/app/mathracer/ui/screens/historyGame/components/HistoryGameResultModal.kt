package com.app.mathracer.ui.screens.historyGame.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.mathracer.R

@Composable
fun HistoryGameResultModal(
    isWinner: Boolean,
    reward: Int = 0,
    levelNumber: Int,
    onDismiss: () -> Unit,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = Color(0xE62C2C2C),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(2.dp, Color(0x66FFFFFF))
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 300.dp)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Nivel
                Text(
                    text = "Nivel $levelNumber",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = if (isWinner) "¡GANASTE!" else "PERDISTE",
                    color = if (isWinner) Color(0xFF3CFF4D) else Color.Red,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(16.dp))

                Image(
                    painter = if(isWinner) painterResource(R.drawable.mathi) else painterResource(R.drawable.mathi_sad),
                    contentDescription = null,
                    modifier = Modifier.size(110.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(16.dp))

                if (isWinner) {
                    Text(
                        text = "Recompensa obtenida",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.coin),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = reward.toString(),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.height(20.dp))
                }

                Divider(thickness = 1.dp, color = Color.White.copy(alpha = 0.15f))
                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(2.dp, Color(0x66FFFFFF)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("REGRESAR", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onNext,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2EB7A7),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            if (isWinner) "SIGUIENTE" else "VOLVER A INTENTAR",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
