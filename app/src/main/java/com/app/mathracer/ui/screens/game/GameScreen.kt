package com.app.mathracer.ui.screens.game

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.ui.screens.game.viewmodel.GameViewModel
import com.app.mathracer.ui.screens.game.viewmodel.GameUiState
import com.app.mathracer.ui.screens.game.components.GameResultModal
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.mathracer.R
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import com.app.mathracer.ui.components.ProductImage
import androidx.compose.runtime.DisposableEffect
import com.app.mathracer.data.CurrentUser

private val PanelColor  = Color(0xE62C2C2C)
private val BorderLight = Color(0x66FFFFFF)
private val NextTeal    = Color(0xFF2EB7A7)
private val GreyTitle   = Color.White.copy(alpha = 0.35f)
private val BgDark        = Color(0xFF222224)
private val CardDark      = Color(0xFF2C2C2C)
private val BorderSoft    = Color(0x66FFFFFF)
private val LabelBlue     = Color(0xFF51B7FF)
private val OptionTeal    = Color(0xFF2EB7A7)

data class PlayerResult(val rank: Int, val name: String, val points: Int)
data class PowerUp(val iconRes: Int, val count: Int, val tint: Color, val enabled: Boolean = true)

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
    optionsHaveShadows: Boolean = true,
    expectedResult: String = "",
    onBack: () -> Unit,
    onPowerUpClick: (index: Int) -> Unit,
    onOptionClick: (index: Int, value: Int?) -> Unit,
    showShuffleMessage: Boolean = false,
    totalQuestions: Int
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
            TrackCard(
                title = opponentName,
                titleColor = Color.White.copy(alpha = 0.65f),
                trackRes = rivalTrackRes,
                carRes = rivalCarRes,
                underlineColor = Color(0xFF4BC3FF),
                progress = rivalProgress,
                totalQuestions = totalQuestions
            )
            Spacer(Modifier.height(10.dp))

            TrackCard(
                title = playerName,
                titleColor = LabelBlue,
                trackRes = youTrackRes,
                carRes = youCarRes,
                underlineColor = LabelBlue,
                progress = yourProgress,
                totalQuestions = totalQuestions
            )

            Spacer(Modifier.height(30.dp))

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
                        onClick = { onPowerUpClick(i) },
                        enabled = p.enabled
                    )
                    Spacer(Modifier.width(12.dp))
                }
            }

            if (showShuffleMessage) {
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Se han mezclado las opciones de la ecuación del rival.",
                        color = Color.Cyan,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(22.dp))
            } else {
                Spacer(Modifier.height(30.dp))
            }

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


            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val optionRows = options.chunked(2)
                optionRows.forEachIndexed { rowIndex, rowOptions ->
                    if (rowOptions.size == 2) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            rowOptions.forEachIndexed { i, opt ->
                                val globalIndex = rowIndex * 2 + i
                                OptionButton(
                                    text = (opt ?: "").toString(),
                                    modifier = Modifier.weight(1f),
                                    state = getOptionButtonState(
                                        option = opt,
                                        lastAnswerGiven = lastAnswerGiven,
                                        lastAnswerWasCorrect = lastAnswerWasCorrect,
                                        showAnswerFeedback = showAnswerFeedback,
                                        isWaitingForAnswer = isWaitingForAnswer,
                                        isPenalized = isPenalized
                                    ),
                                    hasShadow = optionsHaveShadows,
                                    onClick = { onOptionClick(globalIndex, opt) }
                                )
                            }
                        }
                    } else if (rowOptions.size == 1) {

                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            val opt = rowOptions[0]
                            val globalIndex = rowIndex * 2
                            OptionButton(
                                text = (opt ?: "").toString(),
                                modifier = Modifier.fillMaxWidth(0.6f),
                                state = getOptionButtonState(
                                    option = opt,
                                    lastAnswerGiven = lastAnswerGiven,
                                    lastAnswerWasCorrect = lastAnswerWasCorrect,
                                    showAnswerFeedback = showAnswerFeedback,
                                    isWaitingForAnswer = isWaitingForAnswer,
                                    isPenalized = isPenalized
                                ),
                                hasShadow = optionsHaveShadows,
                                onClick = { onOptionClick(globalIndex, opt) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
public fun TopBar(timeLabel: String, coins: Int, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgDark)
            .padding(top = 50.dp, bottom = 10.dp, start = 8.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
        }
        Spacer(Modifier.width(4.dp))
    }
}

@Composable
public fun TrackCard(
    title: String,
    titleColor: Color,
    trackRes: Int,
    carRes: Int,
    underlineColor: Color,
    progress: Int = 0,
    totalQuestions: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, BorderSoft, RoundedCornerShape(14.dp))
            .background(CardDark)
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            ScrollingTrack(
                trackRes = trackRes,
                height = 120.dp,
                speedDpPerSec = 90.dp
            )

            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "$title ($progress/$totalQuestions)",
                    color = titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            val progress01 = (progress.toFloat() / totalQuestions.toFloat()).coerceIn(0f, 1f)
            val animated by animateFloatAsState(progress01, label = "carProgress")

            val startMargin = 12.dp
            val endMargin   = 12.dp
            val carWidth    = 72.dp
            val carHeight   = 48.dp

            val density = LocalDensity.current
            val offsetX = remember(maxWidth, animated) {
                with(density) {
                    val travelPx = (maxWidth - startMargin - endMargin - carWidth).toPx()
                    (travelPx * animated).toDp()
                }
            }

            ProductImage(
                productId = carRes,
                fallbackRes = R.drawable.car_game,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = startMargin + offsetX, y = 0.dp)
                    .padding(bottom = 12.dp)
                    .size(width = carWidth, height = carHeight),
                contentScale = ContentScale.Fit
            )

        /*
            Image(
                painter =  painterResource(carRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = startMargin + offsetX, y = 0.dp)
                    .padding(bottom = 12.dp)
                    .size(width = carWidth, height = carHeight),
                contentScale = ContentScale.Fit
            )

         */
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        fraction = (progress.toFloat() / totalQuestions.toFloat()).coerceIn(0f, 1f)
                    )
                    .height(6.dp)
                    .background(underlineColor)
            )
        }
    }
}
@Composable
fun ScrollingTrack(
    trackRes: Int,
    height: Dp = 120.dp,
    speedDpPerSec: Dp = 90.dp,
    corner: Dp = 10.dp
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(corner))
    ) {
        val density = LocalDensity.current

        val containerWidth: Dp = this.maxWidth
        val containerWidthPx = with(density) { containerWidth.toPx() }
        val speedPxPerSec = with(density) { speedDpPerSec.toPx() }

        val duration = ((containerWidthPx / speedPxPerSec) * 1000f).toInt()

        val t = rememberInfiniteTransition(label = "scroll")
        val x by t.animateFloat(
            initialValue = 0f,
            targetValue = containerWidthPx,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "xAnim"
        )

        val offsetPx = x % containerWidthPx

        Box(Modifier.fillMaxSize()) {

            ProductImage(
                productId = trackRes,
                fallbackRes = R.drawable.track_city,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(containerWidth)
                    .offset { IntOffset((-offsetPx).roundToInt(), 0) },
                contentScale = ContentScale.FillHeight
            )

            ProductImage(
                productId = trackRes,
                fallbackRes = R.drawable.track_city,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(containerWidth)
                    .offset { IntOffset((containerWidthPx - offsetPx).roundToInt(), 0) },
                contentScale = ContentScale.FillHeight
            )
        }
    }
}

