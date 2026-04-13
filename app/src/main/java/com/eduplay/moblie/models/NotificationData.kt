package com.eduplay.moblie.models

import java.time.LocalDateTime

sealed class NotificationData (val notificationId: String,) {

    class EmptyNotification():NotificationData("")

    class FavoriteNotificationData(
        notificationId: String,
        val eventId: String,
        val eventName: String,
        val date: LocalDateTime
    ) :NotificationData(notificationId)

    class EndEventNotificationData(
        notificationId: String,
        val eventId: String,
        val eventName: String,
        val date: LocalDateTime,
        val timeLeft: TimeLeft,
        val notStartedFavorite: Boolean
    ) : NotificationData(notificationId) {
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