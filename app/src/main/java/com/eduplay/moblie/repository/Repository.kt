package com.eduplay.moblie.repository

import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.responseTypes.AuthResponse

interface Repository {
    suspend fun login(auth: Auth): AuthResult
    suspend fun register(auth: Auth): AuthResult
    suspend fun logout()
    suspend fun getEvents(page:Int = 1): List<QuestShortInfo>
}