package com.eduplay.moblie.useCases.downloadTaskTypes

data class DownloadBlock(
    val blockId: String,
    val name: String,
    val blockOrder: Int,
    val isParallel: Boolean,
    val showPoints: Boolean,
    val showAnswers: Boolean,
    val partialPoints: Boolean,
    val eventId: String
)
