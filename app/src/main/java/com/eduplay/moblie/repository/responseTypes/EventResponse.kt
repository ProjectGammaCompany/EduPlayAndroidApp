package com.eduplay.moblie.repository.responseTypes

import com.eduplay.moblie.models.EventTag
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class EventResponse(
    val id: String,
    val title: String,
    val description: String,
    val rate: Double,
    val favorite: Boolean,
    @SerializedName("last_edition_date")
    val lastEditionDate: String, // LocalDateTime
    val tags: List<EventTag>,
    val cover: String
)