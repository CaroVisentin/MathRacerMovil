package com.app.mathracer.ui.screens.historyGame

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.R
import com.app.mathracer.ui.screens.game.OptionButtonState
import com.app.mathracer.ui.screens.game.PowerUpChip
import com.app.mathracer.ui.screens.game.TopBar
import com.app.mathracer.ui.screens.game.TrackCard
import com.app.mathracer.ui.screens.game.components.GameResultModal
import com.app.mathracer.ui.screens.historyGame.viewmodel.HistoryGameViewModel

private val PanelColor  = Color(0xE62C2C2C) // #2C2C2C con 90% alpha
private val BorderLight = Color(0x66FFFFFF)
private val NextTeal    = Color(0xFF2EB7A7)
private val GreyTitle   = Color.White.copy(alpha = 0.35f)
private val BgDark        = Color(0xFF222224)
private val CardDark      = Color(0xFF2C2C2C)
private val BorderSoft    = Color(0x66FFFFFF)
private val LabelBlue     = Color(0xFF51B7FF)
private val OptionTeal    = Color(0xFF2EB7A7)

data class PlayerResult(val rank: Int, val name: String, val points: Int)
data class PowerUp(val iconRes: Int, val count: Int, val tint: Color)

@Composable
fun HistoryGameScreen(
    levelId: Int,
    playerName: String = "Jugador",
    resultType: String,
    onNavigateBack: () -> Unit = {},
    onPlayAgain: () -> Unit = {},
    viewModel: HistoryGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    Log.d("resultType", resultType)
    // Inicializar el juego
    LaunchedEffect(levelId, playerName) {
        viewModel.initializeGame(levelId, playerName)
    }

    // Limpiar feedback automáticamente después de mostrar resultado
    LaunchedEffect(uiState.showFeedback) {
        if (uiState.showFeedback) {
            if (uiState.isLastAnswerCorrect == true) {
                kotlinx.coroutines.delay(200)
                viewModel.clearFeedback()
                viewModel.prepareForNextQuestion()
            } else {
                kotlinx.coroutines.delay(1000)
                viewModel.clearFeedback()
            }
        }
    }

    GamePlayScreen(
        timeLabel = "10 seg",
        coins = 123_000,
        rivalTrackRes = R.drawable.track_city,
        youTrackRes = R.drawable.track_cake,
        rivalCarRes = R.drawable.car_game,
        opponentName = uiState.machineName,
        playerName = uiState.playerName,
        youCarRes = R.drawable.car_game,
        powerUps = listOf(
            PowerUp(R.drawable.ic_shield, uiState.fireExtinguisherCount, Color(0xFFFF6B6B))
        ),
        expression = uiState.currentQuestion.ifEmpty { 
            when {
                uiState.isLoading -> "Conectando al juego..."
                uiState.gameEnded -> "¡Juego terminado!"
                uiState.isPenalized -> "⏱️ PENALIZADO - Espera 1 segundo..."
                uiState.selectedOption != null && !uiState.showFeedback -> "Procesando respuesta..."
                uiState.showFeedback -> if (uiState.isLastAnswerCorrect == true) "✅ ¡Correcto!" else "❌ Incorrecto"
                else -> ""
            }
        },
        options = uiState.options.map { it },
        rivalProgress = uiState.machineProgress,
        yourProgress = uiState.playerProgress,
        isWaitingForAnswer = uiState.selectedOption != null && !uiState.showFeedback,
        lastAnswerGiven = uiState.selectedOption,
        lastAnswerWasCorrect = uiState.isLastAnswerCorrect,
        showAnswerFeedback = uiState.showFeedback,
        isPenalized = uiState.isPenalized,
        expectedResult = resultType,
        onBack = onNavigateBack,
        onPowerUpClick = { index -> 
            when (index) {
                0 -> viewModel.useFireExtinguisher()
            }
        },
        onOptionClick = { index, value ->
            if (uiState.currentQuestion.isNotEmpty() && !uiState.isPenalized && !uiState.showFeedback) {
                viewModel.submitAnswer(value)
            }
        }
    )

    // Modal de resultado del juego
    if (uiState.gameEnded) {
        GameResultModal(
            isWinner = uiState.winner?.contains("Ganaste") == true,
            userName = uiState.playerName,
            userNameRival = uiState.machineName,
            onDismiss = { 
                // No necesitamos método específico, el estado ya está manejado
            },
            onPlayAgain = {
                onPlayAgain()
            },
            onBackToHome = {
                onNavigateBack()
            }
        )
    }
}

