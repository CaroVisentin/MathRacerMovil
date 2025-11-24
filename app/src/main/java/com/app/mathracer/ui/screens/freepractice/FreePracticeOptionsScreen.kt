package com.app.mathracer.ui.screens.freepractice

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.app.mathracer.R
import com.app.mathracer.ui.theme.CyanMR
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Alignment.Companion.CenterStart

@Composable
fun FreePracticeOptionsScreen(
    onModoLibre: () -> Unit,
    onModoInfinito: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

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


            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 48.dp, end = 48.dp)
                        .background(Color.Black.copy(alpha = 0.45f), shape = RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onModoLibre,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Modo Libre",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanMR,
                            )
                        }

                        TextButton(
                            onClick = onModoInfinito,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(width = 2.dp, color = CyanMR, shape = RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(
                                text = "Modo Infinito",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanMR,
                            )
                        }
                    }
                }
            }
        }
    }
}
