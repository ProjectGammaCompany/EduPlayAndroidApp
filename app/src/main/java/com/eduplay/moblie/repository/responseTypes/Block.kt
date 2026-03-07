package com.eduplay.moblie.repository.responseTypes

import com.google.gson.annotations.SerializedName

data class Block(
    val id: String,
    val name: String,
    val tasks: List<ShortTask>
)