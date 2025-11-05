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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.app.mathracer.data.model.ChestItem
import com.app.mathracer.data.repository.ChestRepository
import com.app.mathracer.R

@Composable
fun ChestScreen(
    onContinue: () -> Unit = {}
) {
    var loading by remember { mutableStateOf(true) }
    var items by remember { mutableStateOf<List<ChestItem>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    
    var isOpening by remember { mutableStateOf(false) }
    var showOpenImage by remember { mutableStateOf(false) }
    var itemsVisibleCount by remember { mutableStateOf(0) }
    var fetched by remember { mutableStateOf(false) }

    val chestScale by animateFloatAsState(targetValue = if (isOpening) 1.08f else 1f, animationSpec = tween(400))
    val chestRotation by animateFloatAsState(targetValue = if (isOpening) -6f else 0f, animationSpec = tween(400))

   
    fun openChest() {
        if (isOpening || fetched) return
        isOpening = true
        scope.launch {
            try {
                val resp = ChestRepository.completeTutorial()
                if (resp.isSuccessful) {
                    val body = resp.body()
                    items = body?.items ?: emptyList()
                    fetched = true
                    delay(400)
                    showOpenImage = true
                    
                    for (i in items.indices) {
                        itemsVisibleCount = i + 1
                        delay(300)
                    }
                } else {
                    error = resp.errorBody()?.string() ?: "Error ${resp.code()}"
                    
                    isOpening = false
                }
            } catch (e: Exception) {
                error = e.message ?: "Excepción al abrir cofre"
                isOpening = false
            } finally {
                loading = false
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF0B0B0D)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "¡Felicidades!", fontSize = 28.sp, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Acá está tu recompensa por terminar el tutorial", fontSize = 20.sp, color = Color.White.copy(alpha = 0.9f))
            Spacer(modifier = Modifier.height(20.dp))

            
            Box(contentAlignment = Alignment.Center) {
                val chestRes = if (showOpenImage) R.drawable.chest_open else R.drawable.chest
                Image(
                    painter = painterResource(id = chestRes),
                    contentDescription = "cofre",
                    modifier = Modifier
                        .size(220.dp)
                        .scale(chestScale)
                        .rotate(chestRotation)
                        .clickable { openChest() }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (loading && !fetched) {
                Text(text = "Toca el cofre para verlo", color = Color.White)
            }

            if (error != null) {
                Text(text = "Error: $error", color = Color.Red)
            }

            
            val scrollState = rememberScrollState()
            Row(modifier = Modifier
                .padding(12.dp)
                .horizontalScroll(scrollState), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (i in items.indices) {
                    val visible = i < itemsVisibleCount
                    AnimatedVisibility(visible = visible, enter = fadeIn(tween(250)), exit = fadeOut()) {
                        val it = items[i]
                        
                        val bgColor = Color(0xFF2E2E2E)

                        Card(
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .width(140.dp)
                                .height(210.dp)
                                .border(BorderStroke(1.dp, Color.White), shape = RoundedCornerShape(10.dp))
                        ) {
                            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(modifier = Modifier.height(8.dp))

                                
                                val imgRes = when {
                                    it.type == "Product" -> when (it.product?.productType) {
                                        1 -> R.drawable.car
                                        2 -> R.drawable.avatar
                                        3 -> R.drawable.background
                                        else -> R.drawable.coin
                                    }
                                    it.type == "Coins" -> R.drawable.coin
                                    it.type == "Wildcard" -> R.drawable.coin
                                    else -> R.drawable.coin
                                }

                                Image(
                                    painter = painterResource(id = imgRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(72.dp)
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                
                                Text(text = when (it.type) {
                                    "Product" -> it.product?.name ?: "Producto"
                                    "Coins" -> "Monedas"
                                    "Wildcard" -> it.wildcard?.name ?: "Comodín"
                                    else -> it.type
                                }, color = androidx.compose.ui.graphics.Color.White, fontSize = 18.sp)

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(text = "x${it.quantity}", color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.95f), fontSize = 13.sp)

                                it.product?.rarityName?.let { rarity ->
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = rarity, color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { onContinue() }) {
                Text(text = "Continuar")
            }
        }
    }
}
