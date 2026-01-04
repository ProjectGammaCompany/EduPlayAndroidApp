package com.eduplay.moblie.models

data class EventOwnerInfo(
    val description: String,
    val tags: List<String>,
    val cover: String?,
    val startDate: String?,
    val endDate: String?,
    val private: Boolean,
    val lastEditionDate: String,
    val groups: List<String>,
    val rating: Float,
    val collaboratos: List<String>?
)