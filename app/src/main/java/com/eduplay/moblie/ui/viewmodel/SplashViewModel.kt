package com.eduplay.moblie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.useCases.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel @Inject constructor(
    eduRepository: EduRepository,
    tokenManager: TokenManager
) : ViewModel() {
    private val mutableStateFlow = MutableStateFlow(true)
    val isLoading = mutableStateFlow.asStateFlow()

    private val isAuthorisedFlow = MutableStateFlow(true)
    val isAuthorised = isAuthorisedFlow.asStateFlow()


    init {
        viewModelScope.launch {
            try {
                eduRepository.getProfile()
            } catch (_: NotAuthorisedException) {
                isAuthorisedFlow.value = false
            } catch (_: Exception) {
                isAuthorisedFlow.value = tokenManager.getAccessToken().first().isNotEmpty()
            }

            mutableStateFlow.value = false
        }
    }
}