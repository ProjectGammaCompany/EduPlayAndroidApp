package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class EventListViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    val noInternetConnection = mutableStateOf(false)
    val unauthorised = mutableStateOf(false)
    val unknownError = mutableStateOf(false)

    fun changeFavourite(eventId: String, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addToFavourites(eventId, isFavorite)
            } catch (_: ConnectException) {
                noInternetConnection.value = true
                Log.i("main screen fetch events", "no internet")
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
                Log.i("main screen fetch events", "not authorised")
            } catch (e: Exception) {
                unknownError.value = true
                Log.e("main screen fetch events", e.message ?: "unknown error")
            }
        }
    }
}