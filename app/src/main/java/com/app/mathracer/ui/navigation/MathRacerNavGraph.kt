package com.app.mathracer.ui.navigation

import LoginScreen
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.app.mathracer.R
import com.app.mathracer.ui.RegisterScreen
import com.app.mathracer.ui.screens.home.HomeScreen
import com.app.mathracer.ui.screens.game.GameScreen
import com.app.mathracer.ui.screens.levels.LevelsScreen
import com.app.mathracer.ui.screens.levels.viewmodel.LevelsViewModel
import com.app.mathracer.ui.screens.profile.ProfileScreen
import com.app.mathracer.ui.screens.register.viewmodel.RegisterViewModel
import com.app.mathracer.ui.screens.waitingOpponent.WaitingOpponentScreen
import com.app.mathracer.ui.screens.worlds.WorldsScreen
import com.app.mathracer.ui.screens.worlds.WorldsScreenRoute

@Composable
fun MathRacerNavGraph(
    navController: NavHostController,
    onGoogleSignIn: (launcher: ActivityResultLauncher<Intent>) -> Unit = {}
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
                    navController.navigate(Routes.WORLDS)
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
            val context = LocalContext.current
            val registerViewModel: RegisterViewModel = hiltViewModel()

            // Configurar Google Sign-In
            android.util.Log.d("GoogleSignIn", "Configurando cliente de Google Sign-In")
            val googleSignInClient = remember {
                try {
                    val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                        com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                    )
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()  // Solicitar perfil básico
                        .build()
                    android.util.Log.d("GoogleSignIn", "GSO configurado correctamente")
                    com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
                } catch (e: Exception) {
                    android.util.Log.e("GoogleSignIn", "Error al configurar GSO", e)
                    throw e
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                android.util.Log.d("GoogleSignIn", "Resultado recibido: ${result.resultCode}")
                when (result.resultCode) {
                    android.app.Activity.RESULT_OK -> {
                        android.util.Log.d("GoogleSignIn", "Result OK, procesando resultado...")
                        try {
                            // Verificar si hay datos en el intent
                            if (result.data == null) {
                                android.util.Log.e("GoogleSignIn", "Intent de resultado es null")
                                return@rememberLauncherForActivityResult
                            }

                            val task = com.google.android.gms.auth.api.signin.GoogleSignIn
                                .getSignedInAccountFromIntent(result.data)
                            
                            // Intentar obtener la cuenta de manera síncrona para logging
                            try {
                                val account = task.result
                                android.util.Log.d("GoogleSignIn", "Cuenta obtenida: ${account.email}, ID: ${account.id}")
                            } catch (e: Exception) {
                                android.util.Log.e("GoogleSignIn", "Error al obtener cuenta de manera síncrona", e)
                            }

                            // Proceder con el manejo asíncrono en el ViewModel
                            registerViewModel.handleGoogleSignInResult(result.data)
                        } catch (e: Exception) {
                            android.util.Log.e("GoogleSignIn", "Error al procesar resultado", e)
                            e.printStackTrace()
                        }
                    }
                    android.app.Activity.RESULT_CANCELED -> {
                        android.util.Log.d("GoogleSignIn", "Usuario canceló el inicio de sesión")
                    }
                    else -> {
                        android.util.Log.e("GoogleSignIn", "Error desconocido: ${result.resultCode}")
                    }
                }
            }

            RegisterScreen(
                viewModel = registerViewModel,
                onGoogleSignIn = { launcher.launch(googleSignInClient.signInIntent) },
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                onRegisterSuccess = {
                    // Guardar preferencias o datos si hace falta
                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("show_tutorial_on_next_launch", true)
                        .apply()

                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
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

        composable(Routes.WORLDS) {
            WorldsScreenRoute(
                onWorldClick = { world ->
                    navController.navigate("levels/${world.id}/${world.title}")
                }
            )
        }

        composable(
            route = "levels/{worldId}/{worldName}",
            arguments = listOf(
                navArgument("worldId") { type = NavType.IntType },
                navArgument("worldName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val viewModel: LevelsViewModel = hiltViewModel()
            val worldId = backStackEntry.arguments?.getInt("worldId") ?: 0
            val worldName = backStackEntry.arguments?.getString("worldName") ?: ""

            // Cargar datos del mundo seleccionado
            viewModel.loadLevelsForWorld(worldId, worldName)

            LevelsScreen(viewModel = viewModel)
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
