package com.app.mathracer.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.app.mathracer.ui.screens.tutorial.TutorialOverlay
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.R
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.ui.theme.CyanMR

@Composable
fun HomeScreen(
    userName: String? = null,
    userEmail: String? = null,
    onMultiplayerClick: () -> Unit = {},
    onStoryModeClick: () -> Unit = {},
    onFreePracticeClick: () -> Unit = {},
    onShopClick: () -> Unit = {},
    onGarageClick: () -> Unit = {},
    onStatsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTutorialComplete: () -> Unit = {},
    viewModel: com.app.mathracer.ui.screens.home.viewmodel.HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val _context = LocalContext.current
    var showTutorial by remember { mutableStateOf(false) }
    Log.d("energy", uiState.energy.toString())
    //  Carga inicial de energía al entrar
    LaunchedEffect(Unit) {
        viewModel.onEnterHome()
        try {
            val prefs = _context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            showTutorial = prefs.getBoolean("show_tutorial_on_next_launch", false)
        } catch (_: Throwable) {
            showTutorial = false
        }
    }

    //  Refrescar al volver al foco
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                viewModel.onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Navegación a waiting cuando VM lo pida
    LaunchedEffect(uiState.navigateToWaiting) {
        if (uiState.navigateToWaiting) {
            viewModel.clearNavigation()
            onMultiplayerClick()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column {
                Scaffold(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    topBar = {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 40.dp
                                )
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = null,
                                    modifier = Modifier.width(150.dp)
                                )
                                Spacer(modifier = Modifier.weight(1f))

                                //                            // Panel HUD (energía + avatar)
                                //                            Row(
                                //                                modifier = Modifier
                                //                                    .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                //                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                //                                verticalAlignment = Alignment.CenterVertically,
                                //                                horizontalArrangement = Arrangement.spacedBy(24.dp)
                                //                            ) {
                                //                                // --- Lado izquierdo: batería / energía desde VM ---
                                //                                Column(horizontalAlignment = Alignment.End) {
                                //                                    RechargeStatus(
                                //                                        secondsUntilNextRecharge = uiState.energy.secondsLeft,
                                //                                        currentAmount = uiState.energy.currentAmount,
                                //                                        maxAmount = uiState.energy.maxAmount,
                                //                                        batteryBoltRes = R.drawable.ic_battery_bolt,
                                //                                        cellFilledRes = R.drawable.ic_cell_filled,
                                //                                        cellEmptyRes = R.drawable.ic_cell_empty
                                //                                    )
                                //                                }
                                //
                                //                                // --- Lado derecho: avatar + nombre ---
                                //                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                //                                    Image(
                                //                                        painter = painterResource(R.drawable.avatar),
                                //                                        contentDescription = "avatar",
                                //                                        contentScale = ContentScale.Crop,
                                //                                        modifier = Modifier
                                //                                            .size(60.dp)
                                //                                            .clip(CircleShape)
                                //                                            .clickable { onProfileClick() }
                                //                                    )
                                //
                                //                                    Text(
                                //                                        text = CurrentUser.user?.name ?: "",
                                //                                        color = Color.White,
                                //                                        fontSize = 24.sp,
                                //                                        fontWeight = FontWeight.SemiBold,
                                //                                        textAlign = TextAlign.Center,
                                //                                        modifier = Modifier.padding(top = 4.dp)
                                //                                    )
                                //                                }
                                // ---- HUD tipo mockup: monedas, tickets, energía ----
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Monedas
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = R.drawable.coin), // poné tu asset
                                            contentDescription = "Monedas",
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "${CurrentUser.user?.coins ?: '0'}",
                                            color = Color.Yellow,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    }

                                    // Energía estilo iconitos
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RechargeStatus(
                                            secondsUntilNextRecharge = uiState.energy.secondsLeft,
                                            currentAmount = uiState.energy.currentAmount,
                                            maxAmount = uiState.energy.maxAmount,
                                            batteryBoltRes = R.drawable.ic_battery_bolt,
                                            cellFilledRes = R.drawable.ic_cell_filled,
                                            cellEmptyRes = R.drawable.ic_cell_empty
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Engranaje (configuración / perfil)
                                IconButton(
                                    onClick = onProfileClick,
                                    modifier = Modifier
                                        .size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Configuración",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                            }
                            PlayerSummaryCard(
                                playerName = CurrentUser.user?.name ?: "Jugador",
                                rankText = "#${CurrentUser.user?.points} - Nivel ${CurrentUser.user?.lastLevelId}",
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(start = 16.dp, end = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Espacio antes del auto grande y botones
                            Spacer(modifier = Modifier.height(60.dp))


                            Column(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 32.dp, start = 64.dp, end = 64.dp)
                            ) {
                                TextButton(
                                    onClick = { viewModel.navigateToMultiplayer() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 2.dp,
                                            color = CyanMR,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            Color.Black.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        text = "Multijugador",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanMR,
                                    )
                                }

                                TextButton(
                                    onClick = onStoryModeClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 2.dp,
                                            color = CyanMR,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            Color.Black.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        text = "Modo Historia",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanMR,
                                    )
                                }

                                TextButton(
                                    onClick = onFreePracticeClick,
                                    enabled = false,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            width = 2.dp,
                                            color = CyanMR,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            Color.Gray.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                ) {
                                    Text(
                                        text = "Práctica libre",
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanMR,
                                    )
                                }
                            }

                            Box(modifier = Modifier.height(32.dp))

                            // Botonera inferior
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                            ) {
                                IconButton(
                                    onClick = onShopClick,
                                    modifier = Modifier
                                        .shadow(4.dp, RoundedCornerShape(16.dp))
                                        .border(2.dp, CyanMR, RoundedCornerShape(16.dp))
                                        .background(
                                            Color.Gray.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .size(64.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ShoppingCart,
                                        contentDescription = "Tienda",
                                        tint = CyanMR,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(32.dp))

                                IconButton(
                                    onClick = onGarageClick,
                                    modifier = Modifier
                                        .shadow(4.dp, RoundedCornerShape(16.dp))
                                        .border(2.dp, CyanMR, RoundedCornerShape(16.dp))
                                        .background(
                                            Color.Gray.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .size(64.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Home,
                                        contentDescription = "Garage",
                                        tint = CyanMR,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(32.dp))

                                IconButton(
                                    onClick = onStatsClick,
                                    modifier = Modifier
                                        .shadow(4.dp, RoundedCornerShape(16.dp))
                                        .border(2.dp, CyanMR, RoundedCornerShape(16.dp))
                                        .background(
                                            Color.Black.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .size(64.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "Estadísticas",
                                        tint = CyanMR,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Snackbars
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                SnackbarHost(hostState = snackbarHostState)
            }

            if (showTutorial) {
                TutorialOverlay(onFinish = {
                    showTutorial = false
                    onTutorialComplete()
                })
            }
        }
    }
}

// ======= CARD DE JUGADOR TIPO MOCKUP =======
@Composable
fun PlayerSummaryCard(
    playerName: String,
    rankText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .height(110.dp)
            .border(
                width = 2.dp,
                color = CyanMR,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.track_cake),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(16.dp))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = playerName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(40.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_trophy),
                        contentDescription = "Ranking",
                        tint = Color(0xFFF7E400),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = rankText,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Image(
                painter = painterResource(id = R.drawable.car_game),
                contentDescription = "Auto del jugador",
                modifier = Modifier
                    .height(50.dp)
                    .align(Alignment.Bottom),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun RechargeStatus(
    secondsUntilNextRecharge: Int,
    currentAmount: Int,
    maxAmount: Int,
    modifier: Modifier = Modifier,
    batteryBoltRes: Int,
    cellFilledRes: Int,
    cellEmptyRes: Int,
    tintCells: Color? = Color(0xFFF7E400)
) {
    val yellow = Color(0xFFF7E400)
    val white  = Color.White
    Log.d("energy", "secondsUntilNextRecharge: $secondsUntilNextRecharge, currentAmount: $currentAmount, maxAmount: $maxAmount")
    val total  = maxAmount.coerceAtLeast(0)
    val filled = currentAmount.coerceIn(0, total)
    Log.d("energy", "total: $total, filled: $filled")

    // si está lleno o no hay contador, no muestres “0:00”
    val showTimer = secondsUntilNextRecharge > 0 && filled < total

    fun formatMMSS(total: Int): String {
        val m = total / 60
        val s = total % 60
        return "%d:%02d".format(m, s)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Ícono batería + timer (timer solo cuando corresponde)
        Column(horizontalAlignment = Alignment.Start) {
            Image(
                painter = painterResource(id = batteryBoltRes),
                contentDescription = "Recarga",
                modifier = Modifier.size(18.dp),
                colorFilter = ColorFilter.tint(yellow) // quitá si tu asset ya es amarillo
            )
            if (showTimer) {
                Text(
                    text = formatMMSS(secondsUntilNextRecharge),
                    color = white,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
        }

        // Celdas
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(total) { i ->
                Image(
                    painter = painterResource(id = if (i < filled) cellFilledRes else cellEmptyRes),
                    contentDescription = null,
                    modifier = Modifier
                        .height(28.dp)
                        .width(18.dp),
                    colorFilter = tintCells?.let { ColorFilter.tint(it) } // aplica sólo si querés tinte
                )
            }
        }
    }
}
