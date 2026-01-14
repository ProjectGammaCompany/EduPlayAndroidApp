package com.eduplay.moblie.repository.responseTypes

data class ShortTask(
    val id: String,
    val name: String,
    val time: Int,
    val isCompleted: Boolean
)