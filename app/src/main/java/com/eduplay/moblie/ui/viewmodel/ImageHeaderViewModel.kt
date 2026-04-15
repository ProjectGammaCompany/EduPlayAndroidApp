package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.network.NetworkHeaders
import com.eduplay.moblie.BuildConfig
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.useCases.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class ImageHeaderViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val modeManager: OfflineModeManager
) :
    ViewModel(), ImageHeaderInterface {
    override val headers = mutableStateOf<NetworkHeaders>(NetworkHeaders.Builder().build())
    override val appMode = mutableStateOf(modeManager.getAppMode())

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

    override fun getFullUrl(fileName: String): String {
        val initialFilePath = runBlocking {
            when (modeManager.getAppMode().first()) {
                OfflineModeManager.AppModes.ONLINE -> BuildConfig.BACKEND_FILE_URL
                OfflineModeManager.AppModes.OFFLINE -> ""
            }
        }
        return initialFilePath + fileName
    }


}
interface ImageHeaderInterface {
    fun getFullUrl(fileName: String): String
    val headers: MutableState<NetworkHeaders>
    val appMode: MutableState<Flow<OfflineModeManager.AppModes>>
}