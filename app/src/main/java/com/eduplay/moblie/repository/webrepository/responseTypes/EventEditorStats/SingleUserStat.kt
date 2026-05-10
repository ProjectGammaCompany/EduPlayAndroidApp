package com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats

data class SingleUserStat(val blocks: List<SingleUserBlock>) {

    data class SingleUserBlock(
        val id: String,
        val name: String,
        val tasks: List<SingleUserTask>
    )

    data class SingleUserTask(
        val id: String,
        val name: String,
        val type: Int,
        val status: String,
        val options: List<StatOption>,
        val userAnswers: List<String>,
        val userPoints: Int,
        val points: Int,
    )

    data class StatOption(
        val id: String,
        val value: String,
        val isCorrect: Boolean,
    )
}
