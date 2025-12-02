package com.app.mathracer.ui.screens.infinite

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.mathracer.ui.screens.infinite.viewmodel.InfiniteGameViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.draw.clip
import com.app.mathracer.R
import com.app.mathracer.ui.screens.game.InfiniteTrackCard
import com.app.mathracer.ui.screens.game.TopBar
import com.app.mathracer.ui.screens.game.TrackCard
import com.app.mathracer.ui.screens.game.PowerUpChip
import com.app.mathracer.ui.screens.game.OptionButton
import com.app.mathracer.ui.screens.game.getOptionButtonState

@Composable
fun InfiniteGameScreen(
    gameId: String,
    onExit: () -> Unit,
    youTrackRes: Int = R.drawable.track_day,
    youCarRes: Int = R.drawable.car,
    powerUps: List<com.app.mathracer.ui.screens.game.PowerUp> = emptyList(),
) {
    val vm: InfiniteGameViewModel = viewModel()
    val uiState by vm.state.collectAsState()

    LaunchedEffect(gameId) {
        vm.start(gameId)
    }

    
    BackHandler {
        vm.abandonGame()
    }
    val abandonResult by vm.abandonResult.collectAsState()

    Scaffold(
        containerColor = Color(0xFF222224),
        topBar = {
            TopBar(timeLabel = "", coins = 0, onBack = { vm.abandonGame() })
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Top
        ) {
            InfiniteTrackCard(
                title = uiState.playerName.ifEmpty { "Jugador" },
                titleColor = Color(0xFF51B7FF),
                trackRes = uiState.playerTrackProductId ?: youTrackRes,
                carRes = youCarRes,
                carProductId = uiState.playerCarProductId,
                trackProductId = uiState.playerTrackProductId,
                underlineColor = Color(0xFF51B7FF),
                progress = uiState.yourProgress,
                totalAnswered = uiState.totalAnswered
            )

            Spacer(Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                powerUps.forEachIndexed { i, p ->
                    PowerUpChip(iconRes = p.iconRes, count = p.count, tint = p.tint, onClick = { })
                    Spacer(Modifier.height(12.dp))
                }
            }

            Spacer(Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                    .background(Color(0xFF2C2C2C))
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.expression.ifEmpty { "" },
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
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
                text = uiState.expectedResult,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 36.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(60.dp))

            val optionIndices = uiState.options.mapIndexedNotNull { idx, v -> if (v != null) idx else null }
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                when (optionIndices.size) {
                    2 -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            optionIndices.forEach { idx ->
                                OptionButton(
                                    text = (uiState.options.getOrNull(idx) ?: "").toString(),
                                    modifier = Modifier.weight(1f),
                                    state = getOptionButtonState(
                                        option = uiState.options.getOrNull(idx),
                                        lastAnswerGiven = uiState.lastAnswerGiven,
                                        lastAnswerWasCorrect = uiState.lastAnswerWasCorrect,
                                        showAnswerFeedback = uiState.showAnswerFeedback,
                                        isWaitingForAnswer = uiState.isWaitingForAnswer,
                                        isPenalized = uiState.isPenalized
                                    ),
                                    hasShadow = true,
                                    onClick = { vm.submitAnswer(idx) }
                                )
                            }
                        }
                    }
                    3 -> {
        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OptionButton(
                                text = (uiState.options.getOrNull(optionIndices[0]) ?: "").toString(),
                                modifier = Modifier.weight(1f),
                                state = getOptionButtonState(
                                    option = uiState.options.getOrNull(optionIndices[0]),
                                    lastAnswerGiven = uiState.lastAnswerGiven,
                                    lastAnswerWasCorrect = uiState.lastAnswerWasCorrect,
                                    showAnswerFeedback = uiState.showAnswerFeedback,
                                    isWaitingForAnswer = uiState.isWaitingForAnswer,
                                    isPenalized = uiState.isPenalized
                                ),
                                hasShadow = true,
                                onClick = { vm.submitAnswer(optionIndices[0]) }
                            )
                            OptionButton(
                                text = (uiState.options.getOrNull(optionIndices[1]) ?: "").toString(),
                                modifier = Modifier.weight(1f),
                                state = getOptionButtonState(
                                    option = uiState.options.getOrNull(optionIndices[1]),
                                    lastAnswerGiven = uiState.lastAnswerGiven,
                                    lastAnswerWasCorrect = uiState.lastAnswerWasCorrect,
                                    showAnswerFeedback = uiState.showAnswerFeedback,
                                    isWaitingForAnswer = uiState.isWaitingForAnswer,
                                    isPenalized = uiState.isPenalized
                                ),
                                hasShadow = true,
                                onClick = { vm.submitAnswer(optionIndices[1]) }
                            )
                        }

                       
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OptionButton(
                                text = (uiState.options.getOrNull(optionIndices[2]) ?: "").toString(),
                                modifier = Modifier.fillMaxWidth(0.5f),
                                state = getOptionButtonState(
                                    option = uiState.options.getOrNull(optionIndices[2]),
                                    lastAnswerGiven = uiState.lastAnswerGiven,
                                    lastAnswerWasCorrect = uiState.lastAnswerWasCorrect,
                                    showAnswerFeedback = uiState.showAnswerFeedback,
                                    isWaitingForAnswer = uiState.isWaitingForAnswer,
                                    isPenalized = uiState.isPenalized
                                ),
                                hasShadow = true,
                                onClick = { vm.submitAnswer(optionIndices[2]) }
                            )
                        }
                    }
                    else -> {
                       
                        if (optionIndices.isEmpty()) {
                         
                        } else {
                             
                            val firstRow = optionIndices.take(2)
                            val secondRow = optionIndices.drop(2).take(2)

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                firstRow.forEach { idx ->
                                    OptionButton(
                                        text = (uiState.options.getOrNull(idx) ?: "").toString(),
                                        modifier = Modifier.weight(1f),
                                        state = getOptionButtonState(
                                            option = uiState.options.getOrNull(idx),
                                            lastAnswerGiven = uiState.lastAnswerGiven,
                                            lastAnswerWasCorrect = uiState.lastAnswerWasCorrect,
                                            showAnswerFeedback = uiState.showAnswerFeedback,
                                            isWaitingForAnswer = uiState.isWaitingForAnswer,
                                            isPenalized = uiState.isPenalized
                                        ),
                                        hasShadow = true,
                                        onClick = { vm.submitAnswer(idx) }
                                    )
                                }
                                
                                if (firstRow.size == 1) Spacer(modifier = Modifier.weight(1f))
                            }

                            if (secondRow.isNotEmpty()) {
                                Spacer(Modifier.height(12.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    secondRow.forEach { idx ->
                                        OptionButton(
                                            text = (uiState.options.getOrNull(idx) ?: "").toString(),
                                            modifier = Modifier.weight(1f),
                                            state = getOptionButtonState(
                                                option = uiState.options.getOrNull(idx),
                                                lastAnswerGiven = uiState.lastAnswerGiven,
                                                lastAnswerWasCorrect = uiState.lastAnswerWasCorrect,
                                                showAnswerFeedback = uiState.showAnswerFeedback,
                                                isWaitingForAnswer = uiState.isWaitingForAnswer,
                                                isPenalized = uiState.isPenalized
                                            ),
                                            hasShadow = true,
                                            onClick = { vm.submitAnswer(idx) }
                                        )
                                    }
                                    if (secondRow.size == 1) Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }

     
    if (abandonResult != null) {
        AbandonResultDialog(
            result = abandonResult!!,
            onDismiss = {
                onExit()
            }
        )
    }
}

@Composable
fun AbandonResultDialog(result: com.app.mathracer.ui.screens.infinite.viewmodel.InfiniteAbandonResult, onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E1E1E))
                    .border(2.dp, Color.White, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Text(
                        text = "X",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onDismiss() }
                    )

                    Text(
                        text = "Resultado de la partida",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Jugador: ${result.playerName}", color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(text = "Tiempo jugado: ${result.timePlayed}", color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text(text = "Respuestas correctas: ${result.totalCorrectAnswers}", color = Color.White)
                    }

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(30.dp))
                            .padding(horizontal = 32.dp, vertical = 10.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Color.White),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "OK", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
