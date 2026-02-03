package com.eduplay.moblie.models

data class EventOwnerInfo(
    val description: String,
    val tags: List<EventTag>,
    val cover: String?,
    val startDate: String?,
    val endDate: String?,
    val private: Boolean,
    val lastEditionDate: String,
    val groupNames: List<String>,
    val rating: Float,
    val collaboratos: List<String>?
)