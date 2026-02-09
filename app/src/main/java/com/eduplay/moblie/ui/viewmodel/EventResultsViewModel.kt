package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
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
class EventResultsViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    val points = mutableIntStateOf(0)
    val unauthorised = mutableStateOf(false)

    fun fetchResults(eventId: String, onNoInternet: ()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repository.getEventResults(eventId)
                points.intValue = result.points
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