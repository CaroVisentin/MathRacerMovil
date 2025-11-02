package com.app.mathracer.ui.navigation

import LoginScreen
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.app.mathracer.ui.RegisterScreen
import com.app.mathracer.ui.screens.home.HomeScreen
import com.app.mathracer.ui.screens.game.GameScreen
import com.app.mathracer.ui.screens.levels.LevelsScreen
import com.app.mathracer.ui.screens.levels.viewmodel.LevelsViewModel
import com.app.mathracer.ui.screens.profile.ProfileScreen
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
