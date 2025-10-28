package com.app.mathracer.ui.navigation

import LoginScreen
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.app.mathracer.ui.RegisterScreen
import com.app.mathracer.ui.screens.home.HomeScreen
import com.app.mathracer.ui.screens.game.GameScreen
import com.app.mathracer.ui.screens.profile.ProfileScreen
import com.app.mathracer.ui.screens.waitingOpponent.WaitingOpponentScreen

@Composable
fun MathRacerNavGraph(
    navController: NavHostController
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.HOME) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute
            )
            
            HomeScreen(
                onMultiplayerClick = {
                    navController.navigate(Routes.WAITING_OPPONENT)
                },
                onStoryModeClick = { 
                    // TODO: Implementar navegación a modo historia
                },
                onFreePracticeClick = { 
                    // TODO: Implementar navegación a práctica libre
                },
                onShopClick = { 
                    // TODO: Implementar navegación a tienda
                },
                onGarageClick = { 
                    // TODO: Implementar navegación a garage
                },
                onStatsClick = { 
                  //  navController.navigate(Routes.SIGNALR_TEST)
                },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }
        
        composable(Routes.WAITING_OPPONENT) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )
            
            WaitingOpponentScreen(
                onNavigateToGame = { gameId, playerName ->
                    navController.navigate(Routes.gameWithIdAndPlayer(gameId, playerName)) {
                        popUpTo(Routes.WAITING_OPPONENT) {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(Routes.LOGIN) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() } // volver atrás
            )

            LoginScreen(
                onLogin = { user, pass ->
                    // 🔹 Acá podés validar login o navegar al home
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onLoginWithGoogle = {
                    // 🔹 Lógica para login con Google
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            val _ctx = LocalContext.current
            RegisterScreen(
                onRegister = { email: String, user: String, pass: String ->
                    try {
                        val prefs = _ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit().putBoolean("show_tutorial_on_next_launch", true).apply()
                    } catch (_: Throwable) {
                        // ignore
                    }
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onGoogle = { /* flujo Google */ },
                onLoginClick = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.PROFILE) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )

            ProfileScreen(
               // onNavigateBack = { navController.navigateUp() }
            )
        }


        composable(
            route = "game/{gameId}/{playerName}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType },
                navArgument("playerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
            val playerName = backStackEntry.arguments?.getString("playerName") ?: "Jugador"
            
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                }
            )
            
            GameScreen(
                gameId = gameId,
                playerName = playerName,
                onNavigateBack = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) {
                            inclusive = true
                        }
                    }
                },
                onPlayAgain = {
                    navController.navigate(Routes.WAITING_OPPONENT) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        
        composable(Routes.SIGNALR_TEST) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )
            

        }
    }
}
