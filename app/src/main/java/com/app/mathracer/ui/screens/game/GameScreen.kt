package com.app.mathracer.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.mathracer.R

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
fun GamePlayScreen(
    timeLabel: String = "10 seg",
    coins: Int = 123000,
    rivalTrackRes: Int,
    youTrackRes: Int,
    rivalCarRes: Int,
    youCarRes: Int,
    powerUps: List<PowerUp>,
    expression: String = "Y = 13 - X",
    options: List<String>,
    onBack: () -> Unit,
    onPowerUpClick: (index: Int) -> Unit,
    onOptionClick: (index: Int, value: String) -> Unit,
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
                title = "RIVAL",
                titleColor = Color.White.copy(alpha = 0.65f),
                trackRes = rivalTrackRes,
                carRes = rivalCarRes,
                underlineColor = Color(0xFF4BC3FF)
            )
            Spacer(Modifier.height(10.dp))

            // ====== TRACK VOS ======
            TrackCard(
                title = "VOS",
                titleColor = LabelBlue,
                trackRes = youTrackRes,
                carRes = youCarRes,
                underlineColor = LabelBlue
            )

            Spacer(Modifier.height(30.dp))

            // ====== POWER UPS ======
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
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
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Elegí la opción para que la Y sea MAYOR",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(60.dp))

            // ====== OPCIONES (2 x 2) ======
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OptionButton(
                        text = options.getOrNull(0) ?: "",
                        modifier = Modifier.weight(1f),
                        onClick = { onOptionClick(0, options.getOrNull(0) ?: "") }
                    )
                    OptionButton(
                        text = options.getOrNull(1) ?: "",
                        modifier = Modifier.weight(1f),
                        onClick = { onOptionClick(1, options.getOrNull(1) ?: "") }
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OptionButton(
                        text = options.getOrNull(2) ?: "",
                        modifier = Modifier.weight(1f),
                        onClick = { onOptionClick(2, options.getOrNull(2) ?: "") }
                    )
                    OptionButton(
                        text = options.getOrNull(3) ?: "",
                        modifier = Modifier.weight(1f),
                        onClick = { onOptionClick(3, options.getOrNull(3) ?: "") }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopBar(timeLabel: String, coins: Int, onBack: () -> Unit) {
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
        Text(
            text = timeLabel,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        // Indicadores / monedas (simplificado)
        Spacer(Modifier.width(6.dp))
        // Icono de combustible
        Icon(
            painter = painterResource(R.drawable.ic_fuel), // tu drawable
            contentDescription = "Combustible",
            tint = Color(0xFFFFD600),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))

        // Indicadores (2 amarillos + 1 gris)
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(Color(0xFFFFD600), RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(Color(0xFFFFD600), RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(Color(0xFF555555), RoundedCornerShape(4.dp))
        )
    }
}

@Composable
private fun TrackCard(
    title: String,
    titleColor: Color,
    trackRes: Int,
    carRes: Int,
    underlineColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(2.dp, BorderSoft, RoundedCornerShape(14.dp))
            .background(CardDark)
    ) {
        // Imagen de pista con auto
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(trackRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 8.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
            ) {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Auto
            Image(
                painter = painterResource(carRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .height(48.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Subrayado de color
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(underlineColor)
        )
    }
}

@Composable
private fun PowerUpChip(
    iconRes: Int,
    count: Int,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .shadow(6.dp, RoundedCornerShape(10.dp), clip = true)
            .clip(RoundedCornerShape(10.dp))
            .background(BgDark)
            .border(2.dp, Color.White, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
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
            color = tint,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
private fun OptionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = OptionTeal,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, Color.White)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/** Modal: Perdiste + Resultados */
@Composable
fun ResultsModal(
    open: Boolean,
    results: List<PlayerResult>,      // ej: 2 jugadores
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
    onNavigateBack: () -> Unit = {},
    onGameFinished: () -> Unit = {}
) {
    var showResults by remember { mutableStateOf(false) }
    
    // Datos de ejemplo para los resultados
    val resultsData = listOf(
        PlayerResult(1, "Tú", 2500),
        PlayerResult(2, "Rival", 1750)
    )

    // Modal de resultados
    if (showResults) {
        ResultsModal(
            open = true,
            results = resultsData,
            carImageRes = R.drawable.car,
            medalGoldRes = R.drawable.medal_gold,
            medalSilverRes = R.drawable.medal_silver,
            onBack = {
                showResults = false
                onNavigateBack()
            },
            onReplay = { 
                showResults = false
                // Aquí podrías reiniciar el juego o navegar a una nueva partida
            },
            onDismiss = {
                showResults = false
                onNavigateBack()
            }
        )
    }

    GamePlayScreen(
        timeLabel = "10 seg",
        coins = 123_000,
        rivalTrackRes = R.drawable.track_day,
        youTrackRes = R.drawable.track_night,
        rivalCarRes = R.drawable.car,
        youCarRes = R.drawable.car,
        powerUps = listOf(
            PowerUp(R.drawable.ic_shield, 99, Color(0xFFFF6B6B)),
            PowerUp(R.drawable.ic_shuffle, 99, Color.White),
            PowerUp(R.drawable.ic_bolt, 99, Color(0xFF76E4FF))
        ),
        expression = "Y = 13 - X",
        options = listOf("3", "6", "8", "10"),
        onBack = onNavigateBack,
        onPowerUpClick = { /* usar powerup[it] */ },
        onOptionClick = { index, value ->
            // Al seleccionar una opción, mostrar el modal de resultados
            showResults = true
        }
    )
}
