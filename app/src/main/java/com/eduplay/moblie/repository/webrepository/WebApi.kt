package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.responseTypes.AuthResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface WebApi {
    @POST("/login")
    suspend fun login(auth: Auth): Response<AuthResponse>

    @POST("/register")
    suspend fun register(auth: Auth): Response<AuthResponse>
}