package com.eduplay.moblie.repository.responseTypes

import com.google.gson.annotations.SerializedName

data class ShortTask(
    val id: String,
    val name: String,
    val time: Int,
    @SerializedName("is_completed")
    val isCompleted: Boolean
)