package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: EduRepository): ViewModel() {
    val unauthorised = mutableStateOf(false)
    val didntFindEvents = mutableStateOf(false)
    val events: MutableState<Flow<PagingData<QuestShortInfo>>> = mutableStateOf(flowOf())
    val noInternetConnection = mutableStateOf(false)
    val tags = mutableStateListOf<String>()
    private val tagIds = mutableMapOf<String, String>()

    init {
        viewModelScope.launch {
            try {
                tags.addAll(repository
                    .getTags()
                    .tags
                    .map {
                        tagIds[it.name] = it.id
                        it.name
                    }
                )
            } catch (_: ConnectException) {
                noInternetConnection.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("All events", e.message ?: "", e)
                didntFindEvents.value = true
            }
        }
    }

    fun searchEvents(
        tags: List<String>? = null,
        decliningRating: Boolean = false,
        active: Boolean = false,
        favorites: Boolean = false,
        title: String = ""
    ) {
        val searchTags: List<String> = tags?.map { tagIds[it] ?: "" }?.toList() ?: listOf()
        try {
            events.value = repository
                .getEvents(
                    tags = searchTags.ifEmpty { null },
                    decliningRating = decliningRating,
                    active = active,
                    favorites = favorites,
                    title = title,
                )
                .cachedIn(viewModelScope)
            didntFindEvents.value = false
        } catch (_: ConnectException) {
            noInternetConnection.value = true
            didntFindEvents.value = true
            events.value = flowOf()
        } catch (_: NotAuthorisedException) {
            unauthorised.value = true
            events.value = flowOf()
        }  catch (e: Exception) {
            Log.e("All events", e.message ?: "", e)
            events.value = flowOf()
            didntFindEvents.value = true
        }
    }
}