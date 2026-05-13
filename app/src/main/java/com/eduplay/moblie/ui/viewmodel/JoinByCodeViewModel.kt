package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.requestTypes.EventPasswords
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class JoinByCodeViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    val showGroupFields = mutableStateOf(false)
    val noInternet = mutableStateOf(false)
    val badCode = mutableStateOf(false)
    val unauthorised = mutableStateOf(false)
    val badPasswords = mutableStateOf(false)
    val eventId = mutableStateOf<String?>(null)

    private var code: String? = null

    val proceedToPassword = mutableStateOf(false)

    fun getFields(joinCode: String) {
        viewModelScope.launch {
            try {
                showGroupFields.value = repository.getRequiredJoinFields(joinCode).groupFields
                proceedToPassword.value = true
                code = joinCode
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (_: NoSuchElementException) {
                badCode.value = true
            } catch (e: Exception) {
                Log.e("JOIN_CODE", e.message ?: "", e)
                badCode.value = true
            }
        }
    }

    fun validatePasswords(
        eventPassword: String,
        groupName: String,
        groupPassword: String
    ) {
        viewModelScope.launch {
            val group = if (showGroupFields.value) groupName else null
            val groupPass = if (showGroupFields.value) groupPassword else null
            try {
                eventId.value = repository.enterPrivateEvent(
                    code ?: "",
                    EventPasswords(
                        eventPassword,
                        group,
                        groupPass
                    )
                ).eventId
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: IllegalAccessException) {
                if (e.message?.contains("password") ?: false)
                    badPasswords.value = true
                else
                    Log.e("JOIN_CODE", e.message ?: "", e)
            } catch (e: Exception) {
                Log.e("JOIN_CODE", e.message ?: "", e)
            }
        }
    }
}