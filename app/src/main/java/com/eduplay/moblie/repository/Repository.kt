package com.eduplay.moblie.repository

import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.EventStage
import java.time.LocalDateTime

interface Repository {
    suspend fun login(auth: Auth): AuthResult
    suspend fun logout(): Boolean
    suspend fun getRole(eventId: String): EventRole
    suspend fun getPlayerEventInfo(eventId: String): EventPlayerInfo
    suspend fun getOwnerEventInfo(eventId: String): EventOwnerInfo
    suspend fun getFavouriteEvents(page: Int, maxOnPage: Int): List<QuestShortInfo>
    suspend fun getCreatedEvents(page: Int, maxOnPage: Int): List<QuestShortInfo>
    suspend fun getCompletedEvents(page: Int, maxOnPage: Int): List<QuestShortInfo>
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

    suspend fun register(auth: RegistrationData): AuthResult
    suspend fun getEvents(
        page: Int,
        tags: List<String>? = null,
        decliningRating: Boolean = false,
        active: Boolean = false,
        favorites: Boolean = false,
        title: String = ""
    ): List<QuestShortInfo>
}