package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.Refresh
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import com.eduplay.moblie.repository.responseTypes.AuthResponse
import com.eduplay.moblie.repository.webrepository.requestTypes.EmailForPasswordChange
import com.eduplay.moblie.repository.webrepository.responseTypes.PasswordResetCodeValidity
import com.eduplay.moblie.repository.webrepository.responseTypes.PasswordUpdate
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface AuthApi {
    @POST("/auth/login")
    suspend fun login(@Body auth: Auth): Response<AuthResponse>

    @PUT("/auth/logout")
    @InjectAuth
    suspend fun logout(): Response<Unit>

    @POST("/auth/refresh")
    @InjectAuth
    suspend fun refresh(@Body refresh: Refresh): Response<AuthResponse>

    @POST("/auth/register")
    suspend fun register(@Body auth: RegistrationData): Response<AuthResponse>

    @POST("/auth/recoverPasswordCode")
    suspend fun requestPasswordCodeByEmail(@Body email: EmailForPasswordChange): Response<Unit>

    @GET("/auth/recoverPasswordCodeValidity")
    suspend fun isPasswordCodeValid(@Query("code") code: String): Response<PasswordResetCodeValidity>

    @PUT("auth/password")
    suspend fun updatePassword(@Body password: PasswordUpdate): Response<AuthResponse>
}