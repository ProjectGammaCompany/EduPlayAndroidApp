package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class JoinByGroupViewModel @Inject constructor(private val repository: EduRepository) :
    ViewModel() {
    val badPassword = mutableStateOf(false)
    val noInternet = mutableStateOf(false)
    val canProceedToEvent = mutableStateOf(false)

    fun joinByGroup(eventId: String, group: String, password: String) {
        viewModelScope.launch {
            try {
                repository.enterGroupEvent(eventId, group, password)
                canProceedToEvent.value = true
            } catch (e: IllegalAccessException) {
                Log.i("goroup_access", e.message ?: "", e)
                badPassword.value = true
            } catch (_: ConnectException) {
                noInternet.value = true
                Log.i("goroup_access", "no internet")
            } catch (e: Exception) {
                Log.i("goroup_access", e.message ?: "", e)
            }
        }
    }
}