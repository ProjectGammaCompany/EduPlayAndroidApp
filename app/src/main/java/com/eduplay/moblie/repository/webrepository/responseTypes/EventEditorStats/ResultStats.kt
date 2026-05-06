package com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats

data class ResultStats(
    val groupEvent: Boolean,
    val users: List<UserEditorStat>?,
    val groups: List<GroupEditorStats>?
)