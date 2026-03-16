package com.eduplay.moblie.repository.responseTypes

import com.google.gson.annotations.SerializedName

data class AnswerResult(
    @SerializedName("rightAnswer")
    val rightAnswer: List<String>?,
    val points: Int?,
    @SerializedName("isCorrect")
    val isCorrect: TaskAnswerStatus?
)

enum class TaskAnswerStatus(val status: String) {
    CORRECT("correct"),
    INCORRECT("incorrect"),
    PARTIALLY("partially")

}