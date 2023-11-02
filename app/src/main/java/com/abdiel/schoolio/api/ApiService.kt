package com.abdiel.schoolio.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    //Login
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("phone") phone: String?,
        @Field("password") password: String?
    ): String

    //Register
    @FormUrlEncoded
    @POST("api/auth/register")
    suspend fun register(
        @Field("username") username: String?,
        @Field("email") email: String?,
        @Field("phone") phone: String?,
        @Field("password") password: String?,
        @Field("confirm_password") confirm_password: String?
    ): String
}