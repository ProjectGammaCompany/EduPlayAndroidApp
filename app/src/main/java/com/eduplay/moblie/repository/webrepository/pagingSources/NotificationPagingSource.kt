package com.eduplay.moblie.repository.webrepository.pagingSources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.repository.webrepository.WebRepository

class NotificationPagingSource(private val repository: WebRepository):
    PagingSource<Int, NotificationData>() {
        private val numOfOffScreenPage: Int = 4

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationData> {
            val pageIndex = params.key ?: 1
            params.loadSize
            return try {
                val responseData = repository.getNotifications(pageIndex, 10)

                LoadResult.Page(
                    data = responseData,
                    prevKey = if (pageIndex == 1) null else pageIndex - 1,
                    nextKey = if (responseData.isEmpty()) null else pageIndex + 1
                )
            } catch (e: Exception) {
                Log.e("LOAD_NOTIFICATIONS", (e.message ?: "") + ((e.cause?.message) ?: " "), e)
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, NotificationData>): Int? {
            return state.anchorPosition?.let { anchor ->
                state.closestPageToPosition(anchor)?.prevKey?.plus(numOfOffScreenPage)
                    ?: state.closestPageToPosition(anchor)?.nextKey?.minus(numOfOffScreenPage)
            }
        }
}