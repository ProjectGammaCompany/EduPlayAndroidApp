package com.eduplay.moblie.repository.webrepository.responseTypes

import com.eduplay.moblie.repository.responseTypes.Block

data class EventStage(
    val type: String,
    val task: Task?,
    val block: Block?
)