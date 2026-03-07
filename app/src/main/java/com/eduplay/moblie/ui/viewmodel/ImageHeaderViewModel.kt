package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.network.NetworkHeaders
import com.eduplay.moblie.BuildConfig
import com.eduplay.moblie.services.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class ImageHeaderViewModel @Inject constructor(private val tokenManager: TokenManager) :
    ViewModel() {
    val headers = mutableStateOf<NetworkHeaders>(NetworkHeaders.Builder().build())

    init {
        runBlocking {
            viewModelScope.launch(Dispatchers.IO) {
                headers.value =
                    NetworkHeaders.Builder()
                        .set("Authorization", "Bearer ${tokenManager.getAccessToken().last()}")
                        .build()
            }
        }
    }
    fun getFullUrl(fileName: String): String {
        return BuildConfig.BACKEND_FILE_URL + fileName
    }
}