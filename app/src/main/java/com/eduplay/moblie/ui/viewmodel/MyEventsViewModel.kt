package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException
import kotlin.math.max

@HiltViewModel
class MyEventsViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    val favourite = mutableStateListOf<QuestShortInfo>()
    val completed = mutableStateListOf<QuestShortInfo>()
    val created = mutableStateListOf<QuestShortInfo>()
    val unauthorised = mutableStateOf(false)

    private val totalFavourite = mutableListOf<QuestShortInfo>()
    private val totalCompleted = mutableListOf<QuestShortInfo>()
    private val totalCreated = mutableListOf<QuestShortInfo>()

    private var completedPage = 0
    private var favouritePage = 0
    private var createdPage = 0

    private val pageSize = 20

    enum class ListType {
        FAVOURITE,
        COMPLETED,
        CREATED
    }

    fun fetchData(onLoadedCallBack: () -> Unit, onErrorCallBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getFavouriteEvents(0)
                    .forEach { favourite.add(it); totalFavourite.add(it) }
            } catch (_: ConnectException) {
                onErrorCallBack()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("fetch my events", e.message ?: "", e)
            }
        }.invokeOnCompletion {
            onLoadedCallBack()
        }

    }

    fun getNextPage(list: ListType, onErrorCallBack: () -> Unit) {
        when (list) {
            ListType.CREATED -> fillCreated(createdPage + 1, onErrorCallBack)
            ListType.COMPLETED -> fillCompleted(completedPage + 1, onErrorCallBack)
            ListType.FAVOURITE -> fillFavourite(favouritePage + 1, onErrorCallBack)
        }
    }

    fun getPrevPage(list: ListType, onErrorCallBack: () -> Unit) {
        when (list) {
            ListType.CREATED -> fillCreated(max(createdPage - 1, 0), onErrorCallBack)
            ListType.COMPLETED -> fillCompleted(max(completedPage - 1, 0), onErrorCallBack)
            ListType.FAVOURITE -> fillFavourite(max(favouritePage - 1, 0), onErrorCallBack)
        }
    }

    private fun fillCreated(page: Int, onErrorCallBack: () -> Unit) {
        if (totalCreated.size < pageSize * (page)) {
            created.clear()
            totalCreated
                .forEachIndexed { idx, it ->
                    if (idx <= pageSize * (page - 1) && idx < pageSize * page) {
                        totalCreated.add(it)
                    }
                }
            createdPage = page
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                var res: List<QuestShortInfo> = listOf()
                try {
                    res = repository.getCreatedEvents(page)
                } catch (_: ConnectException) {
                    onErrorCallBack()
                } catch (_: NotAuthorisedException) {
                    unauthorised.value = true
                } catch (e: Exception) {
                    Log.e("fetch created events", e.message ?: "", e)
                }
                if (res.isEmpty()) {
                    // TODO("toast")
                } else {
                    res.forEach { created.add(it); totalCreated.add(it) }
                    createdPage = page
                }
            }
        }
    }

    private fun fillCompleted(page: Int, onErrorCallBack: () -> Unit) {
        if (totalCompleted.size < pageSize * (page)) {
            completed.clear()
            totalCompleted
                .forEachIndexed { idx, it ->
                    if (idx <= pageSize * (page - 1) && idx < pageSize * page) {
                        totalCompleted.add(it)
                    }
                }
            completedPage = page
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                var res: List<QuestShortInfo> = listOf()
                try {
                    res = repository.getCompletedEvents(page)
                } catch (_: ConnectException) {
                    onErrorCallBack()
                } catch (_: NotAuthorisedException) {
                    unauthorised.value = true
                } catch (e: Exception) {
                    Log.e("fetch completed events", e.message ?: "", e)
                }
                if (res.isEmpty()) {
                    // TODO("toast")
                } else {
                    res.forEach { completed.add(it); totalCompleted.add(it) }
                    completedPage = page
                }
            }
        }
    }

    private fun fillFavourite(page: Int, onErrorCallBack: () -> Unit) {
        if (totalFavourite.size < pageSize * (page)) {
            favourite.clear()
            totalFavourite
                .forEachIndexed { idx, it ->
                    if (idx <= pageSize * (page - 1) && idx < pageSize * page) {
                        totalFavourite.add(it)
                    }
                }
            favouritePage = page
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                var res: List<QuestShortInfo> = listOf()
                try {
                    res = repository.getFavouriteEvents(page)
                } catch (_: ConnectException) {
                    onErrorCallBack()
                } catch (_: NotAuthorisedException) {
                    unauthorised.value = true
                } catch (e: Exception) {
                    Log.e("fetch favorite events", e.message ?: "", e)
                }
                if (res.isEmpty()) {
                    // TODO("toast")
                } else {
                    res.forEach { favourite.add(it); totalFavourite.add(it) }
                    favouritePage = page
                }
            }
        }
    }
}