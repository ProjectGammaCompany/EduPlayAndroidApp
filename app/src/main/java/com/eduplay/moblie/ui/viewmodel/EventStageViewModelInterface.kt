package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.Task
import java.time.LocalDateTime

interface EventStageViewModelInterface {
    val currentStageType: MutableState<StageType>

    var currentTask: MutableState<Task?>

    var currentBlock: MutableState<Block?>

    var taskStartTime: LocalDateTime

    val answers: SnapshotStateList<String>
    val disableTask: MutableState<Boolean>
    val showResults: MutableState<Boolean>
    val correctAnswer: MutableList<String>
    var points: Int?
    var isAnswerCorrect: Boolean?

    fun getNextStage(eventId: String)

    fun sendAnswer(eventId: String)

    fun chooseTask(eventId: String, taskId: String)
}