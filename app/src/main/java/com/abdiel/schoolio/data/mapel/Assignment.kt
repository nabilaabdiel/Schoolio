package com.abdiel.schoolio.data.mapel


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Assignment(
    @SerializedName("admin_id")
    val adminId: String?,
    @SerializedName("comment")
    val comment: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("created_at_formatted")
    val createdAtFormatted: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("due_date")
    val dueDate: String?,
    @SerializedName("id")
    val id: String?,
//    @SerializedName("lampiran")
//    val lampiran: List<Any?>?,
    @SerializedName("lampiran_urls")
    val lampiranUrls: String?,
    @SerializedName("mata_pelajaran_id")
    val mataPelajaranId: String?,
    @SerializedName("score")
    val score: String?,
//    @SerializedName("submission")
//    val submission: List<Any?>?,
    @SerializedName("submission_url")
    val submissionUrl: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("updated_at_formatted")
    val updatedAtFormatted: String?
) :Parcelable