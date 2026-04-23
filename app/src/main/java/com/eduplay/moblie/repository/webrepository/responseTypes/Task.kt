package com.eduplay.moblie.repository.webrepository.responseTypes

import com.eduplay.moblie.models.AnswerOption
import com.eduplay.moblie.repository.localrepository.entity.OptionEntity
import com.eduplay.moblie.repository.localrepository.entity.TaskEntity
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class Task(
    @SerializedName("taskId")
    val id: String,
    val blockId: String,
    val name: String,
    val description: String?,
    val type: Int,
    val options: List<AnswerOption>?,
    val files: List<TaskFile>,
    val time: Int?,
    @SerializedName("timestamp")
    val timeStamp: String?
) {
    constructor(task: TaskEntity, taskOptions: List<OptionEntity>, startTime: String?) : this(
        id = task.id,
        blockId = task.blockId,
        name = task.name,
        description = task.description,
        type = task.type,
        options = taskOptions.map {
            AnswerOption(
                id = it.id,
                value = it.value
            )
        },
        files = Gson().fromJson<List<TaskFile>>(task.files, object : TypeToken<List<TaskFile>>() {}.type),
        time = task.time,
        timeStamp = startTime
    )

    @Serializable
    data class TaskFile(
        val url: String,
        val name: String,
    )
}