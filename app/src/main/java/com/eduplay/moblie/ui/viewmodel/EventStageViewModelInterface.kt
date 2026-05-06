package com.eduplay.moblie.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.repository.webrepository.responseTypes.Task
import com.eduplay.moblie.useCases.FileDownloadStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface EventStageViewModelInterface {
    val fileStatusFlows: SnapshotStateMap<String, Flow<FileDownloadStatus>>
    val currentStageType: MutableState<StageType>

    var currentTask: MutableState<Task?>

    var currentBlock: MutableState<Block?>

    var taskStartTime: LocalDateTime

    val answers: SnapshotStateList<String>
    val disableTask: MutableState<Boolean>
    val showResults: MutableState<Boolean>
    val correctAnswer: MutableList<String>
    var points: Int?
    var isAnswerCorrect: TaskAnswerStatus?
    val unauthorised: MutableState<Boolean>
    var bluetoothCallBack: (Int) -> Unit
    val noInternet: MutableState<Boolean>
    fun chooseTask(eventId: String, taskId: String)
    fun sendAnswer(eventId: String)
    fun onDownloadFile(fileName: String, fileUri: String)
    fun getNextStage(eventId: String, retry: Boolean = false)
    fun onOpenFile(fileUri: String, fileName: String, context: Context)
}