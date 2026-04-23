package com.eduplay.moblie.useCases.downloadTaskTypes

import com.eduplay.moblie.repository.webrepository.responseTypes.Task

data class DownloadTask(
    val taskId: String,
    val blockId: String,
    val name: String,
    val description: String,
    val type: Int,
    val files: List<String>,
    val time: Int,
    val points: Int,
    val partialPoints: Boolean,
    val taskOrder: Int
)