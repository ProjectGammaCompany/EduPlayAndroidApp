package com.eduplay.moblie.repository.localrepository.pagingSources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.localrepository.LocalRepository

class LocalCreatedEventsPagingSource(private val repository: LocalRepository) :
    PagingSource<Int, QuestShortInfo>() {
    private val numOfOffScreenPage: Int = 4

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuestShortInfo> {
        val offset = ((params.key ?: 1) - 1) * params.loadSize
        return try {
            val data = repository
                .getCreatedEvents(
                    offset = offset,
                    limit = params.loadSize,
                )

            LoadResult.Page(
                data = data,
                prevKey = if (params.key == 1) null else (params.key ?: 1)  - 1,
                nextKey = if (data.isEmpty()) null else (params.key ?: 1) + 1
            )
        } catch (e: Exception) {
            Log.e("DATA_BASE_MAIN_EVENT_LIST", (e.message ?: "") + ((e.cause?.message) ?: " "))
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, QuestShortInfo>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(numOfOffScreenPage)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(numOfOffScreenPage)
        }
    }
}