package com.eduplay.moblie.repository.responseTypes

import android.util.Log
import com.google.gson.annotations.SerializedName

data class AnswerResult(
    @SerializedName("rightAnswer")
    val rightAnswer: List<String>?,
    val points: Int?,
    @SerializedName("isCorrect")
    val isCorrect: TaskAnswerStatus?
)

enum class TaskAnswerStatus(val status: String, val altName: String? = null) {
    CORRECT("correct"),
    INCORRECT("incorrect"),
    PARTIALLY("partially", "partial");
    companion object {
        fun valueByStatus(value: String): TaskAnswerStatus {
            for (status in TaskAnswerStatus.entries) {
                if (value==status.status || (status.altName != null && value == status.altName)) {
                    return status
                }
            }
            Log.e("ENUM", "$value is not a valid TaskAnswerStatus")
            return INCORRECT
        }
    }
}