package com.eduplay.moblie.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventTagList
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.localrepository.LocalRepository
import com.eduplay.moblie.repository.localrepository.pagingSources.LocalAllEventsPagingSource
import com.eduplay.moblie.repository.localrepository.pagingSources.LocalCompletedEventsPagingSource
import com.eduplay.moblie.repository.localrepository.pagingSources.LocalCreatedEventsPagingSource
import com.eduplay.moblie.repository.localrepository.pagingSources.LocalFavoriteEventsPagingSource
import com.eduplay.moblie.repository.webrepository.pagingSources.AllEventsPagingWebSource
import com.eduplay.moblie.repository.webrepository.pagingSources.CompletedEventsPagingSource
import com.eduplay.moblie.repository.webrepository.pagingSources.CreatedEventsPagingSource
import com.eduplay.moblie.repository.webrepository.pagingSources.FavoriteEventsPagingSource
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.EventPasswords
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.EventIdResponse
import com.eduplay.moblie.repository.webrepository.responseTypes.EventStage
import com.eduplay.moblie.repository.responseTypes.JoinCodeInfo
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.repository.responseTypes.RequiredJoinFields
import com.eduplay.moblie.repository.webrepository.WebRepository
import com.eduplay.moblie.repository.webrepository.pagingSources.NotificationPagingSource
import com.eduplay.moblie.useCases.OfflineModeManager
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime

