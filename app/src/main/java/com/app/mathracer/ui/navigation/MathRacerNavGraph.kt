package com.app.mathracer.ui.navigation

import LoginScreen
import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import com.app.mathracer.ui.screens.multiplayer.MultiplayerOptionsScreen
import com.app.mathracer.ui.screens.multiplayer.CreateMatchScreen
import com.app.mathracer.ui.screens.multiplayer.JoinMatchesScreen
import com.app.mathracer.ui.screens.multiplayer.InviteFriendsScreen
import com.app.mathracer.ui.screens.ranking.RankingScreen
import com.app.mathracer.ui.screens.ranking.viewmodel.RankingViewModel
import com.app.mathracer.ui.screens.worlds.WorldsScreen
import com.app.mathracer.ui.screens.worlds.WorldsScreenRoute
import com.app.mathracer.ui.rules.RulesScreen

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
                    userName = null,
                    userEmail = null,
                    onMultiplayerClick = {
                        navController.navigate(Routes.MULTIPLAYER_OPTIONS)
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
                        navController.navigate(Routes.RANKING)
                    },
                    onProfileClick = { navController.navigate(Routes.PROFILE) }
                )
            }

            composable(
                route = "home?userName={userName}&userEmail={userEmail}",
                arguments = listOf(
                    navArgument("userName") { type = NavType.StringType; defaultValue = "" },
                    navArgument("userEmail") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                HandleBackNavigation(
                    navController = navController,
                    currentRoute = currentRoute
                )

                val userName = backStackEntry.arguments?.getString("username")?.let { java.net.URLDecoder.decode(it, "utf-8") } ?: ""
                val userEmail = backStackEntry.arguments?.getString("userEmail")?.let { java.net.URLDecoder.decode(it, "utf-8") } ?: ""

                HomeScreen(
                    userName = if (userName.isNotBlank()) userName else null,
                    userEmail = if (userEmail.isNotBlank()) userEmail else null,
                    onMultiplayerClick = {
                        navController.navigate(Routes.MULTIPLAYER_OPTIONS)
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
                        navController.navigate(Routes.RANKING)
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

        composable(Routes.MULTIPLAYER_OPTIONS) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )

            MultiplayerOptionsScreen(
                onCreateGame = {
                    navController.navigate(Routes.CREATE_MATCH)
                },
                onJoinGame = {
                    navController.navigate(Routes.JOIN_MATCHES)
                },
                onInviteFriend = {
                    navController.navigate(Routes.INVITE_FRIENDS)
                },
                onCompetitiveMatch = {
                    navController.navigate(Routes.WAITING_OPPONENT)
                },
                onRanking = {
                    navController.navigate(Routes.RANKING)
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(Routes.INVITE_FRIENDS) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )

            InviteFriendsScreen(
                onInvite = { friendId, difficulty, resultType ->
                    navController.navigateUp()
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(Routes.RANKING) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )

            val viewModel: RankingViewModel = hiltViewModel()
            RankingScreen(viewModel = viewModel)
        }


        composable(Routes.CREATE_MATCH) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )

            CreateMatchScreen(
                onCreateMatch = { name, privacy, difficulty, resultType ->
                    navController.navigate(Routes.WAITING_OPPONENT)
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(Routes.JOIN_MATCHES) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )

            JoinMatchesScreen(
                onJoinConfirmed = { matchId, password ->
                   //cuando se haga lo de la contraseña hay que validarla aca, ahora hice que mande directo a la partida
                    navController.navigate(Routes.WAITING_OPPONENT)
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(Routes.LOGIN) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )

            val context = LocalContext.current
            val loginViewModel: com.app.mathracer.ui.screens.login.viewmodel.LoginViewModel = hiltViewModel()

            android.util.Log.d("GoogleSignIn", "Configurando cliente de Google Sign-In (Login)")
            val googleSignInClient = remember {
                try {
                    val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                        com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                    )
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
                        .build()
                    android.util.Log.d("GoogleSignIn", "GSO configurado correctamente (Login)")
                    com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
                } catch (e: Exception) {
                    android.util.Log.e("GoogleSignIn", "Error al configurar GSO (Login)", e)
                    throw e
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                android.util.Log.d("GoogleSignIn", "Resultado recibido (Login): ${result.resultCode}")
                when (result.resultCode) {
                    android.app.Activity.RESULT_OK -> {
                        android.util.Log.d("GoogleSignIn", "Result OK, procesando resultado... (Login)")
                        try {
                            if (result.data == null) {
                                android.util.Log.e("GoogleSignIn", "Intent de resultado es null (Login)")
                                return@rememberLauncherForActivityResult
                            }
                            val task = com.google.android.gms.auth.api.signin.GoogleSignIn
                                .getSignedInAccountFromIntent(result.data)
                            try {
                                val account = task.result
                                android.util.Log.d("GoogleSignIn", "Cuenta obtenida (Login): ${account.email}, ID: ${account.id}")
                            } catch (e: Exception) {
                                android.util.Log.e("GoogleSignIn", "Error al obtener cuenta de manera síncrona (Login)", e)
                            }
                            loginViewModel.handleGoogleSignInResult(result.data)
                        } catch (e: Exception) {
                            android.util.Log.e("GoogleSignIn", "Error al procesar resultado (Login)", e)
                            e.printStackTrace()
                        }
                    }
                    android.app.Activity.RESULT_CANCELED -> {
                        android.util.Log.d("GoogleSignIn", "Usuario canceló el inicio de sesión (Login)")
                    }
                    else -> {
                        android.util.Log.e("GoogleSignIn", "Error desconocido: ${result.resultCode} (Login)")
                    }
                }
            }

            LoginScreen(
                onLoginWithGoogle = { launcher.launch(googleSignInClient.signInIntent) },
                onRegisterClick = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    val prefs = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
                    prefs.edit().putBoolean("show_tutorial_on_next_launch", false).apply()
                    val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    val displayName = firebaseUser?.displayName ?: ""
                    val email = firebaseUser?.email ?: ""
                    navController.navigate(Routes.homeWithUser(displayName, email)) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                viewModel = loginViewModel
            )
        }

        composable(Routes.REGISTER) {
            val context = LocalContext.current
            val registerViewModel: RegisterViewModel = hiltViewModel()

            android.util.Log.d("GoogleSignIn", "Configurando cliente de Google Sign-In")
            val googleSignInClient = remember {
                try {
                    val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                        com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                    )
                        .requestIdToken(context.getString(R.string.default_web_client_id))
                        .requestEmail()
                        .requestProfile()
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
                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("show_tutorial_on_next_launch", true)
                        .apply()

                    val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                    val displayName = firebaseUser?.displayName ?: ""
                    val email = firebaseUser?.email ?: ""

                    navController.navigate(Routes.homeWithUser(displayName, email)) {
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
               onHelpClick = { navController.navigate(Routes.RULES) }
            )
        }

        composable(Routes.WORLDS) {
            WorldsScreenRoute(
                onWorldClick = { world ->
                    navController.navigate("levels/${world.id}/${world.title}")
                }
            )
        }

        // Rules screen route
        composable(Routes.RULES) {
            HandleBackNavigation(
                navController = navController,
                currentRoute = currentRoute,
                onBackPressed = { navController.navigateUp() }
            )

            RulesScreen()
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
