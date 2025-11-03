package com.app.mathracer.ui.rules

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R

@Composable
fun RulesScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background), // tu imagen de fondo
            contentDescription = "Fondo de reglas",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0x88000000), Color(0xCC000000))
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "REGLAS",
                fontSize = 28.sp,
                color = Color.Cyan,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mathi Racer es un juego de carreras donde para avanzar en la pista deberás resolver ecuaciones correctamente. Cada respuesta acertada hace que tu auto avance y te acerque a la meta.\n\nSi respondes mal, verás la respuesta correcta y recibirás otra ecuación para continuar con el juego.",
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "TIPOS DE DESAFÍOS:",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                ChallengeItem(
                    textParts = listOf(
                        "Encuentra el valor de ",
                        "x",
                        " para que ",
                        "y",
                        " sea el número mayor posible."
                    )
                )
                ChallengeItem(
                    textParts = listOf(
                        "Encuentra el valor de ",
                        "x",
                        " para que ",
                        "y",
                        " sea el número menor posible."
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "MODOS DE JUEGO:",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            GameModeItem(
                title = "MULTIJUGADOR:",
                description = "compite contra otros jugadores en carreras rápidas. Gana el que llegue primero a la meta.\nCada victoria te da puntos para subir en el ranking."
            )

            Spacer(modifier = Modifier.height(20.dp))

            GameModeItem(
                title = "HISTORIA:",
                description = "juega solo contra la máquina, avanza por niveles y mundos con dificultad creciente.\nAl superar niveles recibes monedas o premios sorpresa."
            )

            // --- Nueva sección separada: VIDAS ---
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "VIDAS:",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("El modo HISTORIA cuenta con 3 vidas, al perder un nivel perderá ")
                    withStyle(style = SpanStyle(color = Color(0xFF00FFFF), fontWeight = FontWeight.Bold)) {
                        append("una vida")
                    }
                    append(".\nLas mismas se recargarán con tiempo o canjeándolas por monedas.")
                },
                color = Color.White,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun ChallengeItem(textParts: List<String>) {
    Text(
        text = buildAnnotatedString {
            textParts.forEach { part ->
                when (part) {
                    "x" -> withStyle(style = SpanStyle(color = Color.Cyan, fontWeight = FontWeight.Bold)) { append(part) }
                    "y" -> withStyle(style = SpanStyle(color = Color(0xFF00FFAA), fontWeight = FontWeight.Bold)) { append(part) }
                    else -> append(part)
                }
            }
        },
        fontSize = 16.sp,
        color = Color.White,
        lineHeight = 22.sp
    )
}

@Composable
fun GameModeItem(
    title: String,
    description: String,
    highlighted: String? = null,
    suffix: String = ""
) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Cyan, fontWeight = FontWeight.Bold)) {
                append("• $title ")
            }
            append(description)
            highlighted?.let {
                withStyle(style = SpanStyle(color = Color(0xFF00FFFF), fontWeight = FontWeight.Bold)) {
                    append(it)
                }
            }
            append(suffix)
        },
        color = Color.White,
        fontSize = 16.sp,
        lineHeight = 22.sp
    )
}
