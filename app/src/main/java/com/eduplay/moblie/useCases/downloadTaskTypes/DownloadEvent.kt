package com.eduplay.moblie.useCases.downloadTaskTypes

data class DownloadEvent(
    val eventId: String,
    val title: String,
    val description: String,
    val tags: List<String>,
    val cover: String,
    val startDate: String,
    val endDate: String,
    val lastEditionDate: String,
    val groupEvent: Boolean,
    val authorId: List<String>
)
