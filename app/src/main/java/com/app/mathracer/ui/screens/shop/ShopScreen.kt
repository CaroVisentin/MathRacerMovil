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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.app.mathracer.R
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.network.ItemDto
import com.app.mathracer.data.network.ShopResponseEnergies
import com.app.mathracer.data.network.ShopResponseWildcards
import com.app.mathracer.ui.components.ProductImage
import com.app.mathracer.ui.screens.shop.viewmodel.ShopBuyType
import com.app.mathracer.ui.screens.shop.viewmodel.ShopViewModel
import com.app.mathracer.ui.theme.MagentaMR

// ---------------------------------------------------------------------
// PANTALLA PRINCIPAL
// ---------------------------------------------------------------------
@Composable
fun ShopScreen(
    viewModel: ShopViewModel,
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    var selectedItem by remember { mutableStateOf<ItemDto?>(null) }
    var selectedBuyType by remember { mutableStateOf<ShopBuyType?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogPrice by remember { mutableStateOf(0) }

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
            item {
                ShopHeader(
                    coins = state.coins,
                    onBackClick = onBackClick
                )
            }

            // ENERGÍAS
            item {
                SectionTitle(text = "ENERGIAS")
            }

            item {
                state.energies?.let { energies ->
                    EnergyShopCard(
                        data = energies,
                        onClick = {
                            selectedItem = null        // porque no es ItemDto
                            selectedBuyType = ShopBuyType.ENERGY
                            dialogPrice = energies.pricePerUnit  // o el total que quieras cobrar
                            showDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // COMODINES
            item {
                SectionTitle(text = "COMODINES")
            }
            item {
                ShopSectionWildcardGrid(
                    items = state.comodines,
                    onItemClick = {
                        selectedItem = null
                        selectedBuyType = ShopBuyType.COMODIN
                        dialogPrice = 0
                        showDialog = true
                    }
                )
            }

            // AUTOS
            item {
                SectionTitle(text = "AUTOS")
            }
            item {
                ShopSectionGrid(
                    items = state.cars,
                    onItemClick = { item ->
                        selectedItem = item
                        selectedBuyType = ShopBuyType.CAR
                        dialogPrice = item.price
                        showDialog = true
                    }
                )
            }

            // FONDOS
            item {
                SectionTitle(text = "FONDOS")
            }
            item {
                ShopSectionGrid(
                    items = state.backgrounds,
                    onItemClick = { item ->
                        selectedItem = item
                        selectedBuyType = ShopBuyType.BACKGROUND
                        dialogPrice = item.price
                        showDialog = true
                    }
                )
            }

            // PERSONAJES
            item {
                SectionTitle(text = "PERSONAJES")
            }
            item {
                ShopSectionGrid(
                    items = state.characters,
                    onItemClick = { item ->
                        selectedItem = item
                        selectedBuyType = ShopBuyType.CHARACTER
                        dialogPrice = item.price
                        showDialog = true
                    }
                )
            }
        }

        // DIALOGO DE COMPRA
        if (showDialog && selectedBuyType != null) {
            BuyConfirmDialog(
                price = dialogPrice,
                onDismiss = {
                    showDialog = false
                    selectedItem = null
                    selectedBuyType = null
                },
                onConfirm = {
                    val playerId = CurrentUser.user?.id ?: 0

                    when (selectedBuyType) {
                        ShopBuyType.CAR -> {
                            selectedItem?.let { item ->
                                viewModel.buyCar(
                                    playerId = playerId,
                                    item = item
                                )
                            }
                        }

                        ShopBuyType.BACKGROUND -> {
                            selectedItem?.let { item ->
                                viewModel.buyBackground(
                                    playerId = playerId,
                                    item = item
                                )
                            }
                        }

                        ShopBuyType.CHARACTER -> {
                            selectedItem?.let { item ->
                                viewModel.buyCharacter(
                                    playerId = playerId,
                                    item = item
                                )
                            }
                        }

                        ShopBuyType.ENERGY -> {
                            viewModel.buyEnergy(
                                playerId = playerId
                            )
                        }

                        ShopBuyType.COMODIN -> {
                            selectedItem?.let { item ->
                                viewModel.buyComodin(
                                    playerId = playerId,
                                    item = item
                                )
                            }
                        }

                        null -> Unit
                    }

                    // cierro el diálogo
                    showDialog = false
                    selectedItem = null
                    selectedBuyType = null
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
            verticalAlignment = Alignment.CenterVertically,
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

@Composable
fun ShopSectionWildcardGrid(
    items: List<ShopResponseWildcards>,
    onItemClick: (ShopResponseWildcards) -> Unit
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
                    ShopItemWildcardCard(
                        item = item,
                        modifier = Modifier
                            .weight(1f), // <- IMPORTANTE: igual que ShopItemCard
                        onClick = { onItemClick(item) }
                    )
                }

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
        "común", "comun" -> Color(0xFF9C9C9C)
        "poco común", "poco comun" -> Color(0xFF1EFF00)
        "raro" -> Color(0xFF007BFF)
        "épico", "epico" -> Color(0xFFA335EE)
        "legendario" -> Color(0xFFFFA500)
        else -> Color(0xFF1E1E1E)
    }
}

// ---------------------------------------------------------------------
// CARD DE CADA ITEM (AUTO, FONDO, PERSONAJE, COMODÍN)
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
            ProductImage(
                productId = item.id,
                fallbackRes = R.drawable.mathi,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
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

@Composable
fun ShopItemWildcardCard(
    item: ShopResponseWildcards,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val textColor = textColorForBackground(Color.White)

    Box(
        modifier = modifier
            .aspectRatio(0.8f)
            .clip(RoundedCornerShape(12.dp))
            .background(MagentaMR)
            .border(2.dp, Color.White, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(top = 20.dp)
        ) {
            val iconModifier = Modifier.size(36.dp) // <- tamaño de la imagen

            when (item.name) {
                "Matafuego" -> {
                    Image(
                        painter = painterResource(id = R.drawable.ic_shield),
                        contentDescription = null,
                        modifier = iconModifier,
                        contentScale = ContentScale.Fit
                    )
                }

                "Cambio de rumbo" -> {
                    Image(
                        painter = painterResource(id = R.drawable.ic_shuffle),
                        contentDescription = null,
                        modifier = iconModifier,
                        contentScale = ContentScale.Fit
                    )
                }

                "Nitro" -> {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bolt),
                        contentDescription = null,
                        modifier = iconModifier,
                        contentScale = ContentScale.Fit
                    )
                }
            }

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

@Composable
fun EnergyShopCard(
    data: ShopResponseEnergies,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val enabled = data.maxCanBuy > 0

    val bgColor = if (enabled) Color(0xFF00C853) else Color(0xFF555555) // verde / gris
    val contentAlpha = if (enabled) 1f else 0.4f

    val cardModifier = modifier
        .fillMaxWidth()
        .height(130.dp) // ajustá este alto para matchear la otra card
        .clip(RoundedCornerShape(12.dp))
        .background(bgColor)
        .border(2.dp, Color.White, RoundedCornerShape(12.dp))
        .then(
            if (enabled) Modifier.clickable { onClick() } else Modifier
        )
        .padding(8.dp)

    Box(modifier = cardModifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha)
        ) {
            // Imagen de batería (tu PNG de energía)
            Image(
                painter = painterResource(id = R.drawable.energy),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Precio por unidad
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
                    text = "%,d".format(data.pricePerUnit),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Info de energía
            Text(
                text = "Energía: ${data.currentAmount}/${data.maxAmount}",
                color = Color.White,
                fontSize = 12.sp
            )

            Text(
                text = if (enabled)
                    "Podés comprar: ${data.maxCanBuy}"
                else
                    "Sin energía disponible",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}
