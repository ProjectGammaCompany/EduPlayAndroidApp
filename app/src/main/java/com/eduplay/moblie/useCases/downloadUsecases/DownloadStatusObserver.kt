package com.eduplay.moblie.useCases.downloadUsecases

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateSetOf

object DownloadStatusObserver {
    // key - download url, value - downloadId
    val downloading = mutableStateMapOf<String, String>()
    val downloaded = mutableStateSetOf<String>()

    fun addToDownloading(eventId: String, eventUrl: String) {
        downloading[eventUrl] = eventId
    }

    fun updateDownloaded(eventUrl: String, success: Boolean) {
        val eventId = downloading.remove(eventUrl)
        if (eventId != null && success)
            downloaded.add(eventId)
    }

    fun downloadFailed(eventUrl: String) {
        downloading.remove(eventUrl)
    }

    fun deletedFile(eventId: String) {
        downloaded.remove(eventId)
    }

}