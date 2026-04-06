package com.eduplay.moblie.repository.webrepository.responseTypes

data class Notification (
    val id: String,
    val type: String,
    val date: String,
    val favoriteEventStartExtra: FavoriteEventStartExtra?,
    val eventEndExtra: EventEndExtra?
) {
    enum class NotificationType(val type: String) {
        FAVORITE_START("favoriteEventStart"),
        EVENT_END("eventEnd");
        companion object {
            fun valueByType(type: String): NotificationType? {
                for (entry in entries) {
                    if (entry.type == type) return entry
                }
                return null
            }
        }
    }
}