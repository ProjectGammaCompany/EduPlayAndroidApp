package com.eduplay.moblie.repository.responseTypes

data class Block(
    val id: String,
    val name: String,
    val tasks: List<ShortTask>
)