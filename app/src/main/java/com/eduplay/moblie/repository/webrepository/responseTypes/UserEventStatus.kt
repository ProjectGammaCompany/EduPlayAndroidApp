package com.eduplay.moblie.repository.webrepository.responseTypes

data class UserEventStatus(
    val eventId: String,
    val status: String,
    val type: String?,
    val taskId: String?,
    val blockId: String?,
    val timeStamp: String?,
    val groupName: String?,
    val lastEditionDate: String,
    val pointsInBlock: Int,
    val completedTasksInBlock: List<String>
)

data class UserEventStatusList(val events: List<UserEventStatus>)
