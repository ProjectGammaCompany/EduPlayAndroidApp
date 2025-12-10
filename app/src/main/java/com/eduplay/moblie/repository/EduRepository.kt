package com.eduplay.moblie.repository

import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.webrepository.WebRepository
import jakarta.inject.Inject

class EduRepository @Inject constructor(
    private val webRepository: WebRepository
) {

    suspend fun login(auth: Auth): AuthResult {
        return webRepository.login(auth)
    }

    suspend fun logout() {
        TODO("Not yet implemented")
    }

    suspend fun register(auth: Auth): AuthResult {
        return webRepository.register(auth)
    }

    suspend fun getEvents(page: Int =1): List<QuestShortInfo> {
        return webRepository.getEvents(page)
    }
}