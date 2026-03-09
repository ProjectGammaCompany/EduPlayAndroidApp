package com.eduplay.moblie.repository.responseTypes

import com.google.gson.annotations.SerializedName

data class TaskFromBlock(
    @SerializedName("blockId")
    val blockId: String,
    @SerializedName("taskId")
    val taskId: String
)
