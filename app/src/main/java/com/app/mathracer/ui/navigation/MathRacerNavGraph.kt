package com.app.mathracer.ui.navigation

import LoginScreen
import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import com.app.mathracer.ui.components.AppTopBar
import com.app.mathracer.ui.screens.home.viewmodel.HomeViewModel
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
import com.app.mathracer.ui.screens.freepractice.FreePracticeOptionsScreen
import com.app.mathracer.ui.screens.infinite.InfiniteGameScreen
import com.app.mathracer.ui.screens.multiplayer.MultiplayerOptionsScreen
import com.app.mathracer.ui.screens.multiplayer.CreateMatchScreen
import com.app.mathracer.ui.screens.multiplayer.JoinMatchesScreen
import com.app.mathracer.ui.screens.multiplayer.InviteFriendsScreen
import com.app.mathracer.ui.screens.multiplayer.InvitationsScreen
import com.app.mathracer.ui.screens.ranking.RankingScreen
import com.app.mathracer.ui.screens.ranking.viewmodel.RankingViewModel
import kotlinx.coroutines.launch
import com.app.mathracer.ui.screens.worlds.WorldsScreen
import com.app.mathracer.ui.screens.worlds.WorldsScreenRoute
import com.app.mathracer.ui.screens.rules.RulesScreen
import com.app.mathracer.ui.screens.historyGame.HistoryGameScreen
import com.app.mathracer.data.model.User
import android.util.Log
import androidx.compose.foundation.background
import com.app.mathracer.ui.screens.chest.ChestScreen
import com.app.mathracer.ui.screens.garage.GarageScreen
import com.app.mathracer.ui.screens.garage.GarageViewModel
import com.app.mathracer.ui.screens.insufficientEnergy.InsufficientEnergyScreen
import com.app.mathracer.ui.screens.login.viewmodel.LoginViewModel
import com.app.mathracer.ui.screens.multiplayer.FriendItem
import com.app.mathracer.ui.screens.profile.viewmodel.ProfileViewModel
import com.app.mathracer.ui.screens.shop.ShopScreen
import com.app.mathracer.ui.screens.shop.viewmodel.ShopViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.app.mathracer.data.repository.GarageRepository
import java.net.URLEncoder


