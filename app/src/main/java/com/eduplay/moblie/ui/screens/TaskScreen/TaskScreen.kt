package com.eduplay.moblie.ui.screens.TaskScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.webrepository.responseTypes.Task
import com.eduplay.moblie.ui.viewmodel.EventStageViewModelInterface
import com.eduplay.moblie.useCases.FileDownloadStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.abs
import kotlin.math.max

//TODO("optional scroll bar для column")

@Composable
fun TaskScreen(
    innerPaddingValues: PaddingValues,
    eventId: String,
    viewModel: EventStageViewModelInterface,
    onGoBack: () -> Unit,
    onNoInternet: () -> Unit
) {
    val taskType = viewModel.currentTask.value!!.type
    var isSubmitBtnShown by remember { mutableStateOf(true) }
    val hideSubmitBtn = { isSubmitBtnShown = false }
    val showSubmitBtn = { isSubmitBtnShown = true }
    var showQr by remember { mutableStateOf(false) }
    val onScanQr = { showQr = true }
    val onSubmit = {
        viewModel.sendAnswer(eventId, onNoInternet)
    }
    if (taskType == TaskType.QR.optionNumber && showQr) {

        QRCameraPreview(
            innerPaddingValues,
            { answer ->
                showQr = false
                viewModel.answers.add(answer)
                viewModel.sendAnswer(eventId, onNoInternet)
            },
            { showQr = false },
        )
    } else {
        BoxWithConstraints {
            val height = maxHeight
            Column(
                modifier = Modifier
                    .padding(
                        top = 0.dp,
                        bottom = innerPaddingValues.calculateBottomPadding(),
                        start = innerPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPaddingValues.calculateEndPadding(LayoutDirection.Ltr)
                    )
                    .fillMaxSize()
                    .background(color = colorScheme.background)
            ) {
                // top bar
                TaskTopBar(onGoBack)


                // header
                TaskHeader(
                    height,
                    TaskType.valueOf(taskType),
                    viewModel.currentTask.value!!.name,
                    viewModel.currentTask.value!!.description ?: "",
                    viewModel.currentTask.value!!.time,
                    viewModel.taskStartTime,
                    viewModel.currentTask.value!!.files,
                    onSubmit,
                    onDownload = { fileName, fileUri ->
                        viewModel.onDownloadFile(
                            fileName,
                            fileUri
                        )
                    },
                    onOpen = { fileUri -> viewModel.onOpenFile(fileUri) },
                    downloadStatus = viewModel.fileStatusFlows
                )

                //task

                Box(modifier = Modifier.weight(2f)) {
                    when (TaskType.valueOf(taskType)) {
                        TaskType.INFO -> {}
                        TaskType.SINGLE_CHOICE -> SingleChoiceTask(viewModel)
                        TaskType.MULTIPLE_CHOICE -> MultipleChoiceTask(viewModel)
                        TaskType.TEXT -> TextTask(viewModel)
                        TaskType.QR -> QRTask(hideSubmitBtn, showSubmitBtn, onScanQr, viewModel)
                    }
                }

                //next btn
                if (isSubmitBtnShown) {
                    SubmitBtn(
                        TaskType.valueOf(taskType),
                        { viewModel.sendAnswer(eventId, onNoInternet) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTopBar(onGoBack: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = { onGoBack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun TaskHeader(
    maxHeight: Dp,
    taskType: TaskType,
    title: String,
    description: String,
    time: Int?,
    startTime: LocalDateTime,
    files: List<Task.TaskFile>,
    invokeEndOnTime: () -> Unit,
    onDownload: (String, String) -> Unit,
    onOpen: (String) -> Unit,
    downloadStatus: SnapshotStateMap<String, Flow<FileDownloadStatus>>
) {
    val scope = rememberCoroutineScope()
    var showTimer by remember { mutableStateOf(time != null && time != 0) }

    Column(
        modifier =
            if (taskType == TaskType.INFO) Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
            else Modifier.fillMaxWidth()
    ) {
        // Timer
        if (showTimer) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.9f)
            ) {
                var currentProgress by remember {
                    mutableFloatStateOf(
                        abs(
                            Duration.between(LocalDateTime.now(), startTime).toSeconds()
                        ).toFloat() / time!!
                    )
                }
                LaunchedEffect(Any()) {
                    scope.launch {
                        while (currentProgress < 1f) {
                            currentProgress = abs(
                                Duration.between(LocalDateTime.now(), startTime).toSeconds()
                            ).toFloat() / time!!
                            delay(500L)
                        }
                    }
                }
                var timeLeft by remember {
                    mutableLongStateOf(
                        time!! - abs(
                            Duration.between(
                                LocalDateTime.now(),
                                startTime
                            ).toSeconds()
                        )
                    )
                }
                LaunchedEffect(time) {
                    scope.launch {
                        while (timeLeft > 0) {
                            timeLeft -= 1
                            delay(1000L)
                        }
                        invokeEndOnTime()
                    }
                }

                LinearProgressIndicator(
                    progress = { currentProgress },
                    color = colorScheme.secondary,
                    trackColor = colorScheme.secondaryContainer,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(35.dp)
                        .padding(vertical = 10.dp)

                )
                Text(
                    text = (timeLeft / 60).toString() + ":" + (timeLeft % 60).toString(),
                    style = typography.titleLarge
                        .copy(color = colorScheme.onSurface),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // task title an description
        Box(
            modifier = if (taskType == TaskType.INFO) Modifier.fillMaxWidth()
            else Modifier
                .heightIn(50.dp, max(50, maxHeight.value.toInt() / 3).dp)
                .fillMaxWidth()
        ) {
            Column(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 15.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // title
                Text(
                    text = title,
                    style = typography.headlineSmall.copy(color = colorScheme.onBackground)
                        .copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            //color = colorScheme.onSurface
                        ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                //description
                Text(
                    text = description,
                    style = typography.bodyMedium.copy(color = colorScheme.onBackground)
                        .copy(color = colorScheme.onSurface),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 15.dp)
                )
                FileView(
                    files,
                    onDownload,
                    onOpen,
                    downloadStatus
                )
            }
        }
    }
}

@Composable
private fun SubmitBtn(taskType: TaskType, onSubmit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
    ) {
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .padding(bottom = 15.dp, top = 5.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth(0.9f)
        ) {
            Text(
                text = if (taskType == TaskType.INFO) stringResource(R.string.proceed)
                else stringResource(R.string.answer),
                style = typography.headlineSmall,

                )
        }
    }
}

@Composable
fun FileView(
    files: List<Task.TaskFile>,
    onDownload: (String, String) -> Unit,
    onOpen: (String) -> Unit,
    downloadStatus: SnapshotStateMap<String, Flow<FileDownloadStatus>>
) {

    Column {
        files.forEach { file ->
            val fileStatus = (downloadStatus[file.url]
                ?: flowOf()).collectAsState(
                FileDownloadStatus.NOT_STARTED
            )
            TextButton(
                onClick = {
                    Log.d("FILE_STATUS", fileStatus.value.toString())
                    if (fileStatus.value == FileDownloadStatus.NOT_STARTED ||
                        fileStatus.value == FileDownloadStatus.FAILED
                    ) {
                        onDownload(
                            file.name,
                            file.url
                        )
                    } else if (fileStatus.value == FileDownloadStatus.SUCCESS) {
                        onOpen(file.name)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.background,
                    contentColor = colorScheme.onBackground
                ),
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .fillMaxWidth()
                    .border(
                        width = 2.dp,
                        color = colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                val infiniteTransition = rememberInfiniteTransition()
                val angle by infiniteTransition.animateFloat(
                    initialValue = 0F,
                    targetValue = 360F,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2000, easing = LinearEasing)
                    )
                )
                // file icon
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.files),
                    contentDescription = file.name,
                    modifier = Modifier.padding(end = 10.dp)
                )
                //file name
                Text(
                    text = file.name,
                    textAlign = TextAlign.Start,
                    style = typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                // download status icon
                when (fileStatus.value) {
                    FileDownloadStatus.NOT_STARTED, FileDownloadStatus.FAILED -> {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.download),
                            contentDescription = file.name,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                    }

                    FileDownloadStatus.SUCCESS -> {}
                    FileDownloadStatus.LOADING, FileDownloadStatus.PAUSED -> {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.progress),
                            contentDescription = file.name,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .rotate(angle)
                        )
                    }
                }

            }
        }
    }
}

//@Preview
//@Composable
//fun TaskPreview() {
//    val flowmap = remember { mutableStateMapOf<String, Flow<FileDownloadStatus>>() }
//    EduPlayTheme(false) {
//        TaskScreen(
//            PaddingValues(),
//            "1",
//            object : EventStageViewModelInterface {
//                override val fileStatusFlows: SnapshotStateMap<String, Flow<FileDownloadStatus>>
//                    get() = flowmap
//                override val currentStageType: MutableState<StageType> =
//                    remember { mutableStateOf(StageType.TASK) }
//                override var currentTask: MutableState<Task?> = remember {
//                    mutableStateOf<Task?>(
//                        Task(
//                            "1",
//                            "1",
//                            "Задание 4 ",
//                            "Отсканировать код",
//                            TaskType.QR.optionNumber,
//                            listOf(
//                                AnswerOption("1", "ответ 1", false),
//                                AnswerOption("0", "ответ 2", false),
//                                AnswerOption("2", "ответ 3", false),
//                                AnswerOption("3", "ответ 4", false),
//                                AnswerOption("4", "ответ 5", false),
//                                AnswerOption("5", "ответ 6", false)
//                            ),
//                            listOf(
//                                "455d8c87-c253-42b7-970d-e3965ac95424.docx",
//                                "455d8c87-c253-42b7-970d-e3965ac95424.docx"
//                            ),
//                            30,
//                            LocalDateTime.now().toString()
//                        )
//                    )
//                }
//                    set(value) {}
//                override var currentBlock: MutableState<Block?>
//                    get() = TODO("Not yet implemented")
//                    set(value) {}
//                override var taskStartTime: LocalDateTime = LocalDateTime.now()
//
//                override val answers: SnapshotStateList<String> =
//                    remember { mutableStateListOf<String>() }
//                override val disableTask: MutableState<Boolean> = remember { mutableStateOf(false) }
//
//                override val showResults: MutableState<Boolean> = remember { mutableStateOf(false) }
//
//                override val correctAnswer: MutableList<String>
//                    get() = TODO("Not yet implemented")
//                override var points: Int?
//                    get() = TODO("Not yet implemented")
//                    set(value) {}
//                override var isAnswerCorrect: TaskAnswerStatus?
//                    get() = TODO("Not yet implemented")
//                    set(value) {}
//
//                override fun chooseTask(
//                    eventId: String,
//                    taskId: String,
//                    onNoInternet: () -> Unit
//                ) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun sendAnswer(eventId: String, onNoInternet: () -> Unit) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun getNextStage(eventId: String, onNoInternet: () -> Unit) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onDownloadFile(fileName: String, fileUri: String) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onOpenFile(fileUri: String) {
//                    TODO("Not yet implemented")
//                }
//            },
//            {},
//            {}
//        )
//    }
//}
