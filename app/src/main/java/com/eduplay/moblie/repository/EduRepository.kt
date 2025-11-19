package com.eduplay.moblie.repository

import com.eduplay.moblie.repository.responseTypes.AuthResponse
import com.eduplay.moblie.repository.webrepository.WebRepository
import jakarta.inject.Inject

class EduRepository @Inject constructor(
    private val webRepository: WebRepository
) : Repository {

    override suspend fun login(): AuthResponse {
        return webRepository.login()
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }

    override suspend fun register() {
        TODO("Not yet implemented")
    }
}