package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class EventResultsViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    val points = mutableIntStateOf(0)

    fun fetchResults(eventId: String, onNoInternet: ()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getEventResults(eventId)
                points.intValue = result.points
            } catch (e: ConnectException) {
                onNoInternet()
            }
        }
    }
}