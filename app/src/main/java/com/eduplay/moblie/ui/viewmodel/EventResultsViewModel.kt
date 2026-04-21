package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.utils.CoroutineContextProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class EventResultsViewModel @Inject constructor(private val repository: EduRepository,  private val coroutineContext: CoroutineContextProvider) :
    ViewModel() {
    val users = mutableStateListOf<PlayerStats.StatUser>()
    val groups = mutableStateListOf<PlayerStats.StatGroup>()
    val unauthorised = mutableStateOf(false)
    val noInternetConnection = mutableStateOf(false)

    fun fetchResults(eventId: String) {
        viewModelScope.launch(coroutineContext.Main) {
            try {
                val result = repository.getEventResults(eventId)
                users.clear()
                users.addAll(
                    result.users ?: listOf()
                )
                groups.clear()
                groups.addAll(
                    result.groups ?: listOf()
                )
            } catch (_: ConnectException) {
                noInternetConnection.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("results", e.message ?: "", e)
            }
        }
    }
}