@Composable
public fun PowerUpChip(
    iconRes: Int,
    count: Int,
    tint: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val chipModifier = Modifier
        .shadow(6.dp, RoundedCornerShape(10.dp), clip = true)
        .clip(RoundedCornerShape(10.dp))
        .background(if (enabled) BgDark else Color(0xFF6B6B6B))
        .border(2.dp, if (enabled) Color.White else Color.DarkGray, RoundedCornerShape(10.dp))
        .then(if (enabled) Modifier.clickable { onClick() } else Modifier)
        .padding(horizontal = 10.dp, vertical = 6.dp)

    Row(
        modifier = chipModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.Black.copy(alpha = 0.35f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(6.dp))
        Text(
            text = count.toString(),
            color = if (enabled) tint else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
    }
}

enum class OptionButtonState {
    NORMAL, SELECTED, CORRECT, INCORRECT, DISABLED
}

fun getOptionButtonState(
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
    hasShadow: Boolean = true,
    onClick: () -> Unit
) {
    val (backgroundColor, borderColor) = when (state) {
        OptionButtonState.NORMAL -> Pair(OptionTeal, Color.White)
        OptionButtonState.SELECTED -> Pair(Color(0xFF1976D2), Color(0xFF64B5F6))
        OptionButtonState.CORRECT -> Pair(Color(0xFF4CAF50), Color(0xFF81C784))
        OptionButtonState.INCORRECT -> Pair(Color(0xFFF44336), Color(0xFFEF5350))
        OptionButtonState.DISABLED -> Pair(Color(0xFF666666), Color(0xFF999999))
    }

    val btnModifier = if (hasShadow) modifier
        .height(56.dp)
        .shadow(6.dp, RoundedCornerShape(12.dp))
    else modifier.height(56.dp)

    val isClickable = state == OptionButtonState.NORMAL || state == OptionButtonState.SELECTED

    Button(
        onClick = { if (isClickable) onClick() },
        modifier = btnModifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White,
            disabledContainerColor = backgroundColor,
            disabledContentColor = Color.White
        ),
        border = BorderStroke(2.dp, borderColor),
        enabled = true
    ) {
        Text(
            text = text,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
    }
}

@Composable
fun ResultsModal(
    open: Boolean,
    results: List<PlayerResult>,
    carImageRes: Int,
    medalGoldRes: Int,
    medalSilverRes: Int,
    onBack: () -> Unit,
    onReplay: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!open) return

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = PanelColor,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(2.dp, BorderLight),
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 300.dp)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("PERDISTE", color = GreyTitle, fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.5.sp)

                Spacer(Modifier.height(4.dp))

                Text("RESULTADOS", color = Color.White, fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.1.sp)

                Spacer(Modifier.height(10.dp))
                Divider(thickness = 1.dp, color = Color.White.copy(alpha = 0.15f))
                Spacer(Modifier.height(10.dp))

                results.take(2).forEachIndexed { idx, r ->
                    ResultRow(
                        result = r,
                        carImageRes = carImageRes,
                        medalRes = if (r.rank == 1) medalGoldRes else medalSilverRes
                    )
                    if (idx == 0) Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = PanelColor,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(2.dp, BorderLight),
                        shape = RoundedCornerShape(10.dp)
                    ) { Text("REGRESAR", fontWeight = FontWeight.Bold) }

                    Button(
                        onClick = onReplay,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NextTeal,
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
    result: PlayerResult,
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
            Text(result.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("Puntos:  ${result.points}", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
        }

        Image(
            painter = painterResource(carImageRes),
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun GameScreen(
    gameId: String,
    playerName: String = "Jugador",
    onNavigateBack: () -> Unit = {},
    onPlayAgain: () -> Unit = {},
    viewModel: GameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(gameId, playerName) {
        viewModel.initializeGame(gameId, CurrentUser.user?.name.toString())
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.leaveCurrentGame()
        }
    }

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
        rivalTrackRes = uiState.opponentTrackRes,
        youTrackRes =  uiState.playerTrackRes,
        rivalCarRes = uiState.opponentCarRes,
        opponentName = uiState.opponentName,
        playerName = uiState.playerName,
        youCarRes = uiState.playerCarRes,
        powerUps = listOf(
            PowerUp(
                R.drawable.ic_shield,
                uiState.fireExtinguisherCount,
                Color(0xFFFF6B6B),
                enabled = uiState.fireExtinguisherCount > 0 && !uiState.powerUpsLocked && !uiState.fireExtinguisherActive
            ),
            PowerUp(
                R.drawable.ic_shuffle,
                uiState.shuffleRivalCount,
                Color.White,
                enabled = uiState.shuffleRivalCount > 0 && !uiState.powerUpsLocked
            ),
            PowerUp(
                R.drawable.ic_bolt,
                uiState.doublePointsCount,
                Color(0xFF76E4FF),
                enabled = uiState.doublePointsCount > 0 && !uiState.powerUpsLocked && !uiState.doubleProgressActive
            )
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
        options = uiState.options,
        rivalProgress = uiState.opponentProgress,
        yourProgress = uiState.playerProgress,
        isWaitingForAnswer = uiState.selectedOption != null && !uiState.showFeedback,
        lastAnswerGiven = uiState.selectedOption,
        lastAnswerWasCorrect = uiState.isLastAnswerCorrect,
        showAnswerFeedback = uiState.showFeedback,
        isPenalized = uiState.isPenalized,
        expectedResult = uiState.expectedResult,
        showShuffleMessage = uiState.showShuffleMessage,
        onBack = {
            viewModel.leaveCurrentGame()
            onNavigateBack()
        },
        onPowerUpClick = { index -> 
            when (index) {
                0 -> viewModel.useFireExtinguisher()
                1 -> viewModel.usePowerUp(2)
                2 -> viewModel.usePowerUp(1)
            }
        },
        onOptionClick = { index, value ->
            if (uiState.currentQuestion.isNotEmpty() && !uiState.isPenalized && !uiState.showFeedback) {
                viewModel.submitAnswer(value)
            }
        },
        totalQuestions = uiState.totalQuestions
    )

    if (uiState.gameEnded) {
        GameResultModal(
            isWinner = uiState.winner?.contains("Ganaste") == true,
            userName = uiState.playerName,
            userNameRival = uiState.opponentName,
            userCarProductId = uiState.playerCarRes.takeIf { it > 0 },
            rivalCarProductId = uiState.opponentCarRes.takeIf { it > 0 },
            onDismiss = {
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
