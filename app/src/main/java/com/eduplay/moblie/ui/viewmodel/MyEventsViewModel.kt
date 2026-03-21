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
import java.net.ConnectException

@HiltViewModel
class MyEventsViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    var favourite: Flow<PagingData<QuestShortInfo>> = flowOf()
    var completed: Flow<PagingData<QuestShortInfo>> = flowOf()
    var created: Flow<PagingData<QuestShortInfo>> = flowOf()
    val unauthorised = mutableStateOf(false)

    val noInternetConnection = mutableStateOf(false)

    init {
        try {
            favourite = repository.getFavouriteEvents().cachedIn(viewModelScope)
            completed = repository.getCompletedEvents().cachedIn(viewModelScope)
            created = repository.getCreatedEvents().cachedIn(viewModelScope)
        } catch (_: ConnectException) {
            noInternetConnection.value = true
        } catch (_: NotAuthorisedException) {
            unauthorised.value = true
        } catch (e: Exception) {
            Log.e("MY_EVENTS_MODEL", e.message ?: "", e)
        }
    }

}