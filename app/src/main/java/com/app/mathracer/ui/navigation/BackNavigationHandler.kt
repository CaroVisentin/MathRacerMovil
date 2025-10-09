package com.app.mathracer.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.app.mathracer.ui.navigation.Routes

@Composable
fun HandleBackNavigation(
    navController: NavController,
    currentRoute: String?,
    onBackPressed: (() -> Unit)? = null
) {
    BackHandler(enabled = true) {
        when {
            onBackPressed != null -> onBackPressed()
            currentRoute == Routes.HOME -> {
                // Si estamos en Home, no hacemos nada (o podríamos cerrar la app)
            }
            else -> {
                navController.navigateUp()
            }
        }
    }
}