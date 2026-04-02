package com.eduplay.moblie.repository.localrepository.pagingSources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.localrepository.LocalRepository
import com.google.gson.Gson

class LocalAllEventsPagingSource(
    private val repository: LocalRepository,
    private val tags: List<String>? = null,
    private val active: Boolean = false,
    private val title: String = ""
) :
    PagingSource<Int, QuestShortInfo>() {
    private val numOfOffScreenPage: Int = 4

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuestShortInfo> {
        val page = params.key?: 0
        val offset = page * params.loadSize
        return try {
            val data = repository
                .getEvents(
                    offset = offset,
                    limit = params.loadSize,
                    tags = tags,
                    active = active,
                    title = title,
                ).map { QuestShortInfo(it) }

            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page-1,
                nextKey = if (data.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("DATA_BASE_MAIN_EVENT_LIST", (e.message ?: "") + ((e.cause?.message) ?: " "), e)
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