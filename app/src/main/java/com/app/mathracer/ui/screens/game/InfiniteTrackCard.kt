package com.app.mathracer.ui.screens.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

private val CardDark      = Color(0xFF2C2C2C)
private val BorderSoft    = Color(0x66FFFFFF)

@Composable
fun InfiniteTrackCard(
    title: String,
    titleColor: Color,
    trackRes: Int,
    carRes: Int,
    underlineColor: Color,
    progress: Int = 0,
    totalAnswered: Int? = null
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
                val labelText = if (totalAnswered != null) {
                    "$title ($progress/$totalAnswered)"
                } else {
                    "$title ($progress)"
                }

                androidx.compose.material3.Text(
                    text = labelText,
                    color = titleColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            val progress01 = ((progress % 10) / 10f).coerceIn(0f, 1f)
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

            Image(
                painter = painterResource(carRes),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = startMargin + offsetX, y = 0.dp)
                    .padding(bottom = 12.dp)
                    .size(width = carWidth, height = carHeight),
                contentScale = ContentScale.Fit
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(Color.Gray.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = ((progress % 10) / 10f).coerceIn(0f, 1f))
                    .height(6.dp)
                    .background(underlineColor)
            )
        }
    }
}
