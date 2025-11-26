package com.app.mathracer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.ui.navigation.MathRacerNavGraph
import com.app.mathracer.ui.screens.shop.viewmodel.ShopViewModel
import com.app.mathracer.ui.theme.MathRacerTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.graphics.toArgb

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val shopViewModel: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            val barColor = Color(0xFF1E1E1E).toArgb()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.statusBarColor = barColor
            window.navigationBarColor = barColor
            window.setBackgroundDrawable(ColorDrawable(barColor))
            val controller = WindowInsetsControllerCompat(window, window.decorView)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
            try {
                controller.hide(WindowInsetsCompat.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } catch (_: Throwable) {
                // ignore if API not available
            }
        } catch (_: Throwable) {
            // ignore on older devices
        }

        // Manejar deep link al iniciar
        handleDeepLink(intent)

        setContent {
            MathRacerTheme {
                val navController = rememberNavController()
                MathRacerNavGraph(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data

        if (data != null && data.scheme == "mathracer" && data.host == "payment") {
            val path = data.path?.removePrefix("/") // "success", "failure", "pending"
            val playerId = CurrentUser.user?.id ?: 0

            Log.d("MainActivity", "🔗 Deep Link recibido: $data")
            Log.d("MainActivity", "📍 Status: $path, PlayerId: $playerId")

            // Delegar al ViewModel para manejar la lógica de negocio
            path?.let { status ->
                shopViewModel.handlePaymentResult(status, playerId)
            }
        }
    }
}
