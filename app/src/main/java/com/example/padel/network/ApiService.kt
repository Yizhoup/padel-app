package com.example.padel.network

import com.example.padel.model.Match
import com.example.padel.model.MatchCreate
import com.example.padel.model.TokenResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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

    @POST("matches/")
    suspend fun createMatch(
        @Header("Authorization") authorization: String,
        @Body match: MatchCreate
    ): Match
}