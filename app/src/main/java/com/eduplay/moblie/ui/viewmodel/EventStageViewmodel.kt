package com.eduplay.moblie.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.repository.webrepository.responseTypes.Task
import com.eduplay.moblie.useCases.DateConverter
import com.eduplay.moblie.useCases.FileDownloadStatus
import com.eduplay.moblie.useCases.LocalFileOpener
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.useCases.TaskDownloadUseCase
import com.eduplay.moblie.utils.CoroutineContextProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.time.LocalDateTime

@HiltViewModel
class EventStageViewmodel @Inject constructor(
    private val repository: EduRepository,
    private val taskDownloader: TaskDownloadUseCase,
    private val offlineModeManager: OfflineModeManager,
    private final val coroutineContext: CoroutineContextProvider = CoroutineContextProvider()
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
    override val unauthorised = mutableStateOf(false)

    override val fileStatusFlows = mutableStateMapOf<String, Flow<FileDownloadStatus>>()

    override var bluetoothCallBack: (Int) -> Unit = {}
    override val noInternet: MutableState<Boolean> = mutableStateOf(false)

    override fun getNextStage(eventId: String, retry: Boolean) {
        viewModelScope.launch(coroutineContext.Main) {
            try {
                val result = repository.getNextStage(eventId)
                clear()
                currentStageType.value = StageType.stringValueOf(result.type)
                currentTask.value = result.task
                currentBlock.value = result.block
                taskStartTime =
                    if (result.task?.timeStamp == null || result.task.timeStamp.isBlank()) {
                        LocalDateTime.now()
                    } else {
                        DateConverter.convertFromServerFormat(result.task.timeStamp)
                    }
                if (offlineModeManager.getAppMode()
                        .first() == OfflineModeManager.AppModes.OFFLINE
                ) {
                    val files = currentTask.value?.files ?: listOf()
                    fileStatusFlows.clear()
                    for (file in files) {
                        fileStatusFlows.put(
                            file.url,
                            flowOf(FileDownloadStatus.SUCCESS)
                        )
                    }
                }

            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                if (!retry) {
                    getNextStage(eventId, true)
                }
                Log.e("EventStage", e.message ?: "", e)
            }
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
        isAnswerCorrect = null
    }

    override fun sendAnswer(eventId: String) {
        disableTask.value = true
        if (currentStageType.value == StageType.TASK) {
            viewModelScope.launch(coroutineContext.Main) {
                val resultingAnswer =
                    if (answers.isEmpty()) {
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
                    if (isAnswerCorrect == null && stageResult.points == null && correctAnswer.isEmpty()) {
                        currentStageType.value = StageType.NONE
                    } else {
                        showResults.value = true
                    }
                } catch (_: ConnectException) {
                    noInternet.value = true
                } catch (_: NotAuthorisedException) {
                    unauthorised.value = true
                } catch (e: Exception) {
                    Log.e("send_stage_answer", e.message ?: e.toString())
                }
            }
        }
    }

    private fun sendStartTime(eventId: String) {
        viewModelScope.launch(coroutineContext.Main) {
            try {
                repository.postTaskStartTime(
                    eventId,
                    currentTask.value?.blockId ?: "",
                    currentTask.value?.id ?: "",
                    taskStartTime
                )
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("EventStageTime", e.message ?: "", e)
            }
        }
    }

    override fun chooseTask(eventId: String, taskId: String) {
        viewModelScope.launch(coroutineContext.Main) {
            try {
                repository.postTaskChoice(
                    eventId,
                    blockId = currentBlock.value?.id ?: "",
                    taskId = taskId
                )
                currentStageType.value = StageType.NONE
            } catch (e: IllegalAccessException) {
                Log.e("send_stage_answer", e.message ?: e.toString(), e)
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("send_stage_answer", e.message ?: e.toString())
            }
        }
    }

    override fun onDownloadFile(fileName: String, fileUri: String) {
        viewModelScope.launch(coroutineContext.Main) {
            when (offlineModeManager.getAppMode().first()) {
                OfflineModeManager.AppModes.ONLINE -> fileStatusFlows.put(
                    fileUri,
                    taskDownloader.download(fileUri, fileName)
                )

                OfflineModeManager.AppModes.OFFLINE -> {}
            }
        }
    }

    override fun onOpenFile(fileUri: String, context: Context) {
        viewModelScope.launch(coroutineContext.Main) {
            if (offlineModeManager.getAppMode().first() == OfflineModeManager.AppModes.ONLINE) {
                taskDownloader.openFile(fileUri)
            } else {
                LocalFileOpener.openFile(fileUri, context)
            }
        }
    }
}