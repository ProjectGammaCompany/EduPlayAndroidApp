package com.eduplay.moblie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class EventListViewModel @Inject constructor(private val repository: EduRepository): ViewModel() {
    fun changeFavourite(eventId: String, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavourites(eventId, isFavorite)
        }
    }
}