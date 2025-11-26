package com.app.mathracer.ui.screens.chest

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.mathracer.R

@Composable
fun ChestScreen(
    onContinue: () -> Unit = {},
    chestType: String
) {
    val vm: ChestViewModel = viewModel()

    val chestScale by animateFloatAsState(
        targetValue = if (vm.isOpening) 1.08f else 1f,
        animationSpec = tween(400)
    )

    val chestRotation by animateFloatAsState(
        targetValue = if (vm.isOpening) -6f else 0f,
        animationSpec = tween(400)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0B0D)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text("¡Felicidades!", fontSize = 28.sp, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Acá está tu recompensa",
                fontSize = 20.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(Modifier.height(20.dp))

            // Imagen del cofre
            Box(contentAlignment = Alignment.Center) {
                val chestRes =
                    if (vm.showOpenImage) R.drawable.chest_open else R.drawable.chest

                Image(
                    painter = painterResource(chestRes),
                    contentDescription = "cofre",
                    modifier = Modifier
                        .size(220.dp)
                        .scale(chestScale)
                        .rotate(chestRotation)
                        .clickable(enabled = !vm.isOpening && !vm.fetched) {
                            vm.selectChestType(chestType)
                        }
                )
            }

            Spacer(Modifier.height(20.dp))

            if (!vm.fetched && vm.error == null) {
                Text(
                    text = if (vm.loading) "Toca el cofre para verlo" else "Toca el cofre para abrirlo",
                    color = Color.White
                )
            }

            if (vm.error != null) {
                Text(text = "Error: ${vm.error}", color = Color.Red)
            }

            // Items revelados
            val scroll = rememberScrollState()

            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .horizontalScroll(scroll),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in vm.items.indices) {
                    val visible = i < vm.itemsVisibleCount

                    AnimatedVisibility(
                        visible = visible,
                        enter = fadeIn(tween(250)),
                        exit = fadeOut()
                    ) {
                        val it = vm.items[i]
                        val bgColor = Color(0xFF2E2E2E)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .width(140.dp)
                                .height(210.dp)
                                .border(BorderStroke(1.dp, Color.White), RoundedCornerShape(10.dp))
                        ) {

                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(8.dp))

                                val imgRes = when {
                                    it.type == "Product" -> when (it.product?.productType) {
                                        1 -> R.drawable.car
                                        2 -> R.drawable.avatar
                                        3 -> R.drawable.background
                                        else -> R.drawable.coin
                                    }
                                    it.type == "Coins" -> R.drawable.coin
                                    it.type == "Wildcard" -> {
                                        val wName = it.wildcard?.name ?: ""
                                        when {
                                            wName.contains("nitro", ignoreCase = true) -> R.drawable.ic_bolt
                                            wName.contains("matafuego", ignoreCase = true) -> R.drawable.ic_shield
                                            wName.contains("cambio", ignoreCase = true) || wName.contains("rumbo", ignoreCase = true) || wName.contains("shuffle", ignoreCase = true) -> R.drawable.ic_shuffle
                                            it.wildcard?.id == 3 -> R.drawable.ic_bolt
                                            else -> R.drawable.coin
                                        }
                                    }
                                    else -> R.drawable.coin
                                }

                                if (it.type == "Product") {
                                    com.app.mathracer.ui.components.ProductImage(
                                        productId = it.product?.id,
                                        fallbackRes = imgRes,
                                        modifier = Modifier.size(88.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(imgRes),
                                        contentDescription = null,
                                        modifier = Modifier.size(88.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }

                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = when (it.type) {
                                        "Product" -> it.product?.name ?: "Producto"
                                        "Coins" -> "Monedas"
                                        else -> it.type
                                    },
                                    color = Color.White,
                                    fontSize = 18.sp
                                )

                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "x${it.quantity}",
                                    color = Color.White.copy(alpha = 0.95f),
                                    fontSize = 13.sp
                                )

                                it.product?.rarityName?.let { rarity ->
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        text = rarity,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            AnimatedVisibility(
                visible = vm.canContinue,
                enter = fadeIn(tween(250)),
                exit = fadeOut()
            ) {
                Button(onClick = onContinue) {
                    Text("Continuar")
                }
            }
        }
    }
}
