package com.abdiel.schoolio.data.assignment

import android.os.Parcelable
import com.abdiel.schoolio.data.mapel.Admin
import com.abdiel.schoolio.data.mapel.Assignment
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class ByIdMapel(
    @SerializedName("name")
    val name: String?,
    @SerializedName("admin_id")
    val adminId: Int?,
    @SerializedName("photo_thumbnail")
    val photoThumbnail: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("nama_sekolah")
    val namaSekolah: String?,
    @SerializedName("assignment")
    val assignment: List<Assignment?>?

) : Parcelable