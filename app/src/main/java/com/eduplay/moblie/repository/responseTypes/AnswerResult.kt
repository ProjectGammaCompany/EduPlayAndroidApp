package com.eduplay.moblie.repository.responseTypes

data class AnswerResult(
    val rightAnswer: List<String>?,
    val points: Int?,
    val isCorrect: Boolean?
)