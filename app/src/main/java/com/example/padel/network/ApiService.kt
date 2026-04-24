package com.example.padel.network

import com.example.padel.model.JoinMatchResponse
import com.example.padel.model.Match
import com.example.padel.model.MatchCreate
import com.example.padel.model.MatchUpdateRequest
import com.example.padel.model.PlayerCreateRequest
import com.example.padel.model.PlayerProfile
import com.example.padel.model.PlayerUpdateRequest
import com.example.padel.model.TokenResponse
import com.example.padel.model.TokenValidationResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password"
    ): TokenResponse

    @GET("matches/")
    suspend fun getMatches(
        @Header("Authorization") authorization: String
    ): List<Match>

    @GET("matches/available/")
    suspend fun getAvailableMatches(
        @Header("Authorization") authorization: String
    ): List<Match>

    @GET("matches/{matchId}")
    suspend fun getMatch(
        @Path("matchId") matchId: Int,
        @Header("Authorization") authorization: String
    ): Match

    @GET("players/profile/token/")
    suspend fun validateToken(
        @Header("Authorization") authorization: String
    ): TokenValidationResponse

    @POST("players/")
    suspend fun register(
        @Body player: PlayerCreateRequest
    ): PlayerProfile

    @PATCH("players/{playerId}")
    suspend fun updatePlayer(
        @Path("playerId") playerId: Int,
        @Header("Authorization") authorization: String,
        @Body player: PlayerUpdateRequest
    ): PlayerProfile

    @POST("matches/")
    suspend fun createMatch(
        @Header("Authorization") authorization: String,
        @Body match: MatchCreate
    ): Match

    @PATCH("matches/{matchId}")
    suspend fun updateMatch(
        @Path("matchId") matchId: Int,
        @Header("Authorization") authorization: String,
        @Body match: MatchUpdateRequest
    ): Match

    @DELETE("matches/{matchId}")
    suspend fun deleteMatch(
        @Path("matchId") matchId: Int,
        @Header("Authorization") authorization: String
    )

    @POST("matches/{matchId}/inscripciones/")
    suspend fun joinMatch(
        @Path("matchId") matchId: Int,
        @Header("Authorization") authorization: String
    ): JoinMatchResponse
}
