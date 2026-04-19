package com.eduplay.moblie.ui.viewmodel

import android.content.ComponentName
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
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.ResultStats
import com.eduplay.moblie.useCases.DateConverter
import com.eduplay.moblie.useCases.DownloadStatusObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.time.LocalDateTime


@HiltViewModel
class EventScreenViewModel @Inject constructor(val repository: EduRepository, val downloadStatusObserver: DownloadStatusObserver) : ViewModel() {
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
    val canDownload = mutableStateOf(false)
    val needGroup = mutableStateOf(false)
    val isRated = mutableStateOf(false)
    val groupEvent = mutableStateOf(false)
    val isDownloaded = mutableStateOf(false)
    val failedToSendAnswers = mutableStateOf(false)
    val needsUpdate = mutableStateOf(false)
    val editorEventStats = mutableStateOf(ResultStats(false, null, null))

    val noInternetConnection = mutableStateOf(false)

    fun fetchData(
        eventId: String,
        callBack: () -> Unit,
        onNoInternet: () -> Unit,
        context: Context
    ) {
        viewModelScope.launch {
            val role = repository.getRole(eventId)

            eventCreatorMode.value = role == EventRole.AUTHOR
            try {
                when (role) {
                    EventRole.AUTHOR -> fetchOwnerData(eventId, context)
                    EventRole.PARTICIPANT -> fetchPlayerData(eventId)
                }
            } catch (e: ConnectException) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
                onNoInternet()
            } catch (e: NotAuthorisedException) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
                unauthorised.value = true
            } catch (e: IllegalStateException) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
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
        description.value = data.description
        eventName.value = data.title
        author.value = data.authors.joinToString(", ") { it.email }
        cover.value = data.cover
        tags.clear()
        tags.addAll(data.tags ?: listOf())
        canDownload.value = data.canBeDownloaded
        needGroup.value = data.needGroup
        isRated.value = data.rated
        isDownloaded.value = data.isDownloaded
        needsUpdate.value = data.needsUpdate


        info.clear()
        info.add(
            Pair(R.string.rating, data.rate.toString() + '⭐')
        )

        var startTime = LocalDateTime.MAX
        if (data.startDate?.isNotBlank() ?: false) {
            startTime = DateConverter.convertFromServerFormat(data.startDate)
            info.add(Pair(R.string.opens, DateConverter.convertForDisplay(startTime) ))
        }
        var endTime = LocalDateTime.MIN
        if (data.endDate?.isNotBlank() ?: false) {
            endTime = DateConverter.convertFromServerFormat(data.endDate)
            Pair(R.string.closes, DateConverter.convertForDisplay(endTime))
        }

        isOpen.value = EventStatus.statusOf(data.status) != EventStatus.ENDED
                && (data.startDate == null || data.startDate.isBlank() || LocalDateTime.now() >= startTime)
                && (data.endDate == null || data.endDate.isBlank() || LocalDateTime.now() <= endTime)
        isContinuing.value =
            isOpen.value && EventStatus.statusOf(data.status) == EventStatus.STARTED
        isCompleted.value = EventStatus.statusOf(data.status) == EventStatus.ENDED

        // заранее отправляем ответы
        if (isDownloaded.value) {
            val result = repository.postAnswerBatch(eventId)
            if (!result) isOpen.value = false
        }
    }

    private suspend fun fetchOwnerData(eventId: String, context: Context) {
        val data = repository.getEventInfoOwner(eventId)

        eventName.value = data?.title ?: ""
        tags.clear()
        tags = data.tags?.toMutableStateList() ?: mutableStateListOf()
        description.value = data.description
        privateEvent.value = data.private
        author.value = data.collaboratos?.joinToString(", ") ?: ""
        groupEvent.value = data.groupEvent

        info.clear()

        if (data.startDate?.isNotBlank() ?: false) {
            info.add(Pair(R.string.opens, DateConverter.convertForDisplay(data.startDate)))
        }
        if (data.endDate?.isNotBlank() ?: false) {
            Pair(R.string.closes, DateConverter.convertForDisplay(data.endDate))
        }
        password.value = data.password ?: ""
        groups.addAll(data.groups ?: listOf())

        info.addAll(
            listOf(
                Pair(R.string.rating, (data.eventRating?.toString() ?: "0") + '⭐'),
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

        editorEventStats.value = repository.getEventEditorStats(eventId)
        if (privateEvent.value) {
            try {
                joinCode.value = repository.getJoinCode(eventId).joinCode
            } catch (e: Exception) {
                Log.e("JOIN_CODE", e.message ?: "", e)
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
            try {
                repository.complain(eventId, reason)
            }  catch (e: ConnectException) {
                Log.e("complain", e.message ?: e.toString(), e)
                noInternetConnection.value = true
            } catch (e: NotAuthorisedException) {
                Log.e("complain", e.message ?: e.toString(), e)
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("complain", e.message ?: e.toString(), e)
            }
        }
    }

    fun downloadEvent(eventId: String, onDownloadEvent: (String, String) -> ComponentName?) {
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

    fun rateEvent(rating: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isRated.value = repository.postEventRating(rating)
            } catch (e: ConnectException) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
                noInternetConnection.value = true
            } catch (e: NotAuthorisedException) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
                unauthorised.value = true
            } catch (e: IllegalStateException) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
                noInternetConnection.value = true
            } catch (e: Exception) {
                Log.e("Fetch_event_screen", e.message ?: e.toString(), e)
            }
        }
    }

    fun deleteEventFromDevice(eventId: String) {
        viewModelScope.launch {
            isDownloaded.value = !repository.deleteEvent(eventId)
            if (isDownloaded.value == false) {
                downloadStatusObserver.deletedFile(eventId)
            }
        }
    }
}