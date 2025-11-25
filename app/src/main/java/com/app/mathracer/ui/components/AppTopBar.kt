package com.app.mathracer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.app.mathracer.ui.components.ProductImage
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.mathracer.R
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.UserState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import android.util.Log
import com.app.mathracer.ui.screens.home.RechargeStatus
import com.app.mathracer.ui.screens.home.viewmodel.HomeViewModel

@Composable
fun AppTopBar(
    energyState: HomeViewModel.EnergyState = HomeViewModel.EnergyState(),
    onShopClick: () -> Unit = {},
    onEnergyClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    screenTitle: String? = null
) {
    Surface(
        color = Color(0xFF1E1E1E),
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(88.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
           
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBack() }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(88.dp),
                    contentScale = ContentScale.Fit
                )
            }

             
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                screenTitle?.let { title ->
                    Text(text = title, color = Color.White, fontSize = 20.sp)
                }
            }

             
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                 
                Box(modifier = Modifier.clickable { onShopClick() }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.coin),
                            contentDescription = "Monedas",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val coinsState by com.app.mathracer.data.UserState.coins.collectAsState()
                        LaunchedEffect(coinsState) {
                            Log.d("AppTopBar", "coinsState changed: $coinsState | CurrentUser=${CurrentUser.user?.coins}")
                        }
                        Text(text = "${coinsState}", color = Color.Yellow, fontSize = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))

               
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onEnergyClick() }
                        .padding(end = 6.dp)
                ) {
                    RechargeStatus(
                        secondsUntilNextRecharge = energyState.secondsLeft,
                        currentAmount = energyState.currentAmount,
                        maxAmount = energyState.maxAmount,
                        batteryBoltRes = R.drawable.ic_battery_bolt,
                        cellFilledRes = R.drawable.ic_cell_filled,
                        cellEmptyRes = R.drawable.ic_cell_empty
                    )
                }

                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                            val activeChar by UserState.activeCharacter.collectAsState()
                            ProductImage(
                                productId = activeChar,
                                fallbackRes = R.drawable.avatar,
                                modifier = Modifier.size(36.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                    }
                }
            }
        }
    }
}
