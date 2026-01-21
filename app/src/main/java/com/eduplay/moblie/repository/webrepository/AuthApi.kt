package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.Refresh
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import com.eduplay.moblie.repository.responseTypes.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body auth: Auth): Response<AuthResponse>

    @PUT("/auth/logout")
    @InjectAuth
    suspend fun logout(): Response<Unit>

    @POST("/auth/refresh")
    @InjectAuth
    fun refresh(@Body refresh: Refresh): Response<AuthResponse>

    @POST("/auth/register")
    suspend fun register(@Body auth: RegistrationData): Response<AuthResponse>
}