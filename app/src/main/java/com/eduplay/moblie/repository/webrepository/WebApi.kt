package com.eduplay.moblie.repository.webrepository

import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.EventComplaint
import com.eduplay.moblie.repository.requestTypes.FavoriteEvent
import com.eduplay.moblie.repository.requestTypes.Refresh
import com.eduplay.moblie.repository.requestTypes.TaskAnswer
import com.eduplay.moblie.repository.requestTypes.TaskStartTime
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.AuthResponse
import com.eduplay.moblie.repository.responseTypes.EventListResponse
import com.eduplay.moblie.repository.responseTypes.EventRoleResponse
import com.eduplay.moblie.repository.responseTypes.EventStage
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.repository.responseTypes.TaskFromBlock
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WebApi {
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

    @GET("/events/personal/favorites")
    @InjectAuth
    suspend fun favouriteEvents(
        @Query("page") page: Int = 1,
        @Query("maxOnPage") maxOnPage: Int = 10,
    ): Response<EventListResponse>

    @GET("/events/personal/created")
    @InjectAuth
    suspend fun createdEvents(
        @Query("page") page: Int = 1,
        @Query("maxOnPage") maxOnPage: Int = 10,
    ): Response<EventListResponse>

    @GET("/events/personal/history")
    @InjectAuth
    suspend fun completedEvents(
        @Query("page") page: Int = 1,
        @Query("maxOnPage") maxOnPage: Int = 10,
    ): Response<EventListResponse>

    @PUT("events/personal/favorites")
    @InjectAuth
    suspend fun addToFavourite(
        @Body event: FavoriteEvent
    ): Response<Unit>

    @GET("/event/{eventId}/role")
    @InjectAuth
    suspend fun getUserEventRole(
        @Path("eventId") eventId: String
    ): Response<EventRoleResponse>

    @GET("/event/{eventId}/playerInfo")
    @InjectAuth
    suspend fun getEventInfoPlayer(
        @Path("eventId") eventId: String
    ): Response<EventPlayerInfo>

    @GET("/event/{eventId}/ownerInfo")
    @InjectAuth
    suspend fun getEventInfoCreator(
        @Path("eventId") eventId: String
    ): Response<EventOwnerInfo>

    @GET("/profile")
    @InjectAuth
    suspend fun getProfile(): Response<ProfileInfo>

    @GET("/event/{eventId}/nextStage")
    @InjectAuth
    suspend fun getNextStage(
        @Path("eventId") eventId: String
    ): Response<EventStage>

    @PUT("/event/{eventId}/nextStage")
    @InjectAuth
    suspend fun postTaskChoice(
        @Path("eventId") eventId: String,
        @Body task: TaskFromBlock
    ): Response<EventStage>

    @POST("event/{eventId}/block/{blockId}/task/{taskId}/timestamp")
    @InjectAuth
    suspend fun postTaskStartTime(
        @Path("eventId") eventId: String,
        @Path("blockId") blockId: String,
        @Path("taskId") taskId: String,
        @Body timeStamp: TaskStartTime
    ): Response<Unit>

    @POST("event/{eventId}/block/{blockId}/task/{taskId}/answer")
    @InjectAuth
    suspend fun postTaskAnswer(
        @Path("eventId") eventId: String,
        @Path("blockId") blockId: String,
        @Path("taskId") taskId: String,
        @Body answer: TaskAnswer
    ): Response<AnswerResult>

    @POST("/event/{eventId}/complaint")
    @InjectAuth
    suspend fun sendEventComplaint(
        @Path("eventId") eventId: String,
        @Body complaint: EventComplaint
    ): Response<Unit>

    @GET("/event/{eventId}/playerStats")
    @InjectAuth
    suspend fun getPlayerStats(@Path("eventId") eventId: String) : Response<PlayerStats>

}