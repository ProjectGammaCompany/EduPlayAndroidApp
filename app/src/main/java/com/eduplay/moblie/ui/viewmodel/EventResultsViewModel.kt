package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class EventResultsViewModel @Inject constructor(private val repository: EduRepository) :
    ViewModel() {
    val users = mutableStateListOf<PlayerStats.StatUser>()
    val groups = mutableStateListOf<PlayerStats.StatGroup>()
    val unauthorised = mutableStateOf(false)
    fun fetchResults(eventId: String, onNoInternet: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {
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
                onNoInternet()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("results", e.message ?: "", e)
            }
        }
    }
}