@Composable
fun GamePlayScreen(
    timeLabel: String = "10 seg",
    coins: Int = 123000,
    rivalTrackRes: Int,
    youTrackRes: Int,
    rivalCarRes: Int,
    opponentName: String,
    playerName: String,
    youCarRes: Int,
    powerUps: List<PowerUp>,
    expression: String = "Y = 13 - X",
    options: List<Int?> = listOf(0, 0, 0, 0),
    rivalProgress: Int = 0,
    yourProgress: Int = 0,
    isWaitingForAnswer: Boolean = false,
    lastAnswerGiven: Int? = null,
    lastAnswerWasCorrect: Boolean? = null,
    showAnswerFeedback: Boolean = false,
    isPenalized: Boolean = false,
    // controla si los botones de opción muestran sombra/relieve
    optionsHaveShadows: Boolean = true,
    expectedResult: String = "",
    onBack: () -> Unit,
    onPowerUpClick: (index: Int) -> Unit,
    onOptionClick: (index: Int, value: Int?) -> Unit,
) {
    Scaffold(
        containerColor = BgDark,
        topBar = {
            TopBar(timeLabel = timeLabel, coins = coins, onBack = onBack)
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top
        ) {
            // ====== TRACK RIVAL ======
            TrackCard(
                title = opponentName,
                titleColor = Color.White.copy(alpha = 0.65f),
                trackRes = rivalTrackRes,
                carRes = rivalCarRes,
                underlineColor = Color(0xFF4BC3FF),
                progress = rivalProgress
            )
            Spacer(Modifier.height(10.dp))

            // ====== TRACK VOS ======
            TrackCard(
                title = playerName,
                titleColor = LabelBlue,
                trackRes = youTrackRes,
                carRes = youCarRes,
                underlineColor = LabelBlue,
                progress = yourProgress
            )

            Spacer(Modifier.height(30.dp))

            // ====== POWER UPS ======

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                powerUps.forEachIndexed { i, p ->
                    PowerUpChip(
                        iconRes = p.iconRes,
                        count = p.count,
                        tint = p.tint,
                        onClick = { onPowerUpClick(i) }
                    )
                    Spacer(Modifier.width(12.dp))
                }
            }

            Spacer(Modifier.height(30.dp))

            // ====== EXPRESIÓN ======
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    .background(CardDark)
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = expression,
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Elegí la opción para que la Y sea",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "${expectedResult.ifEmpty { "" }}",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 36.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(60.dp))

            // ====== OPCIONES (2 x 2) ======
//            Column(
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(12.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    OptionButton(
//                        text = (options.getOrNull(0) ?: "").toString(),
//                        modifier = Modifier.weight(1f),
//                        state = getOptionButtonState(
//                            option = options.getOrNull(0),
//                            lastAnswerGiven = lastAnswerGiven,
//                            lastAnswerWasCorrect = lastAnswerWasCorrect,
//                            showAnswerFeedback = showAnswerFeedback,
//                            isWaitingForAnswer = isWaitingForAnswer,
//                            isPenalized = isPenalized
//                        ),
//                        hasShadow = optionsHaveShadows,
//                        onClick = { onOptionClick(0, options.getOrNull(0)) }
//                    )
//                    OptionButton(
//                        text = (options.getOrNull(1) ?: "").toString(),
//                        modifier = Modifier.weight(1f),
//                        state = getOptionButtonState(
//                            option = options.getOrNull(1),
//                            lastAnswerGiven = lastAnswerGiven,
//                            lastAnswerWasCorrect = lastAnswerWasCorrect,
//                            showAnswerFeedback = showAnswerFeedback,
//                            isWaitingForAnswer = isWaitingForAnswer,
//                            isPenalized = isPenalized
//                        ),
//                        hasShadow = optionsHaveShadows,
//                        onClick = { onOptionClick(1, options.getOrNull(1)) }
//                    )
//                }
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(12.dp),
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    OptionButton(
//                        text = (options.getOrNull(2) ?: "").toString(),
//                        modifier = Modifier.weight(1f),
//                        state = getOptionButtonState(
//                            option = options.getOrNull(2),
//                            lastAnswerGiven = lastAnswerGiven,
//                            lastAnswerWasCorrect = lastAnswerWasCorrect,
//                            showAnswerFeedback = showAnswerFeedback,
//                            isWaitingForAnswer = isWaitingForAnswer,
//                            isPenalized = isPenalized
//                        ),
//                        hasShadow = optionsHaveShadows,
//                        onClick = { onOptionClick(2, options.getOrNull(2)) }
//                    )
//                    OptionButton(
//                        text = (options.getOrNull(3) ?: "").toString(),
//                        modifier = Modifier.weight(1f),
//                        state = getOptionButtonState(
//                            option = options.getOrNull(3),
//                            lastAnswerGiven = lastAnswerGiven,
//                            lastAnswerWasCorrect = lastAnswerWasCorrect,
//                            showAnswerFeedback = showAnswerFeedback,
//                            isWaitingForAnswer = isWaitingForAnswer,
//                            isPenalized = isPenalized
//                        ),
//                        hasShadow = optionsHaveShadows,
//                        onClick = { onOptionClick(3, options.getOrNull(3)) }
//                    )
//                }
//            }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { i, option ->
                    OptionButton(
                        text = option.toString(),
                        modifier = Modifier.fillMaxWidth(),
                        state = getOptionButtonState(
                            option = option?.toInt(),
                            lastAnswerGiven = lastAnswerGiven,
                            lastAnswerWasCorrect = lastAnswerWasCorrect,
                            showAnswerFeedback = showAnswerFeedback,
                            isWaitingForAnswer = isWaitingForAnswer,
                            isPenalized = isPenalized
                        ),
                        totalOptions = options.size,
                        hasShadow = optionsHaveShadows,
                        onClick = { onOptionClick(i, option) }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun OptionsColumn(
    options: List<Int>,
    onOptionClick: (Int, String) -> Unit,
    lastAnswerGiven: Int?,
    lastAnswerWasCorrect: Boolean,
    showAnswerFeedback: Boolean,
    isWaitingForAnswer: Boolean,
    isPenalized: Boolean,
    optionsHaveShadows: Boolean
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Un solo for para crear todos los botones
        for (i in options.indices) {
            val option = options[i]
            OptionButton(
                text = option.toString(),
                modifier = Modifier.fillMaxWidth(),
                state = getOptionButtonState(
                    option = option,
                    lastAnswerGiven = lastAnswerGiven,
                    lastAnswerWasCorrect = lastAnswerWasCorrect,
                    showAnswerFeedback = showAnswerFeedback,
                    isWaitingForAnswer = isWaitingForAnswer,
                    isPenalized = isPenalized
                ),
                totalOptions = options.size,
                hasShadow = optionsHaveShadows,
                onClick = { onOptionClick(i, option.toString()) }
            )
        }
    }
}


private fun getOptionButtonState(
    option: Int?,
    lastAnswerGiven: Int?,
    lastAnswerWasCorrect: Boolean?,
    showAnswerFeedback: Boolean,
    isWaitingForAnswer: Boolean,
    isPenalized: Boolean = false
): OptionButtonState {
    return when {
        isPenalized -> OptionButtonState.DISABLED
        showAnswerFeedback && option == lastAnswerGiven && lastAnswerWasCorrect == true -> OptionButtonState.CORRECT
        showAnswerFeedback && option == lastAnswerGiven && lastAnswerWasCorrect == false -> OptionButtonState.INCORRECT
        isWaitingForAnswer && option == lastAnswerGiven -> OptionButtonState.SELECTED
        else -> OptionButtonState.NORMAL
    }
}

@Composable
fun OptionButton(
    text: String,
    modifier: Modifier = Modifier,
    state: OptionButtonState = OptionButtonState.NORMAL,
    totalOptions: Int,
    hasShadow: Boolean = true,
    onClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp.dp // ancho de pantalla en dp
    val buttonWidth = screenWidthPx / 2 // mitad de la pantalla

    val (backgroundColor, borderColor, symbol) = when (state) {
        OptionButtonState.NORMAL    -> Triple(OptionTeal, Color.White, "")
        OptionButtonState.SELECTED  -> Triple(Color(0xFF1976D2), Color(0xFF64B5F6), "⏳")
        OptionButtonState.CORRECT   -> Triple(Color(0xFF4CAF50), Color(0xFF81C784), "✅")
        OptionButtonState.INCORRECT -> Triple(Color(0xFFF44336), Color(0xFFEF5350), "❌")
        OptionButtonState.DISABLED  -> Triple(Color(0xFF666666), Color(0xFF999999), "⏱️")
    }

    val isEnabled = state == OptionButtonState.NORMAL || state == OptionButtonState.SELECTED
    val shape = RoundedCornerShape(12.dp)

    val btnModifier = modifier
        .then(
            if (hasShadow) {
                Modifier
                    .shadow(elevation = 6.dp, shape = shape, clip = false)
                    .clip(shape)
            } else {
                Modifier.clip(shape)
            }
        )
        .height(56.dp)
        .width(buttonWidth) // <- mitad de la pantalla
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = onClick,
            enabled = isEnabled,
            modifier = btnModifier,
            shape = shape,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = Color.White,
                disabledContainerColor = backgroundColor.copy(alpha = 0.6f),
                disabledContentColor = Color.White.copy(alpha = 0.8f)
            ),
            border = BorderStroke(2.dp, borderColor)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
                if (symbol.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = symbol, fontSize = 18.sp)
                }
            }
        }
    }
}