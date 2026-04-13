package com.eduplay.moblie.useCases.downloadTaskTypes

data class DownloadCondition(
    val conditionId: String,
    val prevBlockId: String,
    val nextBlockId: String,
    val groupName: String?,
    val min: Int,
    val max: Int
)
