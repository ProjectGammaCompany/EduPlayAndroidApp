package com.eduplay.moblie.models

enum class TaskType(val optionNumber: Int) {
    INFO(0),
    SINGLE_CHOICE(1),
    MULTIPLE_CHOICE(2),
    TEXT(3),
    QR(4);

    companion object {
        fun valueOf(optionNumber: Int): TaskType {
            for (e in TaskType.entries) {
                if (e.optionNumber == optionNumber) return e
            }
            return INFO
        }
    }
}