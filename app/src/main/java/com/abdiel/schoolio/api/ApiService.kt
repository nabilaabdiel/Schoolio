package com.abdiel.schoolio.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    //Login
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("email_or_phone") email_or_phone: String?,
        @Field("password") password: String?
    ): String

    //Register
    @FormUrlEncoded
    @POST("api/auth/register")
    suspend fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("phone") phone: String?,
        @Field("password") password: String?,
        @Field("password_confirmation") password_confirmation: String?
    ): String
}