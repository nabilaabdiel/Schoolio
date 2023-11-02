package com.abdiel.schoolio.data.user


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("device_token")
    val deviceToken: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: Any?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("photo")
    val photo: Any?,
    @SerializedName("updated_at")
    val updatedAt: String?
)