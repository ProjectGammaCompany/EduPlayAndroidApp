package com.eduplay.moblie.models

import com.google.gson.annotations.SerializedName

data class EventOwnerInfo(
    val title: String,
    val description: String,
    val tags: List<String>,
    val cover: String?,
    val startDate: String?,
    val endDate: String?,
    val private: Boolean,
    val password: String?,
    val lastEditionDate: String,
    val groupEvent: Boolean,
    val groups: List<EventGroup>?,
    val eventRating: Float?,
    val collaborators : List<String>,
    val allowDownloading: Boolean
)

data class EventGroup(
    val id: String,
    val login: String,
    val password: String
)