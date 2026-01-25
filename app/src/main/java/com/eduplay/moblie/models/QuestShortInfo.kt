package com.eduplay.moblie.models

data class QuestShortInfo(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rate: Double,
    val isFavourite: Boolean,
    val tags: List<String>,
    val isDownloaded: Boolean
)