package com.eduplay.moblie.models

data class EventPlayerInfo(
    val title: String,
    val description: String,
    val rate: Float,
    val favorite: Boolean,
    val startDate: String?,
    val endDate: String?,
    val tags: List<String>,
    val cover: String,
    val status: EventStatus,
    val lastEditionDate: String,
    val completed: Boolean,
    val authors: List<String>
)