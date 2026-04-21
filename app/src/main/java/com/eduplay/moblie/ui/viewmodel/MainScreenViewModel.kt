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
import com.eduplay.moblie.utils.CoroutineContextProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    val repository: EduRepository,
    private val coroutineContext: CoroutineContextProvider
) : ViewModel() {

    val unauthorised = mutableStateOf(false)
    val events = mutableStateOf(flowOf<PagingData<QuestShortInfo>>())
    val noInternetConnection = mutableStateOf(false)

    init {
        viewModelScope.launch(coroutineContext.Main) {
            try {
                events.value = repository.getEvents().cachedIn(viewModelScope)
            } catch (_: ConnectException) {
                noInternetConnection.value = true
                events.value = flowOf()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
                events.value = flowOf()
            } catch (e: Exception) {
                Log.e("All events", e.message ?: "", e)
                events.value = flowOf()
            }
        }
    }
}