package com.app.mathracer.ui.navigation

object Routes {
    const val HOME = "home"
    const val WAITING_OPPONENT = "waiting_opponent"
    const val GAME = "game"
    const val SIGNALR_TEST = "signalr_test"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"

    // Rutas con argumentos
    fun gameWithIdAndPlayer(gameId: String, playerName: String) = "game/$gameId/$playerName"
}