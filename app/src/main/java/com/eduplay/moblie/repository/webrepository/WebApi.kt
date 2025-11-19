package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.repository.responseTypes.AuthResponse
import retrofit2.http.POST

interface WebApi {
    @POST("/login")
    suspend fun login(): AuthResponse
}