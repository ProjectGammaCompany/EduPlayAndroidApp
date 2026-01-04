package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.responseTypes.AuthResponse
import com.eduplay.moblie.repository.responseTypes.EventListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

    @GET("/event/{eventId}/role")
    @InjectAuth
    suspend fun getUserEventRole(
        @Path("eventId") eventId: String
    ) : Response<EventRole>

    @GET("event/{eventId}/playerInfo")
    @InjectAuth
    suspend fun getEventInfoPlayer(
        @Path("eventId") eventId: String
    ) : Response <EventPlayerInfo>

    @GET("event/{eventId}/ownerInfo")
    @InjectAuth
    suspend fun getEventInfoCreator(
        @Path("eventId") eventId: String
    ) : Response <EventOwnerInfo>
}