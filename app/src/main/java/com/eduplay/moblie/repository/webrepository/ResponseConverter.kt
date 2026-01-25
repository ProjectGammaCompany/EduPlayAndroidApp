package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.responseTypes.EventListResponse
import com.eduplay.moblie.repository.responseTypes.EventResponse
import jakarta.inject.Singleton

@Singleton
object ResponseConverter {
    fun convertListEventResponseToListQuestShortInfo(listResponse: EventListResponse): List<QuestShortInfo> {
        return listResponse.events.map { event -> convertEventResponseToQuestShortInfo(event) }
    }

    fun convertEventResponseToQuestShortInfo(eventResponse: EventResponse): QuestShortInfo {
        return QuestShortInfo(
            id = eventResponse.id,
            name = eventResponse.title,
            description = eventResponse.description,
            imageUrl = eventResponse.cover,
            rate = eventResponse.rate,
            isFavourite = eventResponse.favorite,
            tags = eventResponse.tags,
            isDownloaded = false // TODO("проверка на скачанность")
        )
    }
}