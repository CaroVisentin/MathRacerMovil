package com.app.mathracer.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@Composable
fun AppBackgroundMusic() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START,
                Lifecycle.Event.ON_RESUME -> {
                    MusicManager.start(context)
                }
                Lifecycle.Event.ON_PAUSE -> {
                    MusicManager.pause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    MusicManager.stop()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            MusicManager.stop()
        }
    }
}
