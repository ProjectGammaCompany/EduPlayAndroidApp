package com.eduplay.moblie.repository.responseTypes

import java.time.LocalDateTime

data class EventResponse(
    val id: String,
    val title: String,
    val description: String,
    val rate: Double,
    val favorite: Boolean,
    val lastEditionDate: LocalDateTime, // LocalDateTime
    val tags: List<String>,
    val cover: String
)