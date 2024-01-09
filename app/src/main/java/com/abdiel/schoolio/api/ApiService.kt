package com.abdiel.schoolio.api

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {

    //Login
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("email_or_phone") email_or_phone: String?,
        @Field("password") password: String?,
        @Field("device_token") device_token: String?
    ): String

    //Logout
    @POST("api/auth/logout")
    suspend fun logout(): String

    //Get Profile
    @GET("api/auth/me")
    suspend fun getProfile(): String

    //Update Profile
    @FormUrlEncoded
    @POST("api/user/edit-profile")
    suspend fun editProfile(
        @Field("name") name: String?,
    ): String

    //Update Profile Photo
    @Multipart
    @POST("api/user/edit-profile")
    suspend fun editProfileImg(
        @Part("name") name: String?,
        @Part photo: MultipartBody.Part?
    ): String

    //Change Password
    @FormUrlEncoded
    @POST("api/user/edit-password")
    suspend fun changePassword(
        @Field("new_password") new_password: String,
        @Field("password_confirmation") password_confirmation: String
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

    //Index Mapel
    @GET("api/mapel/")
    suspend fun indexSubject(
    ): String

    //By-Id Mapel
    @GET("mapel/{Id}")
    suspend fun byIdSubject(
        @Path("id") id: String?,
    ): String
}