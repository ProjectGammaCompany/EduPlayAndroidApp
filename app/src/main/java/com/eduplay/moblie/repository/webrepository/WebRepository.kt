package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.Repository
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.services.TokenManager
import jakarta.inject.Inject


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


}