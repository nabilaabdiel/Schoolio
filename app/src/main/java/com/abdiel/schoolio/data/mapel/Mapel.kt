package com.abdiel.schoolio.data.mapel


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mapel(
    @SerializedName("admin")
    val admin: Admin?,
    @SerializedName("admin_id")
    val adminId: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("created_at_formatted")
    val createdAtFormatted: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("nama_sekolah")
    val namaSekolah: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("photo_thumbnail")
    val photoThumbnail: String?,
    @SerializedName("remember_token")
    val rememberToken: String?,
    @SerializedName("teacher_name")
    val teacherName: String?,
    @SerializedName("teacher_photo")
    val teacherPhoto: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("updated_at_formatted")
    val updatedAtFormatted: String?,
    @SerializedName("assignment")
    val assignment: List<Assignment?>?
): Parcelable