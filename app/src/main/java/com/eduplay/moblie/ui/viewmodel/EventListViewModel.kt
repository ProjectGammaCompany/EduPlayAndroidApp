package com.eduplay.moblie.ui.viewmodel

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

    fun changeFavourite(eventId: String, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addToFavourites(eventId, isFavorite)
            } catch (e: ConnectException) {
                noInternetConnection.value = true
            } catch (e: NotAuthorisedException) {
                unauthorised.value = true
            }
        }
    }
}