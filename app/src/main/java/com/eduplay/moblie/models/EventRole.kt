package com.eduplay.moblie.models

import com.google.gson.annotations.SerializedName

enum class EventRole(val id: Int) {
    @SerializedName("0")
    PARTICIPANT(0),
    @SerializedName("1")
    AUTHOR(1)
}