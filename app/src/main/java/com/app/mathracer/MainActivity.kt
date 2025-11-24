package com.app.mathracer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.mathracer.ui.navigation.MathRacerNavGraph
import com.app.mathracer.ui.navigation.Routes
import com.app.mathracer.ui.theme.MathRacerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Lo inicializamos en setContent
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MathRacerTheme {
                navController = rememberNavController()

                // Procesar posible deep link con el intent con el que se abrió la Activity
                handleDeepLink(intent)

                MathRacerNavGraph(navController = navController)
            }
        }
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri = intent?.data ?: return

        if (data.scheme == "mathracer" && data.host == "payment") {
            val status = data.getQueryParameter("status") // "success" / "failure" / "pending"

            // Acá podrías guardar status si querés mostrar un mensaje en la shop.

            // Navegamos a la tienda
            if (::navController.isInitialized) {
                navController.navigate(Routes.SHOP) {
                    launchSingleTop = true
                    // Opcional: limpiar back stack hasta HOME
                    popUpTo(Routes.HOME) { inclusive = false }
                }
            }
        }
    }
}
