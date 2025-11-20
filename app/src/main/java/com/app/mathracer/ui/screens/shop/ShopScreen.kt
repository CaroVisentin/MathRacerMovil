package com.app.mathracer.ui.screens.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.app.mathracer.R
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.network.ItemDto
import com.app.mathracer.ui.screens.shop.viewmodel.ShopViewModel

// ---------------------------------------------------------------------
// MODELOS
// ---------------------------------------------------------------------
enum class ShopCategory {
    CAR,
    BACKGROUND,
    CHARACTER
}
// ---------------------------------------------------------------------
// PANTALLA PRINCIPAL
// ---------------------------------------------------------------------
@Composable
fun ShopScreen(
    viewModel: ShopViewModel, onBackClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var selectedItem by remember { mutableStateOf<ItemDto?>(null) }

//    val specialOffer = ItemDto(
//        id = 999,
//        price = 100_000,
//        imageUrl = "www.google.com",
//        name = "oferta",
//        description = "oferta",
//        productTypeId = 2,
//        productTypeName = "2",
//        rarity = "2",
//        isOwned = false,
//        currency = "455",
//    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp, bottom = 16.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // HEADER
            item {
                ShopHeader(
                    coins = state.coins,
                    onBackClick = onBackClick
                )
            }

//            // OFERTA ESPECIAL
//            item {
//                SectionTitle(text = "OFERTA ESPECIAL")
//            }
//            item {
//                SpecialOfferCard(
//                    item = specialOffer,
//                    onClick = { selectedItem = it }
//                )
//            }

            // AUTOS
            item {
                SectionTitle(text = "AUTOS")
            }
            item {
                ShopSectionGrid(
                    items = state.cars,
                    onItemClick = { selectedItem = it }
                )
            }

            // FONDOS
            item {
                SectionTitle(text = "FONDOS")
            }
            item {
                ShopSectionGrid(
                    items = state.backgrounds,
                    onItemClick = { selectedItem = it }
                )
            }

            // PERSONAJES
            item {
                SectionTitle(text = "PERSONAJES")
            }
            item {
                ShopSectionGrid(
                    items = state.characters,
                    onItemClick = { selectedItem = it }
                )
            }
        }

        // DIALOGO DE COMPRA
        if (selectedItem != null) {
            BuyConfirmDialog(
                price = selectedItem!!.price,
                onDismiss = { selectedItem = null },
                onConfirm = {
                    if(selectedItem != null) {
                        viewModel.buyItem(
                            playerId = CurrentUser.user!!.id ?: 0,
                            item = selectedItem
                        )
                        selectedItem = null
                    }
                }
            )
        }
    }
}

// ---------------------------------------------------------------------
// HEADER SUPERIOR (TIENDA + MONEDAS)
// ---------------------------------------------------------------------
@Composable
fun ShopHeader(
    coins: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Volver",
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "TIENDA",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.coin),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "%,d".format(coins),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ---------------------------------------------------------------------
// TÍTULO DE SECCIÓN
// ---------------------------------------------------------------------
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}

// ---------------------------------------------------------------------
// CARD DE OFERTA ESPECIAL
// ---------------------------------------------------------------------
@Composable
fun SpecialOfferCard(
    item: ItemDto,
    onClick: (ItemDto) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF243149))
            .border(2.dp, Color.White, RoundedCornerShape(16.dp))
            .clickable { onClick(item) }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen grande (auto + personaje)
//            Image(
//                painter = painterResource(id = item.image),
//                contentDescription = null,
//                modifier = Modifier
//                    .height(90.dp)
//                    .weight(1f)
//            )
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(90.dp)
                    .weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "MARIO + AUTO ESTILO\n\"MATH RACER\"",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.coin),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "%,d".format(item.price),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------
// GRID POR SECCIÓN (3 columnas)
// ---------------------------------------------------------------------
@Composable
fun ShopSectionGrid(
    items: List<ItemDto>,
    onItemClick: (ItemDto) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    ShopItemCard(
                        item = item,
                        modifier = Modifier.weight(1f),
                        onClick = { onItemClick(item) }
                    )
                }
                // si la fila no tiene las 3 columnas, agrego espacios vacíos
                repeat(3 - rowItems.size) {
                    Spacer(
                        modifier = Modifier
                            .weight(1f)
                            .height(0.dp)
                    )
                }
            }
        }
    }
}
fun textColorForBackground(bg: Color): Color {
    val r = bg.red
    val g = bg.green
    val b = bg.blue

    // luminancia perceptual
    val luminance = (0.299 * r + 0.587 * g + 0.114 * b)

    return if (luminance > 0.6) Color.Black else Color.White
}
fun rarityColor(rarity: String): Color {
    return when (rarity.lowercase()) {
        "común", "comun" -> Color(0xFFFFFFFF)   // blanco
        "poco común", "poco comun" -> Color(0xFF1EFF00) // verde
        "raro" -> Color(0xFF007BFF)             // azul
        "épico", "epico" -> Color(0xFFA335EE)   // violeta
        "legendario" -> Color(0xFFFFA500)       // naranja
        else -> Color(0xFF1E1E1E)               // por default
    }
}

// ---------------------------------------------------------------------
// CARD DE CADA ITEM (AUTO, FONDO, PERSONAJE)
// ---------------------------------------------------------------------
@Composable
fun ShopItemCard(
    item: ItemDto,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = rarityColor(item.rarity)
    val textColor = textColorForBackground(bgColor)

    Box(
        modifier = modifier
            .aspectRatio(0.8f) // para formar cuadraditos
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
//            Image(
//                painter = item.imageUrl,
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//            )
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coin),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "%,d".format(item.price),
                    color = textColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ---------------------------------------------------------------------
// DIALOGO DE CONFIRMACIÓN
// ---------------------------------------------------------------------
@Composable
fun BuyConfirmDialog(
    price: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
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
                    // Botón X
                    Text(
                        text = "X",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { onDismiss() }
                    )
                    // Texto
                    Text(
                        text = "¿Deseas comprar este\nartículo por ${"%,d".format(price)} monedas?",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Botón comprar
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(30.dp))
                            .clickable { onConfirm() }
                            .padding(horizontal = 32.dp, vertical = 10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.coin),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "COMPRAR",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }


                }
            }
        }
    }
}
