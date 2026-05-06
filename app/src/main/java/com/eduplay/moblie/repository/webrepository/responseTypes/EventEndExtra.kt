package com.eduplay.moblie.repository.webrepository.responseTypes

data class EventEndExtra(
    val id: String,
    val timeLeft: String, // "hour" | "day"
    val notStartedFavorite: Boolean,
    val eventName: String
)