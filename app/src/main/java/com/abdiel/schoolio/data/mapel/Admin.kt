package com.abdiel.schoolio.data.mapel


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Admin(
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("photo")
    val photo: String?,
    @SerializedName("updated_at")
    val updatedAt: String?
): Parcelable