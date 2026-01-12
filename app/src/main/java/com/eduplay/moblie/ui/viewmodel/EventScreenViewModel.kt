package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.R
import com.eduplay.moblie.models.EventRole
import com.eduplay.moblie.models.EventStatus
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class EventScreenViewModel @Inject constructor(val repository: EduRepository) : ViewModel() {
    val eventCreatorMode = mutableStateOf(false)
    val isEventFavourite = mutableStateOf(false)
    val isCompleted = mutableStateOf(false)
    val isOpen = mutableStateOf(false)
    val isContinuing = mutableStateOf(false)
    val eventName = mutableStateOf("")
    val author = mutableStateOf("")
    val rating = mutableStateOf("")
    val opens = mutableStateOf<String?>(null)
    val closes = mutableStateOf<String?>(null)
    var tags = mutableStateListOf<String>()
    val info = mutableStateListOf<Pair<Int, String?>>(
        Pair(R.string.rating, rating.value),
        Pair(R.string.opens, opens.value),
        Pair(R.string.closes, closes.value)
    )
    val description = mutableStateOf("")
    val privateEvent = mutableStateOf(true)
    val cover = mutableStateOf("")

    fun fetchData(eventId: String, callBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val role = repository.getRole(eventId)

            eventCreatorMode.value = role == EventRole.AUTHOR

            when (role) {
                EventRole.AUTHOR -> fetchOwnerData(eventId)
                EventRole.PARTICIPANT -> fetchPlayerData(eventId)
            }
        }.invokeOnCompletion { callBack() }
    }

    private suspend fun fetchPlayerData(eventId: String) {
        val data = repository.getEventInfoPlayer(eventId)

        isEventFavourite.value = data.favorite

        tags = data.tags.toMutableStateList()
        opens.value = data.startDate
        closes.value = data.endDate
        rating.value = data.rate.toString() + '⭐'
        description.value = data.description

        info.clear()
        info.addAll(
            listOf(
                Pair(R.string.rating, rating.value),
                Pair(R.string.opens, opens.value),
                Pair(R.string.closes, closes.value)
            )
        )

        //TODO("check status")
        isOpen.value = !data.completed && data.status != EventStatus.ENDED
                && (data.startDate == null || LocalDateTime.now() >= LocalDateTime.parse(data.startDate))
        isContinuing.value = isOpen.value && data.status == EventStatus.STARTED
                && (
                data.endDate == null
                        || (LocalDateTime.now() >= LocalDateTime.parse(data.startDate)
                        && LocalDateTime.now() <= LocalDateTime.parse(data.endDate)
                        )
                )

        eventName.value = data.title
        author.value = data.authors.joinToString(", ")
        isCompleted.value = data.completed
    }

    private suspend fun fetchOwnerData(eventId: String) {
        val data = repository.getEventInfoOwner(eventId)

        tags = data.tags.toMutableStateList()
        opens.value = data.startDate
        closes.value = data.endDate
        rating.value = data.rating.toString() + '⭐'
        description.value = data.description
        privateEvent.value = data.private

        info.clear()
        info.addAll(
            listOf(
                Pair(R.string.rating, rating.value),
                Pair(R.string.opens, opens.value),
                Pair(R.string.closes, closes.value),
                Pair(R.string.groups, data.groups.joinToString { ", " }),
                Pair(R.string.last_edition, data.lastEditionDate),
            )
        )
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