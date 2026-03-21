package com.eduplay.moblie.models

data class EventOwnerInfo(
    val title: String,
    val description: String,
    val tags: List<EventTag>,
    val cover: String?,
    val startDate: String?,
    val endDate: String?,
    val private: Boolean,
    val password: String?,
    val lastEditionDate: String,
    val groupEvent: Boolean,
    val groupNames: List<String>,
    val groups: List<EventGroup>?,
    val eventRating: Float?,
    val collaboratos: List<String>,
    val allowDownloading: Boolean
)

data class EventGroup(
    val id: String,
    val login: String,
    val password: String
)