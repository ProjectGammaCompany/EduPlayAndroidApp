package com.eduplay.moblie.repository.localrepository

import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.Repository
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.EventStage
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import jakarta.inject.Inject
import java.time.LocalDateTime

class LocalRepository @Inject constructor(eventDatabase: EventDatabase): Repository {
    override suspend fun getEvents(page: Int): List<QuestShortInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getRole(eventId: String): EventRole {
        TODO("Not yet implemented")
    }

    override suspend fun getPlayerEventInfo(eventId: String): EventPlayerInfo {
        TODO("Not yet implemented")
    }

    override suspend fun getOwnerEventInfo(eventId: String): EventOwnerInfo {
        TODO("Not yet implemented")
    }

    override suspend fun getFavouriteEvents(
        page: Int,
        maxOnPage: Int
    ): List<QuestShortInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getCreatedEvents(
        page: Int,
        maxOnPage: Int
    ): List<QuestShortInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getCompletedEvents(
        page: Int,
        maxOnPage: Int
    ): List<QuestShortInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getProfile(): ProfileInfo {
        TODO("Not yet implemented")
    }

    override suspend fun getNextStage(eventId: String): EventStage {
        TODO("Not yet implemented")
    }

    override suspend fun postTaskStartTime(
        eventId: String,
        blockId: String,
        taskId: String,
        startTime: LocalDateTime
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun postTaskAnswer(
        eventId: String,
        blockId: String,
        taskId: String,
        answers: List<String>
    ): AnswerResult {
        TODO("Not yet implemented")
    }

    override suspend fun postTaskChoice(
        eventId: String,
        blockId: String,
        taskId: String
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getResults(eventId: String): PlayerStats {
        TODO("Not yet implemented")
    }

    override suspend fun addToFavourite(
        eventId: String,
        isFavorite: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }

    suspend fun saveUser() {

    }

    suspend fun getCurrentUser() {

    }
}