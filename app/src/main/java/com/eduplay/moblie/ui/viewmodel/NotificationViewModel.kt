package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class NotificationViewModel @Inject constructor(private val repository: EduRepository): ViewModel() {
    val unauthorised = mutableStateOf(false)
    val notifications = mutableStateOf(flowOf<PagingData<NotificationData>>())
    val noInternetConnection = mutableStateOf(false)
    val deletedNotifications = mutableStateSetOf<String>()

    init {
        viewModelScope.launch {
            try {
                notifications.value = repository.getNotifications().cachedIn(viewModelScope)
            } catch (_: ConnectException) {
                noInternetConnection.value = true
                notifications.value = flowOf()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
                notifications.value = flowOf()
            } catch (e: Exception) {
                Log.e("All_notifications", e.message ?: "", e)
                notifications.value = flowOf()
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                val result = repository.deleteNotifications(notificationId)
                if (result) {
                    deletedNotifications.add(notificationId)
                }
            } catch (_: ConnectException) {
                noInternetConnection.value = true
                notifications.value = flowOf()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
                notifications.value = flowOf()
            } catch (e: Exception) {
                Log.e("All_notifications", e.message ?: "", e)
                notifications.value = flowOf()
            }
        }
    }
}