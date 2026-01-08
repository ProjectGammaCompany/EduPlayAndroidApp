package com.eduplay.moblie.repository.responseTypes

import com.eduplay.moblie.models.AnswerOption
import com.eduplay.moblie.models.TaskType

data class Task(
    val id: String,
    val blockId: String,
    val name: String,
    val description: String?,
    val type: TaskType,
    val options: List<AnswerOption>?,
    val files: List<String>,
    val time: Int,
    val timeStamp: String?
)