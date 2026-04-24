package com.example.padel.navigation

object PadelDestinations {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val MY_MATCHES = "my_matches"
    const val PROFILE = "profile"
    const val CREATE_MATCH = "create_match"
    const val MATCH_DETAIL = "match/{matchId}"

    fun matchDetail(matchId: String) = "match/$matchId"
}
