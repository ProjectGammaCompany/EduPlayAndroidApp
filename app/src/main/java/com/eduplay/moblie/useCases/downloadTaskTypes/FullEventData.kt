package com.eduplay.moblie.useCases.downloadTaskTypes

import com.eduplay.moblie.repository.localrepository.entity.CorrectAnswerEntity
import com.eduplay.moblie.repository.webrepository.responseTypes.Task

data class FullEventData(
    val event: DownloadEvent,
    val blocks: List<DownloadBlock>,
    val conditions: List<DownloadCondition>,
    val groups: List<DownloadGroup>,
    val tasks: List<DownloadTask>,
    val options: List<DownloadOption>,
    val correctAnswers: List<DownloadCorrectAnswer>,
    val files : List<Task.TaskFile>
)