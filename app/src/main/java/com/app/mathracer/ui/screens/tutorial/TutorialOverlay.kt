package com.app.mathracer.ui.screens.tutorial


import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.ui.screens.game.GamePlayScreen

@Composable
fun TutorialOverlay(
    onFinish: () -> Unit
) {
    val context = LocalContext.current

     
    var step by remember { mutableStateOf(-1) }

    var rivalProgress by remember { mutableStateOf(2) }
    var yourProgress by remember { mutableStateOf(0) }
    val expression = "Y = 15 - X"
    val options = listOf(5, 2, 1, 10)

    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    var lastAnswerWasCorrect by remember { mutableStateOf<Boolean?>(null) }
    var isWaitingForAnswer by remember { mutableStateOf(false) }
    var isPenalized by remember { mutableStateOf(false) }
    var preselectedValue by remember { mutableStateOf<Int?>(null) }

    val incorrectValue = 5
    val correctValue = 1

    fun clearTutorialFlag(ctx: Context) {
        try {
            val prefs = ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("show_tutorial_on_next_launch", false).apply()
        } catch (t: Throwable) {
             
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()
        val configuration = LocalConfiguration.current

        
        var showScale by remember { mutableStateOf(false) }
        LaunchedEffect(step) {
            showScale = false
            kotlinx.coroutines.delay(20)
            showScale = true
        }
        val scale by animateFloatAsState(targetValue = if (showScale) 1f else 0.9f, animationSpec = tween(260))

        
        GamePlayScreen(
            timeLabel = "10 seg",
            coins = 0,
            rivalTrackRes = com.app.mathracer.R.drawable.track_city,
            youTrackRes = com.app.mathracer.R.drawable.track_cake,
            rivalCarRes = com.app.mathracer.R.drawable.car_game,
            opponentName = "RIVAL",
            playerName = "VOS",
            youCarRes = com.app.mathracer.R.drawable.car_game,
            powerUps = listOf(
                com.app.mathracer.ui.screens.game.PowerUp(com.app.mathracer.R.drawable.ic_shield, 99, Color(0xFFFF6B6B)),
            ),
            expression = expression,
            options = options,
            rivalProgress = rivalProgress,
            yourProgress = yourProgress,
            isWaitingForAnswer = isWaitingForAnswer,
            lastAnswerGiven = selectedOption,
            lastAnswerWasCorrect = lastAnswerWasCorrect,
            showAnswerFeedback = showFeedback,
            isPenalized = isPenalized,
            // desactivar sombras en opciones durante tutorial
            optionsHaveShadows = false,
            expectedResult = "",
            onBack = { },
            onPowerUpClick = { },
            onOptionClick = { index, value ->
                if (value == null) return@GamePlayScreen

                when (step) {
                    4 -> {
                        if (!showFeedback && !isPenalized && value == incorrectValue) {
                            selectedOption = value
                            isWaitingForAnswer = false
                            scope.launch {
                                kotlinx.coroutines.delay(400)
                                showFeedback = true
                                lastAnswerWasCorrect = false
                                rivalProgress = (rivalProgress + 2).coerceAtMost(10)
                                step = 5
                            }
                        }
                    }
                    6 -> {
                        if (!showFeedback && !isPenalized && value == correctValue) {
                            selectedOption = value
                            isWaitingForAnswer = false
                            scope.launch {
                                kotlinx.coroutines.delay(400)
                                showFeedback = true
                                lastAnswerWasCorrect = true
                                yourProgress = (yourProgress + 3).coerceAtMost(10)
                                step = 7
                            }
                        }
                    }
                    else -> {
                       
                    }
                }
            }
        )

         
        if (step == -1 || step >= 0) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f)))
        }

        
        if (step == -1) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 360.dp)
                        .background(Color(0xFF0F6F6F), RoundedCornerShape(14.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "TUTORIAL", color = Color.White, fontSize = 24.sp, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    Image(painter = painterResource(id = com.app.mathracer.R.drawable.mathi), contentDescription = "mascota", modifier = Modifier.size(140.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(text = "Hola, ¡Soy Mathi! Y te voy a enseñar cómo jugar a mi juego", color = Color.White, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { step = 0 },
                        modifier = Modifier
                            .height(56.dp)
                            .shadow(6.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2EB7A7),
                            contentColor = Color.White
                        ),
                        border = BorderStroke(2.dp, Color.White)
                    ) {
                        Text("Siguiente", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
            return@Box
        }

        
        val dialogOffsetY = when (step) {
            0 -> (-300).dp
            1 -> (-40).dp
            2 -> (-180).dp
            3 -> (-120).dp
            4,5,6,7 -> 80.dp
            else -> 0.dp
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = dialogOffsetY)
                .padding(20.dp)
                .background(Color(0xFF0F6F6F), RoundedCornerShape(12.dp))
                .padding(14.dp)
                .scale(scale)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val titleText = when (step) {
                    0 -> "Tiempo"
                    1 -> "Carrera"
                    2 -> "Comodines"
                    3 -> "Objetivo"
                    4,5,6,7 -> "Juguemos"
                    8 -> "¡Bien hecho!"
                    else -> ""
                }
                val summaryText = when (step) {
                    0 -> "Tendrás tiempo para contestar en cada ecuación"
                    1 -> "En este sector verás tu avance y el de tu rival"
                    2 -> "Podrás tener ayudas activables durante el juego"
                    3 -> "Verás la ecuación a resolver y la condición debajo"
                    4 -> "Selecciona la opción marcada"
                    5 -> "Opción incorrecta y tu rival te ha adelantado"
                    6 -> "Ahora selecciona la opción correcta para recuperar terreno"
                    7 -> "Opción correcta, ¡bien hecho!"
                    8 -> "¡Completaste el tutorial!. Ya estás listo para salir a la pista"
                    else -> ""
                }

                Text(text = titleText, color = Color.White, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))
                Text(text = summaryText, color = Color.White.copy(alpha = 0.95f), fontSize = 14.sp)
                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (step > 0) {
                        OutlinedButton(
                            onClick = { step = (step - 1).coerceAtLeast(-1) },
                            modifier = Modifier.height(56.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF2C2C2C),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Atras", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    when (step) {
                        in 0..3 -> Button(
                            onClick = { step = (step + 1).coerceAtMost(4) },
                            modifier = Modifier
                                .height(56.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2EB7A7),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Color.White)
                        ) { Text("Siguiente", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) }
                        5 -> Button(
                            onClick = {
                                step = 6
                                selectedOption = null
                                showFeedback = false
                                lastAnswerWasCorrect = null
                                isPenalized = false
                                preselectedValue = correctValue
                                isWaitingForAnswer = true
                            },
                            modifier = Modifier
                                .height(56.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2EB7A7),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Color.White)
                        ) { Text("Siguiente", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) }
                        7 -> Button(
                            onClick = { step = 8 },
                            modifier = Modifier
                                .height(56.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2EB7A7),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Color.White)
                        ) { Text("Siguiente", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) }
                        8 -> Button(
                            onClick = {
                                clearTutorialFlag(context)
                                onFinish()
                            },
                            modifier = Modifier
                                .height(56.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2EB7A7),
                                contentColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Color.White)
                        ) { Text("Finalizar", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) }
                        else -> {}
                    }
                }
            }
        }

        
        LaunchedEffect(step) {
            if (step == 4) {
                preselectedValue = options.getOrNull(0)
                selectedOption = preselectedValue
                isWaitingForAnswer = true
                showFeedback = false
                lastAnswerWasCorrect = null
                isPenalized = false
            } else if (step == 6) {
                selectedOption = preselectedValue
                isWaitingForAnswer = true
                showFeedback = false
                lastAnswerWasCorrect = null
                isPenalized = false
            } else {
                preselectedValue = null
                selectedOption = null
                isWaitingForAnswer = false
            }
        }

    }
}

