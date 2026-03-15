package com.eduplay.moblie.models

enum class EventStatus(val status: String) {
    NOT_STARTED("notStarted"),
    STARTED("in progress"),
    ENDED("finished");
    companion object {
        fun statusOf(value: String): EventStatus {
            for (status in entries) {
                if (status.status == value) {
                    return status
                }
            }
            return ENDED
        }
    }

}