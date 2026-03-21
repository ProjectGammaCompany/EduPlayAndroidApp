package com.eduplay.moblie.repository.pagingSources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.Repository

class AllEventsPagingWebSource(
    private val webRepository: Repository,
    private val tags: List<String>? = null,
    private val decliningRating: Boolean = false,
    private val active: Boolean = false,
    private val favorites: Boolean = false,
    private val title: String = ""
) :
    PagingSource<Int, QuestShortInfo>() {
    private val numOfOffScreenPage: Int = 4

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuestShortInfo> {
        val pageIndex = params.key ?: 1
        params.loadSize
        return try {
            val responseData = webRepository
                .getEvents(
                    page = pageIndex,
                    tags = tags,
                    decliningRating = decliningRating,
                    active = active,
                    favorites = favorites,
                    title = title,
                )

            LoadResult.Page(
                data = responseData,
                prevKey = if (pageIndex == 1) null else pageIndex - 1,
                nextKey = if (responseData.isEmpty()) null else pageIndex + 1
            )
        } catch (e: Exception) {
            Log.e("LOAD_MAIN_EVENT_LIST", (e.message ?: "") + ((e.cause?.message) ?: " "))
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