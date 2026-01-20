package com.eduplay.moblie.ui.viewmodel

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
class MainScreenViewModel @Inject constructor(val repository: EduRepository) : ViewModel() {

    val unauthorised = mutableStateOf(false)

    fun getEventList(onConnectionFailed: ()->Unit): Flow<PagingData<QuestShortInfo>> {
        try {
            return repository.getEvents().cachedIn(viewModelScope)
        } catch (e: ConnectException) {
            onConnectionFailed()
        } catch (e: NotAuthorisedException) {
            unauthorised.value = true
        }
        return flowOf()
    }
}