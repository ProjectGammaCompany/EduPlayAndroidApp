package com.eduplay.moblie.repository

import com.eduplay.moblie.repository.responseTypes.AuthResponse
import com.eduplay.moblie.repository.webrepository.WebRepository
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val webRepository: WebRepository
): Repository {
    override suspend fun login(): AuthResponse {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun register() {
        TODO("Not yet implemented")
    }
}