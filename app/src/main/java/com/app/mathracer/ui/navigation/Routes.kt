package com.app.mathracer.ui.navigation

object Routes {
    const val HOME = "home"
    const val WAITING_OPPONENT = "waiting_opponent"
    const val MULTIPLAYER_OPTIONS = "multiplayer_options"
    const val CREATE_MATCH = "create_match"
    const val JOIN_MATCHES = "join_matches"
    const val INVITE_FRIENDS = "invite_friends"
    const val GAME = "game"
    const val SIGNALR_TEST = "signalr_test"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"
    const val RULES = "rules"
    const val WORLDS = "worlds"
    const val LEVELS = "levels"
    const val RANKING = "ranking"


    // Rutas con argumentos
    fun gameWithIdAndPlayer(gameId: String, playerName: String) = "game/$gameId/$playerName"
}