package com.app.mathracer.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun ProductImage(
    productId: Int?,
    fallbackRes: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val ctx = LocalContext.current
    if (productId != null && productId > 0) {
        val candidates = listOf("products_$productId", "products$productId")
        var resId = 0
        for (name in candidates) {
            val id = ctx.resources.getIdentifier(name, "drawable", ctx.packageName)
            if (id != 0) { resId = id; break }
        }

        if (resId != 0) {
            val resType = try { ctx.resources.getResourceTypeName(resId) } catch (e: Exception) { "" }
            if (resType == "drawable") {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = contentScale
                )
            } else {
                val assetPath = "file:///android_asset/products/$productId.png"
                AsyncImage(
                    model = assetPath,
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = contentScale,
                    placeholder = painterResource(id = fallbackRes),
                    error = painterResource(id = fallbackRes)
                )
            }
        } else {
            val assetPath = "file:///android_asset/products/$productId.png"
            AsyncImage(
                model = assetPath,
                contentDescription = null,
                modifier = modifier,
                contentScale = contentScale,
                placeholder = painterResource(id = fallbackRes),
                error = painterResource(id = fallbackRes)
            )
        }
    } else {
        Image(
            painter = painterResource(id = fallbackRes),
            contentDescription = null,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}
