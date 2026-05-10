package com.eduplay.moblie.ui.viewmodel

import android.content.ComponentName
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.useCases.downloadUsecases.DownloadStatusObserver
import com.eduplay.moblie.useCases.managers.OfflineModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class DownloadedEventsUpdateScreenViewModel @Inject constructor(
    private val repository: EduRepository,
    private val observer: DownloadStatusObserver,
    private val offlineModeManager: OfflineModeManager
) : ViewModel() {
    val events = mutableStateListOf<QuestShortInfo>()
    val noInternet = mutableStateOf(false)
    val isOfflineOn = mutableStateOf(false)
    val gotUpdates = mutableStateOf(false)

    init {
        viewModelScope.launch {
            var temEvents: List<QuestShortInfo>? = listOf<QuestShortInfo>()
            try {
                temEvents = repository.updateDownloadedEventsStatuses()
                gotUpdates.value = true
            } catch (e: ConnectException) {
                noInternet.value = true
            } catch (e: Exception) {
                Log.e("Updates", e.message ?: "", e)
                noInternet.value = true
            }
            if (temEvents == null) noInternet.value = true
            else events.addAll(temEvents)
        }
    }

    fun turnOnOfflineMode() {
        viewModelScope.launch {
            offlineModeManager.saveAppMode(OfflineModeManager.AppModes.OFFLINE)
        }
    }

    fun updateEvent(eventId: String, onDownloadEvent: (String, String) -> ComponentName?) {
        viewModelScope.launch {
            val url = try {
                repository.getEventFileUrl(eventId)
            } catch (e: IllegalAccessException) {
                Log.e("download_service", e.message ?: "", e)
                return@launch
            }
            onDownloadEvent(url, eventId)
        }
    }

    fun deleteEventFromDevice(eventId: String) {
        viewModelScope.launch {
            val isDeleted = repository.deleteEvent(eventId)
            if (isDeleted) {
                observer.deletedFile(eventId)
                events.remove(events.first { it.id == eventId })
            }
        }
    }
}