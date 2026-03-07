package com.eduplay.moblie.repository.responseTypes

data class EventStage(
    val type: String,
    val task: Task?,
    val block: Block?
)