class EduRepository @Inject constructor(
    private val webRepository: WebRepository,
    private val localRepository: LocalRepository,
    private val offlineModeManager: OfflineModeManager
) {
    suspend fun login(auth: Auth): AuthResult {
        var authResult: AuthResult
        try {
            authResult = webRepository.login(auth)
        } catch (e: Exception) {
            throw e
        }
        localRepository.saveUser()
        return authResult
    }

    suspend fun logout(): Boolean {
        var result: Boolean
        try {
            result = webRepository.logout()
        } catch (e: Exception) {
            throw e
        }
        localRepository.removeCurrentUser()
        return result
    }

    suspend fun register(auth: RegistrationData): AuthResult {
        var authResult: AuthResult
        try {
            authResult = webRepository.register(auth)
        } catch (e: Exception) {
            throw e
        }
        localRepository.saveUser()
        return authResult
    }

    suspend fun getEvents(
        pageSize: Int = 20,
        enablePlaceHolders: Boolean = false,
        prefetchDistance: Int = 10,
        initialLoadSize: Int = 20,
        maxCacheSize: Int = 2000,
        tags: List<String>? = null,
        decliningRating: Boolean = false,
        active: Boolean = false,
        favorites: Boolean = false,
        title: String = ""
    ): Flow<PagingData<QuestShortInfo>> {
        if (offlineModeManager.getAppMode().first() == OfflineModeManager.AppModes.ONLINE) {
            return Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = enablePlaceHolders,
                    prefetchDistance = prefetchDistance,
                    initialLoadSize = initialLoadSize,
                    maxSize = maxCacheSize
                ), pagingSourceFactory = {
                    AllEventsPagingWebSource(
                        webRepository,
                        tags,
                        decliningRating,
                        active,
                        favorites,
                        title,
                        localRepository::isEventDownloaded
                    )
                }
            ).flow
        }
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = enablePlaceHolders,
                prefetchDistance = prefetchDistance,
                initialLoadSize = initialLoadSize,
                maxSize = maxCacheSize
            ),
            pagingSourceFactory = {
                LocalAllEventsPagingSource(
                    repository = localRepository,
                    tags = tags,
                    active = active,
                    title = title
                )
            }
        ).flow
    }

    suspend fun getRole(eventId: String): EventRole {
        return getRepository().getRole(eventId)
    }

    suspend fun getEventInfoPlayer(eventId: String): EventPlayerInfo {
        return getRepository().getPlayerEventInfo(eventId)
    }

    suspend fun getEventInfoOwner(eventId: String): EventOwnerInfo {
        return getRepository().getOwnerEventInfo(eventId)
    }

    suspend fun getFavouriteEvents(
        pageSize: Int = 20,
        enablePlaceHolders: Boolean = false,
        prefetchDistance: Int = 10,
        initialLoadSize: Int = 20,
        maxCacheSize: Int = 2000
    ): Flow<PagingData<QuestShortInfo>> {
        if (offlineModeManager.getAppMode().first() == OfflineModeManager.AppModes.ONLINE) {
            return Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = enablePlaceHolders,
                    prefetchDistance = prefetchDistance,
                    initialLoadSize = initialLoadSize,
                    maxSize = maxCacheSize
                ), pagingSourceFactory = {
                    FavoriteEventsPagingSource(webRepository, localRepository::isEventDownloaded)
                }
            ).flow
        } else {
            return Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = enablePlaceHolders,
                    prefetchDistance = prefetchDistance,
                    initialLoadSize = initialLoadSize,
                    maxSize = maxCacheSize
                ), pagingSourceFactory = {
                    LocalFavoriteEventsPagingSource(localRepository)
                }
            ).flow
        }
    }

    suspend fun getCreatedEvents(
        pageSize: Int = 20,
        enablePlaceHolders: Boolean = false,
        prefetchDistance: Int = 10,
        initialLoadSize: Int = 20,
        maxCacheSize: Int = 2000
    ): Flow<PagingData<QuestShortInfo>> {
        if (offlineModeManager.getAppMode().first() == OfflineModeManager.AppModes.ONLINE) {
            return Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = enablePlaceHolders,
                    prefetchDistance = prefetchDistance,
                    initialLoadSize = initialLoadSize,
                    maxSize = maxCacheSize
                ), pagingSourceFactory = {
                    CreatedEventsPagingSource(webRepository, localRepository::isEventDownloaded)
                }
            ).flow
        }
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = enablePlaceHolders,
                prefetchDistance = prefetchDistance,
                initialLoadSize = initialLoadSize,
                maxSize = maxCacheSize
            ), pagingSourceFactory = {
                LocalCreatedEventsPagingSource(localRepository)
            }
        ).flow
    }

    suspend fun getCompletedEvents(
        pageSize: Int = 20,
        enablePlaceHolders: Boolean = false,
        prefetchDistance: Int = 10,
        initialLoadSize: Int = 20,
        maxCacheSize: Int = 2000
    ): Flow<PagingData<QuestShortInfo>> {
        if (offlineModeManager.getAppMode().first() == OfflineModeManager.AppModes.ONLINE) {
            return Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = enablePlaceHolders,
                    prefetchDistance = prefetchDistance,
                    initialLoadSize = initialLoadSize,
                    maxSize = maxCacheSize
                ), pagingSourceFactory = {
                    CompletedEventsPagingSource(webRepository, localRepository::isEventDownloaded)
                }
            ).flow
        }
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = enablePlaceHolders,
                prefetchDistance = prefetchDistance,
                initialLoadSize = initialLoadSize,
                maxSize = maxCacheSize
            ), pagingSourceFactory = {
                LocalCompletedEventsPagingSource(localRepository)
            }
        ).flow
    }

    suspend fun getProfile(): ProfileInfo {
        return getRepository().getProfile()
    }

    suspend fun getNextStage(eventId: String): EventStage {
        return getRepository().getNextStage(eventId)
    }

    suspend fun postTaskStartTime(
        eventId: String,
        blockId: String,
        taskId: String,
        taskStartTime: LocalDateTime
    ): Boolean {
        return getRepository().postTaskStartTime(eventId, blockId, taskId, taskStartTime)
    }

    suspend fun postAnswer(
        eventId: String,
        blockId: String,
        taskId: String,
        answers: List<String>
    ): AnswerResult {
        return getRepository().postTaskAnswer(
            eventId,
            blockId,
            taskId,
            answers.map { it.lowercase() }.toList()
        )
    }

    suspend fun postTaskChoice(eventId: String, blockId: String, taskId: String): Boolean {
        return getRepository().postTaskChoice(eventId = eventId, blockId = blockId, taskId = taskId)
    }

    suspend fun addToFavourites(eventId: String, isFavorite: Boolean): Boolean {
        return getRepository().addToFavourite(eventId, isFavorite)
    }

    suspend fun complain(eventId: String, reason: String) {
        webRepository.complain(eventId, reason)
    }

    suspend fun getEventResults(eventId: String): PlayerStats {
        return getRepository().getResults(eventId)
    }

    suspend fun getTags(): EventTagList {
        return getRepository().getTags()
    }

    suspend fun getRequiredJoinFields(joinCode: String): RequiredJoinFields {
        return webRepository.getRequiredJoinFields(joinCode)
    }

    suspend fun enterPrivateEvent(
        joinCode: String,
        eventPasswords: EventPasswords
    ): EventIdResponse {
        return webRepository.enterPrivateEvent(joinCode, eventPasswords)
    }

    suspend fun getJoinCode(eventId: String): JoinCodeInfo {
        return webRepository.getJoinCode(eventId)
    }

    suspend fun enterGroupEvent(eventId: String, groupName: String, groupPassword: String) {
        getRepository().enterGroupEvent(eventId, groupName, groupPassword)
    }

    suspend fun getLatestNotifications(): List<NotificationData> {
        return getRepository().getNotifications(1, 5)
    }

    suspend fun getNotifications(
        pageSize: Int = 20,
        enablePlaceHolders: Boolean = false,
        prefetchDistance: Int = 10,
        initialLoadSize: Int = 20,
        maxCacheSize: Int = 2000
    ): Flow<PagingData<NotificationData>> {
        if (offlineModeManager.getAppMode().first() == OfflineModeManager.AppModes.ONLINE) {
            return Pager(
                config = PagingConfig(
                    pageSize = pageSize,
                    enablePlaceholders = enablePlaceHolders,
                    prefetchDistance = prefetchDistance,
                    initialLoadSize = initialLoadSize,
                    maxSize = maxCacheSize
                ), pagingSourceFactory = {
                    NotificationPagingSource(webRepository)
                }
            ).flow
        }
        return flowOf()
    }

    private suspend fun getRepository(): Repository {
        return when (offlineModeManager.getAppMode().first()) {
            OfflineModeManager.AppModes.ONLINE ->  webRepository
            OfflineModeManager.AppModes.OFFLINE -> localRepository
        }
    }
}