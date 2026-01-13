package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@HiltViewModel
class EventStageViewmodel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    val currentStageType = mutableStateOf(StageType.NONE)

    var currentTask = mutableStateOf<Task?>(null)
        private set
    var currentBlock = mutableStateOf<Block?>(null)
        private set
    var taskStartTime: LocalDateTime = LocalDateTime.MIN
        private set
    val answers = mutableStateListOf<String>()
    val disableTask = mutableStateOf(false)
    val showResults = mutableStateOf(false)
    val correctAnswer = mutableListOf<String>()
    var points: Int? = null
        private set
    var isAnswerCorrect: Boolean? = false
        private set

    fun getNextStage(eventId: String) {
        currentStageType.value = StageType.NONE
        viewModelScope.launch {
            val result = repository.getNextStage(eventId)
            clear()
            currentStageType.value = result.type
            currentTask.value = result.task
            currentBlock.value = result.block
            taskStartTime = LocalDateTime.parse(result.task?.timeStamp) ?: LocalDateTime.now()
        }.invokeOnCompletion {
            if (currentStageType.value == StageType.TASK && currentTask.value?.timeStamp == null) {
                sendStartTime(eventId)
            }
        }
    }

    private fun clear() {
        answers.clear()
        disableTask.value = false
        showResults.value = false
        correctAnswer.clear()
        points = null
        isAnswerCorrect = false
    }

    fun sendAnswer(eventId: String) {
        disableTask.value = true
        if (currentStageType.value == StageType.TASK) {
            viewModelScope.launch {
                val resultingAnswer =
                    if (currentTask.value!!.type == TaskType.MULTIPLE_CHOICE) {
                        answers.toList()
                    } else {
                        listOf(answers.last())
                    }
                val stageResult = repository.postAnswer(
                    eventId,
                    currentTask.value?.blockId!!,
                    currentTask.value?.id!!,
                    resultingAnswer
                )
                points = stageResult.points
                isAnswerCorrect = stageResult.isCorrect
                correctAnswer.addAll(stageResult.rightAnswer ?: listOf())
                if (stageResult.rightAnswer == null && stageResult.points == null) {
                    currentStageType.value = StageType.NONE
                } else {
                    showResults.value = true
                }
            }
        }
    }

    private fun sendStartTime(eventId: String) {
        viewModelScope.launch {
            repository.postTaskStartTime(
                eventId,
                currentTask.value?.blockId ?: "",
                currentTask.value?.id ?: "",
                taskStartTime
            )
        }
    }

    fun chooseTask(eventId: String, taskId: String) {
        viewModelScope.launch {
            try {
                repository.postTaskChoice(eventId, currentBlock.value?.id ?: "", taskId)
            } catch (e: IllegalAccessException) {

            } catch (e: Exception) {

            }
        }.invokeOnCompletion { currentStageType.value = StageType.NONE }
    }
}