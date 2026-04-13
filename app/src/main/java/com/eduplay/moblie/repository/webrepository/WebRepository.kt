package com.eduplay.moblie.repository.webrepository

import android.util.Log
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventTagList
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.Repository
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.EventComplaint
import com.eduplay.moblie.repository.requestTypes.EventPasswords
import com.eduplay.moblie.repository.requestTypes.FavoriteEvent
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import com.eduplay.moblie.repository.requestTypes.TaskAnswer
import com.eduplay.moblie.repository.requestTypes.TaskStartTime
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.EventIdResponse
import com.eduplay.moblie.repository.responseTypes.JoinCodeInfo
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.repository.responseTypes.RequiredJoinFields
import com.eduplay.moblie.repository.responseTypes.TaskFromBlock
import com.eduplay.moblie.repository.webrepository.requestTypes.AnswerBatch
import com.eduplay.moblie.repository.webrepository.requestTypes.AvatarUpdate
import com.eduplay.moblie.repository.webrepository.requestTypes.EventIdList
import com.eduplay.moblie.repository.webrepository.requestTypes.EventRating
import com.eduplay.moblie.repository.webrepository.requestTypes.GroupCredentials
import com.eduplay.moblie.repository.webrepository.requestTypes.ProfileUpdate
import com.eduplay.moblie.repository.webrepository.responseTypes.EventStage
import com.eduplay.moblie.repository.webrepository.responseTypes.Notification
import com.eduplay.moblie.repository.webrepository.responseTypes.UserEventStatus
import com.eduplay.moblie.repository.webrepository.responseTypes.UserEventStatusList
import com.eduplay.moblie.useCases.DateConverter
import com.eduplay.moblie.useCases.TokenManager
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDateTime


