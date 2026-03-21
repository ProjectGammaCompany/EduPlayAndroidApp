package com.eduplay.moblie.repository.responseTypes

enum class StageType(val stageName: String) {
    TASK("task"),
    BLOCK("block"),
    END("end"),
    NONE("none");
    companion object {
        fun stringValueOf(value: String): StageType {
            for (element in StageType.entries) {
                if (element.stageName == value) {
                    return element
                }
            }
            return NONE
        }
    }
}