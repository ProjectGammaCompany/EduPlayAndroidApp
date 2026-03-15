package com.eduplay.moblie.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.R
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.EventGroup
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventStatus
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@HiltViewModel
class EventScreenViewModel @Inject constructor(val repository: EduRepository) : ViewModel() {
    val eventCreatorMode = mutableStateOf(false)
    val isEventFavourite = mutableStateOf(false)
    val isCompleted = mutableStateOf(false)
    val isOpen = mutableStateOf(false)
    val isContinuing = mutableStateOf(false)
    val eventName = mutableStateOf("")
    val author = mutableStateOf("")
    var tags = mutableStateListOf<EventTag>()
    val info = mutableStateListOf<Pair<Int, String?>>()
    val description = mutableStateOf("")
    val privateEvent = mutableStateOf(true)
    val cover = mutableStateOf("")
    val unauthorised = mutableStateOf(false)
    val password = mutableStateOf("")
    val groups = mutableStateListOf<EventGroup>()
    val joinCode = mutableStateOf("")

    fun fetchData(eventId: String, callBack: () -> Unit, onNoInternet: () -> Unit, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val role = repository.getRole(eventId)

            eventCreatorMode.value = role == EventRole.AUTHOR
            try {
                when (role) {
                    EventRole.AUTHOR -> fetchOwnerData(eventId, context)
                    EventRole.PARTICIPANT -> fetchPlayerData(eventId)
                }
            } catch (_: ConnectException) {
                onNoInternet()
            } catch (e: NotAuthorisedException) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
                unauthorised.value = true
            } catch (_: IllegalStateException) {
                onNoInternet()
            } catch (e: Exception) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
            }
        }
            .invokeOnCompletion {
                callBack()
            }
    }

    private suspend fun fetchPlayerData(eventId: String) {
        val data = repository.getEventInfoPlayer(eventId)

        isEventFavourite.value = data.favorite

        tags = data.tags.toMutableStateList()
        description.value = data.description
        eventName.value = data.title
        author.value = data.authors.joinToString(", ") { it.email }
        cover.value = data.cover

        info.clear()
        info.add(
            Pair(R.string.rating, data.rate.toString() + '⭐')
        )
        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS")
        val presentingFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

        if (data.startDate?.isNotBlank() ?: false) {
            val startTime = LocalDateTime.parse(data.startDate, dateFormatter)
            info.add(Pair(R.string.opens, startTime.format(presentingFormatter)))
        }
        if (data.endDate?.isNotBlank() ?: false) {
            val endTime = LocalDateTime.parse(data.endDate, dateFormatter)
            Pair(R.string.closes, endTime.format(presentingFormatter))
        }

        isOpen.value = EventStatus.statusOf(data.status) != EventStatus.ENDED
                && (data.startDate == null || LocalDateTime.now() >= LocalDateTime.parse(
            data.startDate,
            dateFormatter
        ))
                && (data.endDate == null || LocalDateTime.now() <= LocalDateTime.parse(
            data.endDate,
            dateFormatter
        ))
        isContinuing.value =
            isOpen.value && EventStatus.statusOf(data.status) == EventStatus.STARTED
        isCompleted.value = EventStatus.statusOf(data.status) == EventStatus.ENDED
    }

    private suspend fun fetchOwnerData(eventId: String, context: Context) {
        val data = repository.getEventInfoOwner(eventId)

        eventName.value = data?.title ?: ""
        tags = data.tags?.toMutableStateList() ?: mutableStateListOf()
        description.value = data.description
        privateEvent.value = data.private
        author.value = data.collaboratos?.joinToString(", ") ?: ""

        info.clear()

        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss.SSS")
        val presentingFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

        if (data.startDate?.isNotBlank() ?: false) {
            val startTime = LocalDateTime.parse(data.startDate, dateFormatter)
            info.add(Pair(R.string.opens, startTime.format(presentingFormatter)))
        }
        if (data.endDate?.isNotBlank() ?: false) {
            val endTime = LocalDateTime.parse(data.endDate, dateFormatter)
            Pair(R.string.closes, endTime.format(presentingFormatter))
        }
        password.value = data.password ?: ""
        groups.addAll(data.groups ?: listOf())

        info.addAll(
            listOf(
                Pair(R.string.rating, (data.eventRating?.toString() ?: "0") + '⭐'),
                Pair(R.string.groups, data.groupNames.joinToString { ", " }),
                Pair(R.string.last_edition, data.lastEditionDate),
                Pair(
                    R.string.private_event_flag,
                    if (privateEvent.value) context.getString(R.string.private_event) else context.getString(
                        R.string.public_event
                    )
                ),
                Pair(
                    R.string.group_event,
                    if (data.groupEvent) context.getString(R.string.yes) else context.getString(R.string.no)
                ),
                Pair(
                    R.string.allow_download,
                    if (data.allowDownloading) context.getString(R.string.yes) else context.getString(
                        R.string.no
                    )
                )
            )
        )

        if (privateEvent.value) {
            try {
                joinCode.value = repository.getJoinCode(eventId).joinCode
            } catch (e: Exception) {
                Log.e("JOIN_CODE", e.message ?:"", e)
            }
        }
    }

    fun addToFavourite(eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavourites(eventId, true)
        }
        isEventFavourite.value = true
    }

    fun removeFromFavourite(eventId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToFavourites(eventId, false)
        }
        isEventFavourite.value = false
    }

    fun complain(eventId: String, reason: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.complain(eventId, reason)
        }
    }
}