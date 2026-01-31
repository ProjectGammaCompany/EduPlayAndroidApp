package com.eduplay.moblie.repository.responseTypes

import com.eduplay.moblie.models.AnswerOption
import com.eduplay.moblie.models.TaskType
import com.google.gson.annotations.SerializedName

data class Task(
    val id: String,
    val blockId: String,
    val name: String,
    val description: String?,
    val type: Int,
    val options: List<AnswerOption>?,
    val files: List<String>,
    val time: Int,
    @SerializedName("time_stamp")
    val timeStamp: String?
)