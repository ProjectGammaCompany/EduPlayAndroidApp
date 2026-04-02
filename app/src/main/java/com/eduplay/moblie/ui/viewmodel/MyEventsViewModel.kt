package com.eduplay.moblie.ui.viewmodel

import android.util.Log
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class MyEventsViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    val favourite = mutableStateOf(flowOf<PagingData<QuestShortInfo>>())
    val completed = mutableStateOf(flowOf<PagingData<QuestShortInfo>>())
    val created = mutableStateOf(flowOf<PagingData<QuestShortInfo>>())
    val unauthorised = mutableStateOf(false)

    val noInternetConnection = mutableStateOf(false)

    init {
        viewModelScope.launch {
            try {
                favourite.value = repository.getFavouriteEvents().cachedIn(viewModelScope)
                completed.value = repository.getCompletedEvents().cachedIn(viewModelScope)
                created.value = repository.getCreatedEvents().cachedIn(viewModelScope)
            } catch (_: ConnectException) {
                noInternetConnection.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("MY_EVENTS_MODEL", e.message ?: "", e)
            }
        }
    }

}