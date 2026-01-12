package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.Repository
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.FavoriteEvent
import com.eduplay.moblie.repository.requestTypes.TaskAnswer
import com.eduplay.moblie.repository.requestTypes.TaskStartTime
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.EventStage
import com.eduplay.moblie.repository.responseTypes.TaskFromBlock
import com.eduplay.moblie.services.TokenManager
import jakarta.inject.Inject
import java.time.LocalDateTime


class WebRepository @Inject constructor(
    private val api: WebApi,
    private val tokenManager: TokenManager,

    ) : Repository {
    override suspend fun login(auth: Auth): AuthResult {
        val response = api.login(auth)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            tokenManager.saveAccessToken(body.accessToken)
            tokenManager.saveRefreshToken(body.refreshToken)
            return AuthResult.SUCCESSES
        } // TODO(оделать проверку на причины отказа)
        return AuthResult.INVALID_USER
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun register(auth: Auth): AuthResult {
        val response = api.register(auth)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            tokenManager.saveAccessToken(body.accessToken)
            tokenManager.saveRefreshToken(body.refreshToken)
            return AuthResult.SUCCESSES
        } // TODO(оделать проверку на причины отказа)
        return AuthResult.INVALID_USER
    }

    override suspend fun getEvents(page:Int): List<QuestShortInfo> {
        val response = api.allEvents(page = page)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return ResponseConverter.convertListEventResponseToListQuestShortInfo(body)
        } // TODO(оделать проверку на причины отказа)
        return listOf()
    }

    override suspend fun getRole(eventId: String) : EventRole {
        val response = api.getUserEventRole(eventId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        }
        throw IllegalAccessException("No role for this event")
    }

    override suspend fun getPlayerEventInfo(eventId: String) : EventPlayerInfo {
        val response = api.getEventInfoPlayer(eventId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        }
        throw IllegalAccessException("No info for this event")
    }

    override suspend fun getOwnerEventInfo(eventId: String) : EventOwnerInfo {
        val response = api.getEventInfoCreator(eventId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        }
        throw IllegalAccessException("No info for this event")
    }

    override suspend fun getFavouriteEvents(page:Int, maxOnPage: Int): List<QuestShortInfo> {
        val response = api.favouriteEvents(page, maxOnPage)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return ResponseConverter.convertListEventResponseToListQuestShortInfo(body)
        } // TODO(оделать проверку на причины отказа)
        return listOf()
    }

    override suspend fun getCreatedEvents(page:Int, maxOnPage: Int): List<QuestShortInfo> {
        val response = api.createdEvents(page, maxOnPage)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return ResponseConverter.convertListEventResponseToListQuestShortInfo(body)
        } // TODO(оделать проверку на причины отказа)
        return listOf()
    }

    override suspend fun getCompletedEvents(page:Int, maxOnPage: Int): List<QuestShortInfo> {
        val response = api.completedEvents(page, maxOnPage)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return ResponseConverter.convertListEventResponseToListQuestShortInfo(body)
        } // TODO(оделать проверку на причины отказа)
        return listOf()
    }

    override suspend fun getProfile(): ProfileInfo {
        val response = api.getProfile()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        } // TODO(оделать проверку на причины отказа)
        return ProfileInfo("", "", "")
    }
    override suspend fun getNextStage(eventId: String): EventStage {
        val response = api.getNextStage(eventId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        } // TODO(оделать проверку на причины отказа)
        throw IllegalAccessException("cant enter next stage $eventId")
    }

    override suspend fun postTaskStartTime(eventId: String, blockId: String, taskId: String, startTime: LocalDateTime): Boolean {
        val response = api.postTaskStartTime(eventId, blockId, taskId, TaskStartTime(startTime.toString()))
        if (response.isSuccessful) {
            return true
        } // TODO(оделать проверку на причины отказа)
        if (response.code() != 403) return false
        throw IllegalAccessException("cant enter next stage $eventId")
    }

    override suspend fun postTaskAnswer(eventId: String, blockId: String, taskId: String, answers: List<String>): AnswerResult {
        val response = api.postTaskAnswer(eventId, blockId, taskId, TaskAnswer(answers))
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        } // TODO(оделать проверку на причины отказа)
        throw IllegalAccessException("cant enter next stage $eventId")
    }

    suspend fun postTaskChoice(eventId: String, blockId: String, taskId: String): Boolean {
        val response = api.postTaskChoice(eventId, TaskFromBlock(blockId, taskId))
        if (response.isSuccessful ) {
            return true
        } // TODO(оделать проверку на причины отказа)
        throw IllegalAccessException("cant enter next stage $eventId")
    }

    suspend fun addToFavourite(eventId: String, isFavorite: Boolean): Boolean {
        val response = api.addToFavourite(FavoriteEvent(eventId, isFavorite))
        if (response.isSuccessful) {
            return true
        } // TODO(оделать проверку на причины отказа)
        throw IllegalAccessException("cant add to favourites $eventId")
    }

}