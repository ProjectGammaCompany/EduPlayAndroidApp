package com.eduplay.moblie.repository

import com.eduplay.moblie.repository.responseTypes.AuthResponse

interface Repository {
    suspend fun login(): AuthResponse
    suspend fun logout()
    suspend fun register()
}