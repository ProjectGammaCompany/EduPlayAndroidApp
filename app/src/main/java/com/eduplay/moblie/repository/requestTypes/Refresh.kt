package com.eduplay.moblie.repository.requestTypes

import com.google.gson.annotations.SerializedName

data class Refresh(
    @SerializedName("RefreshToken")
    val refreshToken: String
)