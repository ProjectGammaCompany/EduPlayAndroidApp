package com.eduplay.moblie.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.EventOwnerInfo
import com.eduplay.moblie.models.EventPlayerInfo
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventTagList
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.pagingSources.AllEventsPagingWebSource
import com.eduplay.moblie.repository.pagingSources.CompletedEventsPagingSource
import com.eduplay.moblie.repository.pagingSources.CreatedEventsPagingSource
import com.eduplay.moblie.repository.pagingSources.FavoriteEventsPagingSource
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import com.eduplay.moblie.repository.responseTypes.AnswerResult
import com.eduplay.moblie.repository.responseTypes.EventStage
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.repository.webrepository.WebRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class EduRepository @Inject constructor(
    private val webRepository: WebRepository
) {

    suspend fun login(auth: Auth): AuthResult {
        return webRepository.login(auth)
    }

    suspend fun logout(): Boolean {
        return webRepository.logout()
    }

    suspend fun register(auth: RegistrationData): AuthResult {
        return webRepository.register(auth)
    }

    fun getEvents(
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
                    title
                )
            }
        ).flow
    }

    suspend fun getRole(eventId: String): EventRole {
        return webRepository.getRole(eventId)
    }

    suspend fun getEventInfoPlayer(eventId: String): EventPlayerInfo {
        return webRepository.getPlayerEventInfo(eventId)
    }

    suspend fun getEventInfoOwner(eventId: String): EventOwnerInfo {
        return webRepository.getOwnerEventInfo(eventId)
    }

    fun getFavouriteEvents(
        pageSize: Int = 20,
        enablePlaceHolders: Boolean = false,
        prefetchDistance: Int = 10,
        initialLoadSize: Int = 20,
        maxCacheSize: Int = 2000
    ): Flow<PagingData<QuestShortInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = enablePlaceHolders,
                prefetchDistance = prefetchDistance,
                initialLoadSize = initialLoadSize,
                maxSize = maxCacheSize
            ), pagingSourceFactory = {
                FavoriteEventsPagingSource(webRepository)
            }
        ).flow
    }

    fun getCreatedEvents(
        pageSize: Int = 20,
        enablePlaceHolders: Boolean = false,
        prefetchDistance: Int = 10,
        initialLoadSize: Int = 20,
        maxCacheSize: Int = 2000
    ): Flow<PagingData<QuestShortInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = enablePlaceHolders,
                prefetchDistance = prefetchDistance,
                initialLoadSize = initialLoadSize,
                maxSize = maxCacheSize
            ), pagingSourceFactory = {
                CreatedEventsPagingSource(webRepository)
            }
        ).flow
    }

    fun getCompletedEvents(
        pageSize: Int = 20,
        enablePlaceHolders: Boolean = false,
        prefetchDistance: Int = 10,
        initialLoadSize: Int = 20,
        maxCacheSize: Int = 2000
    ): Flow<PagingData<QuestShortInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = enablePlaceHolders,
                prefetchDistance = prefetchDistance,
                initialLoadSize = initialLoadSize,
                maxSize = maxCacheSize
            ), pagingSourceFactory = {
                CompletedEventsPagingSource(webRepository)
            }
        ).flow
    }

    suspend fun getProfile(): ProfileInfo {
        return webRepository.getProfile()
    }

    suspend fun getNextStage(eventId: String): EventStage {
        return webRepository.getNextStage(eventId)
    }

    suspend fun postTaskStartTime(
        eventId: String,
        blockId: String,
        taskId: String,
        taskStartTime: LocalDateTime
    ): Boolean {
        return webRepository.postTaskStartTime(eventId, blockId, taskId, taskStartTime)
    }

    suspend fun postAnswer(
        eventId: String,
        blockId: String,
        taskId: String,
        answers: List<String>
    ): AnswerResult {
        return webRepository.postTaskAnswer(
            eventId,
            blockId,
            taskId,
            answers.map { it.lowercase() }.toList()
        )
    }

    suspend fun postTaskChoice(eventId: String, blockId: String, taskId: String): Boolean {
        return webRepository.postTaskChoice(eventId, blockId, taskId)
    }

    suspend fun addToFavourites(eventId: String, isFavorite: Boolean): Boolean {
        return webRepository.addToFavourite(eventId, isFavorite)
    }

    suspend fun complain(eventId: String, reason: String) {
        webRepository.complain(eventId, reason)
    }

    suspend fun getEventResults(eventId: String): PlayerStats {
        return webRepository.getResults(eventId)
    }

    suspend fun getTags(): EventTagList {
        return webRepository.getTags()
    }
}