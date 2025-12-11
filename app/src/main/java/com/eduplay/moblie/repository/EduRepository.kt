package com.eduplay.moblie.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.webrepository.WebRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class EduRepository @Inject constructor(
    private val webRepository: WebRepository
) {

    suspend fun login(auth: Auth): AuthResult {
        return webRepository.login(auth)
    }

    suspend fun logout() {
        TODO("Not yet implemented")
    }

    suspend fun register(auth: Auth): AuthResult {
        return webRepository.register(auth)
    }

    fun getEvents(
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
                AllEventsPagingWebSource(webRepository)
            }
        ).flow
    }
}