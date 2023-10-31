package com.abdiel.schoolio.data.slider

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ImageSlider (
    @Expose
    @SerializedName("photo")
    val photo: String?
)