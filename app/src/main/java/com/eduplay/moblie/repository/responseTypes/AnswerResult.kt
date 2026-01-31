package com.eduplay.moblie.repository.responseTypes

import com.google.gson.annotations.SerializedName

data class AnswerResult(
    @SerializedName("right_answer")
    val rightAnswer: List<String>?,
    val points: Int?,
    @SerializedName("is_correct")
    val isCorrect: TaskAnswerStatus?
)

enum class TaskAnswerStatus(val status: String) {
    CORRECT("correct"),
    INCORRECT("incorrect"),
    PARTIALLY("partially")

}