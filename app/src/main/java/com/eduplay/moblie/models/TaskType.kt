package com.eduplay.moblie.models

enum class TaskType (val optionNumber: Int) {
    INFO(0),
    SINGLE_CHOICE(1),
    MULTIPLE_CHOICE(2),
    TEXT(3),
    QR(4)
}