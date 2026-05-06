package com.eduplay.moblie.models

import com.eduplay.moblie.repository.localrepository.entity.EventEntity
import com.eduplay.moblie.repository.responseTypes.EventResponse
import com.google.gson.Gson

data class QuestShortInfo(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val rate: Double,
    val isFavourite: Boolean,
    val tags: List<EventTag>,
    val isDownloaded: Boolean
) {
    constructor(event: EventEntity) : this(
        event.id,
        event.title,
        event.description,
        event.cover,
        0.0,
        false,
        Gson().fromJson<List<String>>(event.tags, List::class.java)
            .mapIndexed { idx, it -> EventTag(idx.toString(), it) },
        true
    )

    constructor(eventResponse: EventResponse, isDownloaded: Boolean) : this(
        id = eventResponse.id,
        name = eventResponse.title,
        description = eventResponse.description,
        imageUrl = eventResponse.cover,
        rate = eventResponse.rate,
        isFavourite = eventResponse.favorite,
        tags = eventResponse.tags,
        isDownloaded = isDownloaded
    )
}