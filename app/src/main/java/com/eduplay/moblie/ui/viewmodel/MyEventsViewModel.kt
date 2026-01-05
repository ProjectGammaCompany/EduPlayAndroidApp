package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.forEach
import kotlin.math.max

@HiltViewModel
class MyEventsViewModel @Inject constructor(private val repository: EduRepository): ViewModel() {
    val favourite = mutableStateListOf<QuestShortInfo>()
    val completed = mutableStateListOf<QuestShortInfo>()
    val created = mutableStateListOf<QuestShortInfo>()

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

    fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavouriteEvents(0).forEach { favourite.add(it); totalFavourite.add(it) }
        }

    }

    fun getNextPage(list: ListType) {
        when(list) {
            ListType.CREATED -> fillCreated(createdPage+1)
            ListType.COMPLETED -> fillCompleted(completedPage+1)
            ListType.FAVOURITE -> fillFavourite(favouritePage+1)
        }
    }

    fun getPrevPage(list: ListType) {
        when(list) {
            ListType.CREATED -> fillCreated(max(createdPage-1, 0))
            ListType.COMPLETED -> fillCompleted(max(completedPage-1, 0))
            ListType.FAVOURITE -> fillFavourite(max(favouritePage-1, 0))
        }
    }

    private fun fillCreated(page: Int) {
        if (totalCreated.size < pageSize*(page)) {
            created.clear();
            totalCreated
                .forEachIndexed { idx, it ->
                    if (idx <= pageSize*(page-1) && idx < pageSize*page ) {
                        totalCreated.add(it)
                    }
                }
            createdPage = page
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val res = repository.getCreatedEvents(page)
                if (res.isEmpty()) {
                    // TODO("toast")
                } else {
                    res.forEach { created.add(it); totalCreated.add(it) }
                    createdPage = page
                }
            }
        }
    }

    private fun fillCompleted(page: Int) {
        if (totalCompleted.size < pageSize*(page)) {
            completed.clear();
            totalCompleted
                .forEachIndexed { idx, it ->
                    if (idx <= pageSize*(page-1) && idx < pageSize*page ) {
                        totalCompleted.add(it)
                    }
                }
            completedPage = page
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val res = repository.getCompletedEvents(page)
                if (res.isEmpty()) {
                    // TODO("toast")
                } else {
                    res.forEach { completed.add(it); totalCompleted.add(it) }
                    completedPage = page
                }
            }
        }
    }

    private fun fillFavourite(page: Int) {
        if (totalFavourite.size < pageSize*(page)) {
            favourite.clear();
            totalFavourite
                .forEachIndexed { idx, it ->
                    if (idx <= pageSize*(page-1) && idx < pageSize*page ) {
                        totalFavourite.add(it)
                    }
                }
            favouritePage = page
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val res = repository.getFavouriteEvents(page)
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