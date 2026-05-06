package com.eduplay.moblie.repository

import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventTagList
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.repository.webrepository.responseTypes.EventStage
import java.time.LocalDateTime

interface Repository {
    suspend fun getRole(eventId: String): EventRole
    suspend fun getPlayerEventInfo(eventId: String): EventPlayerInfo
    suspend fun getOwnerEventInfo(eventId: String): EventOwnerInfo
    suspend fun getProfile(): ProfileInfo
    suspend fun getNextStage(eventId: String): EventStage
    suspend fun postTaskStartTime(
        eventId: String,
        blockId: String,
        taskId: String,
        startTime: LocalDateTime
    ): Boolean

    suspend fun postTaskAnswer(
        eventId: String,
        blockId: String,
        taskId: String,
        answers: List<String>
    ): AnswerResult

    suspend fun postTaskChoice(eventId: String, blockId: String, taskId: String): Boolean
    suspend fun getResults(eventId: String): PlayerStats
    suspend fun addToFavourite(eventId: String, isFavorite: Boolean): Boolean
    suspend fun getTags(): EventTagList
    suspend fun enterGroupEvent(eventId: String, groupName: String, groupPassword: String)
    suspend fun getNotifications(page: Int, maxOnPage: Int): List<NotificationData>
}