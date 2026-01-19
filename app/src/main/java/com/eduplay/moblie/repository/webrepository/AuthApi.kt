package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.Refresh
import com.eduplay.moblie.repository.responseTypes.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("/login")
    suspend fun login(@Body auth: Auth): Response<AuthResponse>

    @PUT("/logout")
    @InjectAuth
    suspend fun logout(): Response<Unit>

    @POST("/refresh")
    @InjectAuth
    fun refresh(@Body refresh: Refresh): Response<AuthResponse>

    @POST("/register")
    suspend fun register(@Body auth: Auth): Response<AuthResponse>
}