class WebRepository @Inject constructor(
    private val api: WebApi,
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,

    ) : Repository {
    suspend fun login(auth: Auth): AuthResult {
        val response = authApi.login(auth)
        val body = response.body()
        Log.d("Requests AUTHORISATION", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            Log.d("Requests AUTHORISATION", body.accessToken)
            tokenManager.saveAccessToken(body.accessToken)
            tokenManager.saveRefreshToken(body.refreshToken)
            return AuthResult.SUCCESSES
        }
        if (response.code() == 404) return AuthResult.INVALID_USER
        return AuthResult.INVALID_PASSWORD
    }

    suspend fun logout(): Boolean {
        val response = authApi.logout()
        if (response.isSuccessful) {
            tokenManager.saveAccessToken("")
            tokenManager.saveRefreshToken("")
            return true
        }
        return false
    }

    suspend fun register(auth: RegistrationData): AuthResult {
        val response = authApi.register(auth)
        val body = response.body()
        Log.d("Requests AUTHORISATION", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            Log.d("Requests AUTHORISATION", body.accessToken)
            tokenManager.saveAccessToken(body.accessToken)
            tokenManager.saveRefreshToken(body.refreshToken)
            return AuthResult.SUCCESSES
        }

        return AuthResult.INVALID_USER
    }

    suspend fun getEvents(
        page: Int,
        tags: List<String>?,
        decliningRating: Boolean,
        active: Boolean,
        favorites: Boolean,
        title: String,
        isDownloaded: suspend (String) -> Boolean
    ): List<QuestShortInfo> {
        val response = api.allEvents(
            page = page,
            tags = tags,
            decliningRating = decliningRating,
            active = active,
            favorites = favorites,
            title = title
        )
        val body = response.body()
        Log.d("Requests events", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body.events.map { QuestShortInfo(it, isDownloaded(it.id)) }
        } // TODO(оделать проверку на причины отказа)
        return listOf()
    }

    override suspend fun getRole(eventId: String): EventRole {

        val response = api.getUserEventRole(eventId)
        val body = response.body()
        Log.d("Requests role", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return if (body.role == 1) EventRole.AUTHOR else EventRole.PARTICIPANT
        }
        throw IllegalAccessException("No role for this event")
    }

    override suspend fun getPlayerEventInfo(eventId: String): EventPlayerInfo {
        val response = api.getEventInfoPlayer(eventId)
        val body = response.body()
        Log.d("Requests player info", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body
        }
        throw IllegalAccessException("No info for this event")
    }

    override suspend fun getOwnerEventInfo(eventId: String): EventOwnerInfo {
        val response = api.getEventInfoCreator(eventId)
        val body = response.body()
        Log.d("Requests owner event", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body
        }
        throw IllegalAccessException("No info for this event")
    }

    suspend fun getFavouriteEvents(
        page: Int,
        maxOnPage: Int,
        isDownloaded: suspend (String) -> Boolean
    ): List<QuestShortInfo> {
        val response = api.favouriteEvents(page, maxOnPage)
        val body = response.body()
        Log.d("Requests get favourite", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body.events.map { QuestShortInfo(it, isDownloaded(it.id)) }
        }
        return listOf()
    }

    suspend fun getCreatedEvents(
        page: Int,
        maxOnPage: Int,
        isDownloaded: suspend (String) -> Boolean
    ): List<QuestShortInfo> {
        val response = api.createdEvents(page, maxOnPage)
        val body = response.body()
        Log.d("Requests created events", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body.events.map { QuestShortInfo(it, isDownloaded(it.id)) }
        }
        return listOf()
    }

    suspend fun getCompletedEvents(
        page: Int,
        maxOnPage: Int,
        isDownloaded: suspend (String) -> Boolean
    ): List<QuestShortInfo> {
        val response = api.completedEvents(page, maxOnPage)
        val body = response.body()
        Log.d("Requests completed events", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body.events.map { QuestShortInfo(it, isDownloaded(it.id)) }
        }
        return listOf()
    }

    override suspend fun getProfile(): ProfileInfo {
        val response = api.getProfile()
        val body = response.body()
        Log.d("Requests profile", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body
        }
        return ProfileInfo("", "")
    }

    override suspend fun getNextStage(eventId: String): EventStage {
        val response = api.getNextStage(eventId)
        val body = response.body()
        Log.e("Requests next stage", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body
        }
        throw IllegalAccessException("cant enter next stage $eventId")
    }

    override suspend fun postTaskStartTime(
        eventId: String,
        blockId: String,
        taskId: String,
        startTime: LocalDateTime
    ): Boolean {
        val response =
            api.postTaskStartTime(eventId, blockId, taskId, TaskStartTime(startTime.toString()))
        if (response.isSuccessful) {
            return true
        }
        if (response.code() != 403) return false
        throw IllegalAccessException("cant enter next stage $eventId")
    }

    override suspend fun postTaskAnswer(
        eventId: String,
        blockId: String,
        taskId: String,
        answers: List<String>
    ): AnswerResult {
        val response = api.postTaskAnswer(eventId, blockId, taskId, TaskAnswer(answers))
        val body = response.body()
        Log.d("Requests_answer", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body
        } // TODO(оделать проверку на причины отказа)
        throw IllegalAccessException("cant send answer $eventId")
    }

    override suspend fun postTaskChoice(eventId: String, blockId: String, taskId: String): Boolean {
        val response =
            api.postTaskChoice(eventId, TaskFromBlock(blockId = blockId, taskId = taskId))
        if (response.isSuccessful) {
            return true
        }
        throw IllegalAccessException("cant enter next stage $eventId")
    }

    override suspend fun getResults(eventId: String): PlayerStats {
        val response = api.getPlayerStats(eventId)
        val body = response.body()
        Log.d("Requests results", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body
        }
        throw IllegalAccessException("cant enter next stage $eventId")
    }

    override suspend fun addToFavourite(eventId: String, isFavorite: Boolean): Boolean {
        val response = api.addToFavourite(FavoriteEvent(eventId, isFavorite))
        Log.d("Requests add favorite events", response.code().toString() + response.raw())
        if (response.isSuccessful) {
            return true
        }
        throw IllegalAccessException("cant add to favourites $eventId")
    }

    suspend fun complain(eventId: String, reason: String) {
        val response = api.sendEventComplaint(eventId, EventComplaint(reason))
        if (response.isSuccessful) {
            return
        }
        throw IllegalAccessException("cant complain $eventId")
    }

    override suspend fun getTags(): EventTagList {
        val response = api.getTags()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        } // TODO(оделать проверку на причины отказа)
        throw IllegalAccessException("cant get tags")
    }

    suspend fun getRequiredJoinFields(joinCode: String): RequiredJoinFields {
        val response = api.getFieldsToJoinEvent(joinCode)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        }
        if (response.code() == 404) {
            throw NoSuchElementException("no event with code $joinCode")
        }
        throw IllegalAccessException("cant get tags")
    }

    suspend fun enterPrivateEvent(
        joinCode: String,
        eventPasswords: EventPasswords
    ): EventIdResponse {
        val response = api.postPasswords(joinCode, eventPasswords)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        }
        if (response.code() == 403) {
            throw IllegalAccessException("wrong password")
        }
        throw IllegalAccessException("cant access event")

    }

    suspend fun getJoinCode(eventId: String): JoinCodeInfo {
        val response = api.getJoinCode(eventId)
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body
        }
        throw IllegalAccessException("cant get join code for $eventId")
    }

    override suspend fun enterGroupEvent(
        eventId: String,
        groupName: String,
        groupPassword: String
    ) {
        val response = api.postGroupPasswordsToEnterPublicGroupEvent(
            eventId,
            GroupCredentials(groupName, groupPassword)
        )
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return
        }
        if (response.code() == 403) {
            throw IllegalAccessException("wrong password")
        }
        throw IllegalAccessException("cant access event")
    }


    override suspend fun getNotifications(page: Int, maxOnPage: Int): List<NotificationData> {
        val response = api.getNotifications(page, maxOnPage)
        val body = response.body()
        Log.d("Requests notifications", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body.notifications
                .filter { Notification.NotificationType.valueByType(it.type) != null }
                .map { notification ->
                    when (Notification.NotificationType.valueByType(notification.type)) {
                        Notification.NotificationType.FAVORITE_START -> {
                            val extra = notification.favoriteEventStartExtra
                                ?: return@map NotificationData.EmptyNotification()
                            NotificationData.FavoriteNotificationData(
                                notification.id,
                                extra.id,
                                extra.eventName,
                                DateConverter.convertFromServerFormat(notification.date)
                            )
                        }

                        Notification.NotificationType.EVENT_END -> {
                            val extra = notification.eventEndExtra
                                ?: return@map NotificationData.EmptyNotification()
                            val timeLeft =
                                NotificationData.EndEventNotificationData.TimeLeft.valueByTime(extra.timeLeft)
                            NotificationData.EndEventNotificationData(
                                notification.id,
                                extra.id,
                                extra.eventName,
                                DateConverter.convertFromServerFormat(notification.date),
                                timeLeft = timeLeft!!,
                                notStartedFavorite = extra.notStartedFavorite,
                            )
                        }

                        null -> NotificationData.EmptyNotification()
                    }
                }
        }
        return listOf()
    }

    suspend fun deleteNotification(id: String): Boolean {
        val response = api.deleteNotification(id)
        Log.d("delete notifications", response.code().toString() + response.raw())
        return response.isSuccessful
    }

    suspend fun updateUsername(userName: String) {
        val response = api.putUserName(ProfileUpdate(userName))
        if (response.isSuccessful) {
            return
        }
        throw IllegalAccessException("cant update username")
    }

    suspend fun updateAvatar(imageFile: File): String {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .build()
        val fileUploadResponse = api.postFile(requestBody)
        val serverImageUrlBody = fileUploadResponse.body()
        if (!fileUploadResponse.isSuccessful || serverImageUrlBody == null) {
            throw IllegalAccessException("cant upload file to server")
        }
        imageFile.delete()
        val response = api.putAvatar(AvatarUpdate(serverImageUrlBody))
        if (!response.isSuccessful) {
            throw IllegalAccessException("cant upload file to server")
        }
        return serverImageUrlBody
    }

    suspend fun postRating(rating: Int) {
        val response = api.postRating(EventRating(rating))
        if (response.isSuccessful) {
            return
        }
        throw IllegalAccessException("cant post rating")
    }

    suspend fun getEventFileUrl(eventId: String): String {
        val response = api.getEventFileUrl(eventId)
        val body = response.body()
        Log.d("pathForDownload", response.code().toString() + response.raw())
        if (response.isSuccessful && body != null) {
            return body.downloadPath
        }
        throw IllegalAccessException("failed to get file path")
    }

    suspend fun postAnswerBatch(answerBatch: AnswerBatch): Boolean {
        val response = api.postAnswerBatch(answerBatch)
        return response.isSuccessful
    }

    suspend fun getDownloadedEventsStatus(events: List<String>): List<UserEventStatus>? {
        val response = api.getUserEventsStatuses(EventIdList(events))
        val body = response.body()
        if (response.isSuccessful && body != null) {
            return body.events
        }
        return null
    }

}