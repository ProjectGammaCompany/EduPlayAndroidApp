package com.eduplay.moblie.repository.responseTypes

data class EventStage (
    val type: StageType,
    val task: Task?,
    val block: Block?
)