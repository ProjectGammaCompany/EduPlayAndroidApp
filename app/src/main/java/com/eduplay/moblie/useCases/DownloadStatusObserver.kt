package com.eduplay.moblie.useCases

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateSetOf
import kotlin.collections.set

object DownloadStatusObserver {
    // key - download url, value - downloadId
    val downloading = mutableStateMapOf<String, String>()
    val downloaded = mutableStateSetOf<String>()

    fun addToDownloading(eventId: String, eventUrl: String) {
        downloading[eventUrl] = eventId
    }

    fun updateDownloaded(eventUrl: String) {
        val eventId = downloading.remove(eventUrl)
        if (eventId != null)
            downloaded.add(eventId)
    }

    fun downloadFailed(eventUrl: String) {
        downloading.remove(eventUrl)
    }
}