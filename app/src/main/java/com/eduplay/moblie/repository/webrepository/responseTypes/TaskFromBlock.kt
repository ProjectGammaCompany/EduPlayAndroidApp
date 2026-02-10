package com.eduplay.moblie.repository.responseTypes

import com.google.gson.annotations.SerializedName

data class TaskFromBlock(
    @SerializedName("block_id")
    val blockId: String,
    @SerializedName("task_id")
    val taskId: String
)
