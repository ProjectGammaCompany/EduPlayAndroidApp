package com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats

data class UserEditorStat(
    val id: String,
    val username: String,
    val answers: Answer,
    val points: Int,
    val avatar: String?,
) {
    data class Answer(val correct: Int, val total: Int)
}