@Composable
fun MathRacerNavGraph(
    navController: NavHostController
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        homeViewModel.onEnterHome()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                homeViewModel.onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(key1 = com.app.mathracer.data.CurrentUser.user?.id) {
        val playerId = com.app.mathracer.data.CurrentUser.user?.id ?: 0
        if (playerId > 0) {
            try {
                val repo = GarageRepository()
                val charsRes = repo.getCharacters(playerId)
                val bgsRes = repo.getBackgrounds(playerId)
                val carsRes = repo.getCars(playerId)
                val activeChar = charsRes.getOrNull()?.activeItem
                val activeBg = bgsRes.getOrNull()?.activeItem
                val activeCar = carsRes.getOrNull()?.activeItem
                com.app.mathracer.data.CurrentUser.activeCharacterProductId = activeChar?.productId
                com.app.mathracer.data.CurrentUser.activeBackgroundProductId = activeBg?.productId
                com.app.mathracer.data.CurrentUser.activeVehicleProductId = activeCar?.productId
                try {
                    com.app.mathracer.data.UserState.setActiveCharacter(activeChar?.productId)
                    com.app.mathracer.data.UserState.setActiveBackground(activeBg?.productId)
                    com.app.mathracer.data.UserState.setActiveVehicle(activeCar?.productId)
                } catch (_: Exception) {}
            } catch (e: Exception) {
                Log.w("MathRacerNavGraph", "Failed to init user assets", e)
            }
        }
    }

    LaunchedEffect(currentRoute) {
        val route = currentRoute ?: ""
        val hideTop = route.contains("game") || route.contains("infinite_game") || route.contains(Routes.LOGIN) || route.contains(Routes.REGISTER)
        if (!hideTop) {
            homeViewModel.refreshEnergy()
        }
    }
    Scaffold(
        topBar = {
            val route = currentRoute ?: ""
            val hideTop = route.contains("game") || route.contains("infinite_game") || route.contains(Routes.LOGIN) || route.contains(Routes.REGISTER)
            if (!hideTop) {
                AppTopBar(
                    energyState = homeState.energy,
                    onShopClick = { navController.navigate(Routes.SHOP) },
                    onEnergyClick = { navController.navigate(Routes.INSUFFICIENT_ENERGY) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    showBack = navController.previousBackStackEntry != null,
                    onBack = { navController.navigateUp() }
                )
            }
        }
    , containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .navigationBarsPadding()
                .background(androidx.compose.ui.graphics.Color(0xFF1E1E1E))
        ) {
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
                            navController.navigate(Routes.infiniteGameWithId("local"))
                        },
                        onShopClick = {
                            navController.navigate(Routes.SHOP)
                        },
                        onGarageClick = {
                            navController.navigate(Routes.GARAGE)
                        },
                        onStatsClick = {
                            navController.navigate(Routes.RANKING)
                        },
                        onProfileClick = { navController.navigate(Routes.PROFILE) },
                        onTutorialComplete = { navController.navigate(Routes.CHEST) }
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

                composable(
                    route = "chest_world/{worldId}",
                    arguments = listOf(navArgument("worldId") { type = NavType.IntType })
                ) { backStackEntry ->
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    val worldIdArg = backStackEntry.arguments?.getInt("worldId") ?: 0
                    val ctx = LocalContext.current

                    ChestScreen(onContinue = {
                        try {
                            ctx.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                .edit().putBoolean("world_reward_claimed_$worldIdArg", true).apply()
                        } catch (_: Exception) {
                        }
                        navController.navigateUp()
                    }, chestType = "world")
                }

                composable(Routes.CHEST) {
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    ChestScreen(
                        onContinue = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        },
                        chestType = "tutorial"
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
                        onInvitationInbox = {
                            navController.navigate(Routes.INVITATION_INBOX)
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


                composable(
                    route = "infinite_game/{gameId}",
                    arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                ) { backStackEntry ->
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                    InfiniteGameScreen(gameId = gameId, onExit = { navController.navigateUp() })
                }

                composable(Routes.INVITATION_INBOX) {
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    InvitationsScreen(
                        onJoinAndWait = { navController.navigate(Routes.WAITING_OPPONENT) },
                        onBack = { navController.navigateUp() }
                    )
                }

                composable(Routes.INVITE_FRIENDS) {
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    val profileViewModel: ProfileViewModel = hiltViewModel()
                    val profileState by profileViewModel.uiState.collectAsState()
                    val scope = rememberCoroutineScope()

                    val inviteList = profileState.remoteFriends.map { remote ->
                        com.app.mathracer.ui.screens.multiplayer.FriendItem(
                            id = "${remote.id}",
                            name = remote.name,
                            points = remote.points
                        )
                    }

                    InviteFriendsScreen(
                        friends = inviteList,
                        onInvite = { friendId, difficulty, resultType ->
                            scope.launch {
                                try {
                                    val idInt = friendId.toIntOrNull() ?: return@launch
                                    val resp =
                                        com.app.mathracer.data.repository.GameInvitationRepository.sendInvitation(
                                            idInt,
                                            difficulty,
                                            resultType
                                        )
                                    if (resp.isSuccessful) {
                                        navController.navigate(Routes.WAITING_OPPONENT) {
                                            popUpTo(Routes.INVITE_FRIENDS) { inclusive = true }
                                        }
                                    } else {
                                        android.util.Log.e(
                                            "InviteFriends",
                                            "Failed to send invitation: ${resp.code()} ${
                                                resp.errorBody()?.string()
                                            }"
                                        )
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e(
                                        "InviteFriends",
                                        "Exception sending invitation",
                                        e
                                    )
                                }
                            }
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

                composable(Routes.SHOP) {
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    val viewModel: ShopViewModel = hiltViewModel()
                    ShopScreen(viewModel = viewModel, onBackClick = { navController.navigateUp() })
                }

                composable(Routes.GARAGE) {
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    val viewModel: GarageViewModel = hiltViewModel()
                    GarageScreen(viewModel = viewModel, onBack = { navController.navigateUp() })
                }


                composable(Routes.CREATE_MATCH) {
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

            val createMatchViewModel: com.app.mathracer.ui.screens.multiplayer.viewmodel.CreateMatchViewModel = hiltViewModel()

            CreateMatchScreen(
                onCreateMatch = { name, privacy, difficulty, resultType, password ->
                    val diffNorm = when (difficulty.lowercase()) {
                        "fácil", "facil" -> "facil"
                        "medio" -> "medio"
                        "difícil", "dificil" -> "dificil"
                        else -> difficulty.lowercase()
                    }
                    val resultNorm = if (resultType.equals("Mayor", ignoreCase = true)) "MAYOR" else "MENOR"

                    createMatchViewModel.createMatch(name, privacy, diffNorm, resultNorm, password) { result ->
                        if (result.isSuccess) {
                            navController.navigate(Routes.WAITING_OPPONENT)
                        } else {
                            android.util.Log.e("CreateMatchNav", "Failed to create match: ${result.exceptionOrNull()?.message}")
                        }
                    }
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

            val joinViewModel: com.app.mathracer.ui.screens.multiplayer.viewmodel.JoinMatchesViewModel = hiltViewModel()
            val gamesState by joinViewModel.games.collectAsState()
            val isLoading by joinViewModel.isLoading.collectAsState()
            val errorMessage by joinViewModel.error.collectAsState()

            LaunchedEffect(Unit) {
                // Fetch available games when entering the screen
                joinViewModel.fetchAvailableGames(publicOnly = false)
            }

            // Map API DTOs into local MatchItem for the composable
            val matches = gamesState.map { dto ->
                com.app.mathracer.ui.screens.multiplayer.MatchItem(
                    id = dto.gameId.toString(),
                    name = dto.gameName,
                    difficulty = dto.difficulty ?: "",
                    privacy = if (dto.isPrivate) "Privada" else "Pública",
                    requiresPassword = dto.requiresPassword
                )
            }

            JoinMatchesScreen(
                matches = matches,
                onJoinConfirmed = { matchId, password ->
                    val id = matchId.toIntOrNull()
                    if (id != null) {
                        joinViewModel.joinGame(id, password) { res ->
                            if (res.isSuccess) {
                                navController.navigate(Routes.WAITING_OPPONENT)
                            } else {
                                android.util.Log.e("JoinMatchesNav", "Failed to join game ${id}: ${res.exceptionOrNull()?.message}")
                            }
                        }
                    } else {
                        android.util.Log.e("JoinMatchesNav", "Invalid match id: $matchId")
                    }
                },
                onBack = { navController.navigateUp() },
                errorMessage = errorMessage,
                onClearError = { joinViewModel.clearError() }
            )
        }

                composable(Routes.LOGIN) {
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    val context = LocalContext.current
                    val loginViewModel: com.app.mathracer.ui.screens.login.viewmodel.LoginViewModel =
                        hiltViewModel()

                    android.util.Log.d(
                        "GoogleSignIn",
                        "Configurando cliente de Google Sign-In (Login)"
                    )
                    val googleSignInClient = remember {
                        try {
                            val gso =
                                com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                    com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                                )
                                    .requestIdToken(context.getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .requestProfile()
                                    .build()
                            android.util.Log.d(
                                "GoogleSignIn",
                                "GSO configurado correctamente (Login)"
                            )
                            com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(
                                context,
                                gso
                            )
                        } catch (e: Exception) {
                            android.util.Log.e("GoogleSignIn", "Error al configurar GSO (Login)", e)
                            throw e
                        }
                    }

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        android.util.Log.d(
                            "GoogleSignIn",
                            "Resultado recibido (Login): ${result.resultCode}"
                        )
                        when (result.resultCode) {
                            android.app.Activity.RESULT_OK -> {
                                android.util.Log.d(
                                    "GoogleSignIn",
                                    "Result OK, procesando resultado... (Login)"
                                )
                                try {
                                    if (result.data == null) {
                                        android.util.Log.e(
                                            "GoogleSignIn",
                                            "Intent de resultado es null (Login)"
                                        )
                                        return@rememberLauncherForActivityResult
                                    }
                                    val task = com.google.android.gms.auth.api.signin.GoogleSignIn
                                        .getSignedInAccountFromIntent(result.data)
                                    try {
                                        val account = task.result
                                        android.util.Log.d(
                                            "GoogleSignIn",
                                            "Cuenta obtenida (Login): ${account.email}, ID: ${account.id}"
                                        )
                                    } catch (e: Exception) {
                                        android.util.Log.e(
                                            "GoogleSignIn",
                                            "Error al obtener cuenta de manera síncrona (Login)",
                                            e
                                        )
                                    }
                                    loginViewModel.handleGoogleSignInResult(result.data)
                                } catch (e: Exception) {
                                    android.util.Log.e(
                                        "GoogleSignIn",
                                        "Error al procesar resultado (Login)",
                                        e
                                    )
                                    e.printStackTrace()
                                }
                            }

                            android.app.Activity.RESULT_CANCELED -> {
                                android.util.Log.d(
                                    "GoogleSignIn",
                                    "Usuario canceló el inicio de sesión (Login)"
                                )
                            }

                            else -> {
                                android.util.Log.e(
                                    "GoogleSignIn",
                                    "Error desconocido: ${result.resultCode} (Login)"
                                )
                            }
                        }
                    }

                    LoginScreen(
                        onLoginWithGoogle = { launcher.launch(googleSignInClient.signInIntent) },
                        onRegisterClick = { navController.navigate(Routes.REGISTER) },
                        onLoginSuccess = { hasProductsAssigned ->
                            val firebaseUser = FirebaseAuth.getInstance().currentUser
                            val displayName = firebaseUser?.displayName ?: ""
                            val email = firebaseUser?.email ?: ""
                            val metadata = firebaseUser?.metadata
                            val isFirstLogin = metadata != null &&
                                    metadata.creationTimestamp == metadata.lastSignInTimestamp
                            val prefs =
                                context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            if (!hasProductsAssigned) {
                                prefs.edit().putBoolean("show_tutorial_on_next_launch", true)
                                    .apply()
                            } else {
                                prefs.edit().putBoolean("show_tutorial_on_next_launch", false)
                                    .apply()
                            }

                            navController.navigate(Routes.homeWithUser(displayName, email)) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
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
                            val gso =
                                com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                                    com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                                )
                                    .requestIdToken(context.getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .requestProfile()
                                    .build()
                            android.util.Log.d("GoogleSignIn", "GSO configurado correctamente")
                            com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(
                                context,
                                gso
                            )
                        } catch (e: Exception) {
                            android.util.Log.e("GoogleSignIn", "Error al configurar GSO", e)
                            throw e
                        }
                    }

                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        Log.d("GoogleSignIn", "Resultado recibido: ${result.resultCode}")
                        when (result.resultCode) {
                            Activity.RESULT_OK -> {
                                Log.d("GoogleSignIn", "Result OK, procesando resultado...")
                                try {
                                    if (result.data == null) {
                                        Log.e("GoogleSignIn", "Intent de resultado es null")
                                        return@rememberLauncherForActivityResult
                                    }

                                    val task = GoogleSignIn
                                        .getSignedInAccountFromIntent(result.data)

                                    try {
                                        val account = task.result
                                        Log.d(
                                            "GoogleSignIn",
                                            "Cuenta obtenida: ${account.email}, ID: ${account.id}"
                                        )
                                    } catch (e: Exception) {
                                        Log.e(
                                            "GoogleSignIn",
                                            "Error al obtener cuenta de manera síncrona",
                                            e
                                        )
                                    }

                                    registerViewModel.handleGoogleSignInResult(result.data)
                                } catch (e: Exception) {
                                    Log.e("GoogleSignIn", "Error al procesar resultado", e)
                                    e.printStackTrace()
                                }
                            }

                            Activity.RESULT_CANCELED -> {
                                Log.d("GoogleSignIn", "Usuario canceló el inicio de sesión")
                            }

                            else -> {
                                Log.e("GoogleSignIn", "Error desconocido: ${result.resultCode}")
                            }
                        }
                    }

                    RegisterScreen(
                        viewModel = registerViewModel,
                        onGoogleSignIn = { launcher.launch(googleSignInClient.signInIntent) },
                        onNavigateToLogin = { navController.navigate(Routes.LOGIN) },
                        onRegisterSuccess = {
                            context.getSharedPreferences("app_prefs", MODE_PRIVATE)
                                .edit()
                                .putBoolean("show_tutorial_on_next_launch", true)
                                .apply()

                            val firebaseUser = FirebaseAuth.getInstance().currentUser
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

                    val context = LocalContext.current

                    ProfileScreen(
                        onHelpClick = { navController.navigate(Routes.RULES) },
                        onLogout = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.WORLDS) {
                    WorldsScreenRoute(
                        onWorldClick = { world ->
                            try {
                                val opsPlain = world.operations.joinToString(",")
                                val ops = Base64.encodeToString(
                                    opsPlain.toByteArray(Charsets.UTF_8),
                                    Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
                                )
                                val name = URLEncoder.encode(world.name, "UTF-8")
                                navController.navigate("levels/${world.id}/$name/$ops")
                            } catch (e: Exception) {
                                val name = URLEncoder.encode(world.name, "UTF-8")
                                navController.navigate("levels/${world.id}/$name/")
                            }
                        }
                    )
                }

                composable(Routes.RULES) {
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    RulesScreen()
                }

                composable(
                    route = "levels/{worldId}/{worldName}/{worldOperations}",
                    arguments = listOf(
                        navArgument("worldId") { type = NavType.IntType },
                        navArgument("worldName") { type = NavType.StringType },
                        navArgument("worldOperations") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = { navController.navigateUp() }
                    )

                    val viewModel: LevelsViewModel = hiltViewModel()
                    val worldId = backStackEntry.arguments?.getInt("worldId") ?: 0
                    val worldName = backStackEntry.arguments?.getString("worldName") ?: ""
                    val encodedOps = backStackEntry.arguments?.getString("worldOperations") ?: ""

                    LaunchedEffect(worldId) {
                        viewModel.loadLevelsForWorld(worldId, worldName)
                    }

                    LevelsScreen(
                        viewModel = viewModel,
                        worldId = worldId,
                        worldOperationsEncoded = encodedOps,
                        onLevelClick = { levelId, resultType ->
                            viewModel.checkEnergyBeforePlay(
                                onHasEnergy = {
                                    navController.navigate(
                                        Routes.historyGameWithLevelId(levelId, resultType)
                                    )
                                },
                                onNoEnergy = {
                                    navController.navigate("insufficient_energy")
                                }
                            )
                        },
                        onObtenerRecompensaClick = { wid ->
                            navController.navigate("chest_world/$wid")
                        }
                    )

                }

                composable("insufficient_energy") {
                    InsufficientEnergyScreen(
                        onBackToLevels = {
                            navController.navigate(Routes.WORLDS) {
                                popUpTo(Routes.HOME)
                            }
                        }
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

                composable(
                    route = "history_game/{levelId}/{resultType}",
                    arguments = listOf(
                        navArgument("levelId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    HandleBackNavigation(
                        navController = navController,
                        currentRoute = currentRoute,
                        onBackPressed = {
                            navController.navigateUp()
                        }
                    )

                    val levelId = backStackEntry.arguments?.getInt("levelId") ?: 0
                    val resultType = backStackEntry.arguments?.getString("resultType") ?: ""

                    val playerName = FirebaseAuth.getInstance()
                        .currentUser?.displayName ?: "Jugador"

                    HistoryGameScreen(
                        levelId = levelId,
                        playerName = playerName,
                        resultType = resultType,
                        onNavigateBack = {
                            navController.navigateUp()
                        },
                        onPlayAgain = { newLevelId ->
                            navController.navigate(
                                Routes.historyGameWithLevelId(
                                    newLevelId,
                                    resultType
                                )
                            ) {
                                popUpTo(Routes.HOME)
                            }
                        },
                        onNoEnergy = {
                            navController.navigate(Routes.INSUFFICIENT_ENERGY)
                        }
                    )
                }
            }
        }
    }
}