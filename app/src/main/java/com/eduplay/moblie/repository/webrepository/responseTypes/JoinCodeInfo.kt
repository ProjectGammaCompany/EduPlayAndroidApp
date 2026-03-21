package com.eduplay.moblie.repository.responseTypes

import com.google.gson.annotations.SerializedName

data class JoinCodeInfo(
    @SerializedName("joinCode")
    val joinCode: String,
    @SerializedName("expiresAt")
    val expiresAt: String
)