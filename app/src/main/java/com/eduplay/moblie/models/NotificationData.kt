package com.eduplay.moblie.models

import java.time.LocalDateTime

sealed class NotificationData () {

    class EmptyNotification():NotificationData()

    data class FavoriteNotificationData(
        val eventId: String,
        val eventName: String,
        val date: LocalDateTime
    ) :NotificationData()

    data class EndEventNotificationData(
        val eventId: String,
        val eventName: String,
        val date: LocalDateTime,
        val timeLeft: TimeLeft,
        val notStartedFavorite: Boolean
    ) : NotificationData() {
        enum class TimeLeft(val time: String) {
            HOUR("hour"),
            DAY("day");

            companion object {
                fun valueByTime(time: String?): TimeLeft? {
                    for (entry in entries) {
                        if (entry.time == time) return entry
                    }
                    return null
                }
            }
        }
    }
}