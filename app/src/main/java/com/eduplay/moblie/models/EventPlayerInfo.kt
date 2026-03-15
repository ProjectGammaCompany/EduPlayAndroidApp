package com.eduplay.moblie.models

import com.eduplay.moblie.repository.responseTypes.Author

data class EventPlayerInfo(
    val title: String,
    val description: String,
    val rate: Float,
    val favorite: Boolean,
    val startDate: String?,
    val endDate: String?,
    val tags: List<EventTag>,
    val cover: String,
    val status: String,
    val lastEditionDate: String,
    val authors: List<Author>
)