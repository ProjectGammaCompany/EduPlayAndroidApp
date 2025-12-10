package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.responseTypes.AuthResponse
import com.eduplay.moblie.repository.responseTypes.EventListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface WebApi {
    @POST("/login")
    suspend fun login(auth: Auth): Response<AuthResponse>

    @POST("/register")
    suspend fun register(auth: Auth): Response<AuthResponse>

    @GET("/events")
    @InjectAuth
    suspend fun allEvents(
        @Query("page") page: Int = 1,
        @Query("maxOnPage") maxOnPage: Int = 10,
        @Query("tags") tags: List<String>? = null,
        @Query("decliningRating") decliningRating: Boolean = false,
        @Query("territorialized") territorialized: Boolean = false,
        @Query("active") active: Boolean = false
    ): Response<EventListResponse>
}