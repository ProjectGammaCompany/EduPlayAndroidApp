package com.eduplay.moblie.repository

import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.responseTypes.AuthResponse

interface Repository {
    suspend fun login(auth: Auth): AuthResult
    suspend fun register(auth: Auth): AuthResult
    suspend fun logout()
    suspend fun getEvents(page:Int = 1): List<QuestShortInfo>
    suspend fun getRole(eventId: String) : EventRole
    suspend fun getPlayerEventInfo(eventId: String): EventPlayerInfo
    suspend fun getOwnerEventInfo(eventId: String): EventOwnerInfo
    suspend fun getFavouriteEvents(page: Int, maxOnPage: Int): List<QuestShortInfo>
    suspend fun getCreatedEvents(page: Int, maxOnPage: Int): List<QuestShortInfo>
    suspend fun getCompletedEvents(page: Int, maxOnPage: Int): List<QuestShortInfo>
}