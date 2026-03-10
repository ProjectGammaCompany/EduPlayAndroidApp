package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.EventStage
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.Task
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.useCases.FileDownloadStatus
import com.eduplay.moblie.useCases.TaskDownloadUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.time.LocalDateTime

@HiltViewModel
class EventStageViewmodel @Inject constructor(
    private val repository: EduRepository,
    private val taskDownloader: TaskDownloadUseCase
) : ViewModel(), EventStageViewModelInterface {
    override val currentStageType = mutableStateOf(StageType.NONE)

    override var currentTask = mutableStateOf<Task?>(null)

    override var currentBlock = mutableStateOf<Block?>(null)

    override var taskStartTime: LocalDateTime = LocalDateTime.MIN

    override val answers = mutableStateListOf<String>()
    override val disableTask = mutableStateOf(false)
    override val showResults = mutableStateOf(false)
    override val correctAnswer = mutableListOf<String>()
    override var points: Int? = null

    override var isAnswerCorrect: TaskAnswerStatus? = null
    val unauthorised = mutableStateOf(false)

    override val fileStatusFlows = mutableStateMapOf<String, Flow<FileDownloadStatus>>()

    var bluetoothCallBack: (Int) -> Unit = {}

    override fun getNextStage(eventId: String, onNoInternet: () -> Unit) {
        currentStageType.value = StageType.NONE
        viewModelScope.launch {
            try {
                val result = repository.getNextStage(eventId)
                clear()
                currentStageType.value = StageType.stringValueOf(result.type)
                currentTask.value = result.task
                currentBlock.value = result.block
                taskStartTime = if (result.task?.timeStamp == null || result.task.timeStamp.isBlank()) {
                                    LocalDateTime.now()
                                } else {
                                    LocalDateTime.parse(result.task.timeStamp)
                                }

            } catch (_: ConnectException) {
                onNoInternet()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("EventStage", e.message ?: "", e)
            }
        }.invokeOnCompletion {
            if (currentStageType.value == StageType.TASK && currentTask.value?.timeStamp == null) {
                sendStartTime(eventId, onNoInternet)
            }
        }
    }

    private fun clear() {
        answers.clear()
        disableTask.value = false
        showResults.value = false
        correctAnswer.clear()
        points = null
        isAnswerCorrect = null
    }

    override fun sendAnswer(eventId: String, onNoInternet: () -> Unit) {
        disableTask.value = true
        if (currentStageType.value == StageType.TASK) {
            viewModelScope.launch {
                val resultingAnswer =
                    if (answers.isEmpty()) {
                        answers.add("")
                        answers.toList()
                    } else if (TaskType.valueOf(currentTask.value!!.type) == TaskType.MULTIPLE_CHOICE) {
                        answers.toList()
                    } else {
                        listOf(answers.last())
                    }
                try {
                    val stageResult = repository.postAnswer(
                        eventId,
                        currentTask.value?.blockId!!,
                        currentTask.value?.id!!,
                        resultingAnswer
                    )
                    points = stageResult.points
                    if (stageResult.points != null) {
                        bluetoothCallBack(stageResult.points)
                    }
                    isAnswerCorrect = stageResult.isCorrect
                    correctAnswer.addAll(stageResult.rightAnswer ?: listOf())
                    if (stageResult.rightAnswer == null && stageResult.points == null) {
                        currentStageType.value = StageType.NONE
                    } else {
                        showResults.value = true
                    }
                } catch (_: ConnectException) {
                    onNoInternet()
                } catch (_: NotAuthorisedException) {
                    unauthorised.value = true
                } catch (e: Exception) {
                    Log.e("send_stage_answer", e.message ?: e.toString())
                }
            }
        }
    }

    private fun sendStartTime(eventId: String, onNoInternet: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.postTaskStartTime(
                    eventId,
                    currentTask.value?.blockId ?: "",
                    currentTask.value?.id ?: "",
                    taskStartTime
                )
            } catch (_: ConnectException) {
                onNoInternet()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("EventStageTime", e.message ?: "", e)
            }
        }
    }

    override fun chooseTask(eventId: String, taskId: String, onNoInternet: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.postTaskChoice(eventId, currentBlock.value?.id ?: "", taskId)
                currentStageType.value = StageType.NONE
            } catch (_: IllegalAccessException) {

            } catch (_: ConnectException) {
                onNoInternet()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("send_stage_answer", e.message ?: e.toString())
            }
        }
    }

    override fun onDownloadFile(fileName: String, fileUri: String) {
        fileStatusFlows.put(fileUri, taskDownloader.download(fileUri, fileName))
    }

    override fun onOpenFile(fileUri: String) {
        taskDownloader.openFile(fileUri)
    }
}