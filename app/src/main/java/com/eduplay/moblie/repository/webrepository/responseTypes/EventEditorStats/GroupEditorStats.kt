package com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats

data class GroupEditorStats(
    val id: String,
    val name: String,
    val users: List<UserEditorStat>
)