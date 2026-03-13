package com.eduplay.moblie.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduplay.moblie.R
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.screens.TaskScreen.TaskScreen
import com.eduplay.moblie.ui.viewmodel.BluetoothViewModel
import com.eduplay.moblie.ui.viewmodel.EventStageViewmodel
import kotlin.collections.toList

@Composable
fun EventStageScreen(
    eventId: String,
    innerPadding: PaddingValues,
    navController: NavController,
    viewModel: EventStageViewmodel = hiltViewModel(),
    bluetoothViewModel: BluetoothViewModel,
    isCompetitionMode: State<Boolean>
) {
    val context = LocalContext.current
    if (isCompetitionMode.value) {
        viewModel.bluetoothCallBack = {points -> bluetoothViewModel.sendResultsToSockets(points, context)}
    }
    var showGoBackDialog by remember { mutableStateOf(false) }
    val onGoBack = {
        showGoBackDialog = true
    }
    val goBack = {
        navController.popBackStack()
    }
    var noInternet by remember { mutableStateOf(false) }
    var cantShowData by remember { mutableStateOf(false) }
    BackHandler {
        onGoBack()
    }
    when (viewModel.currentStageType.value) {
        StageType.NONE -> {
            viewModel.getNextStage(eventId, {noInternet = true; cantShowData = true})
        }

        StageType.TASK -> {
            TaskScreen(
                innerPadding,
                eventId,
                onGoBack = onGoBack,
                viewModel = viewModel,
                onNoInternet = { noInternet = true },
            )
        }

        StageType.BLOCK -> {
            ParallelBlockScreen(
                block = viewModel.currentBlock.value!!,
                onChooseTask = { taskId: String ->
                    viewModel.chooseTask(eventId, taskId, {noInternet=true})
                },
                onGoBack = onGoBack,
                innerPaddingValues = innerPadding
            )
        }

        StageType.END -> {
            navController.navigate("event_result/$eventId")
        }
    }
    if (viewModel.showResults.value) {
        ResultDialog(
            isCorrect = viewModel.isAnswerCorrect,
            answers = viewModel.answers,
            points = viewModel.points,
            proceedToNextTask = {
                viewModel.currentStageType.value = StageType.NONE
            },
            playersResults = bluetoothViewModel.devicesScore

        )
    }
    if (noInternet) {
        NoInternetConnectionToast()
    }
    if (viewModel.unauthorised.value) {
        AuthScreenNavigator(navController)
    }
    if (cantShowData) {
        navController.popBackStack()
    }
    if (showGoBackDialog) {
        ExitEventDialog(
            {
                showGoBackDialog = false
            },
            goBack
        )
    }
}

@Composable
private fun ExitEventDialog(hideDialog: () -> Unit, goBack: () -> Boolean) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.exit_event))
        },
        text = {
            Text(text = stringResource(R.string.exit_event_warning))
        },
        onDismissRequest = hideDialog,
        confirmButton = {
            TextButton(
                onClick = { goBack() }
            ) {
                Text(stringResource(R.string.exit))
            }
        },
        dismissButton = {
            TextButton(
                onClick = hideDialog
            ) {
                Text(stringResource(R.string.stay))
            }
        }
    )
}

@Composable
private fun ResultDialog(
    isCorrect: TaskAnswerStatus?,
    answers: List<String>?,
    points: Int?,
    proceedToNextTask: () -> Unit,
    playersResults: SnapshotStateMap<String, Int>
) {
    Dialog(
        onDismissRequest = proceedToNextTask
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = when (isCorrect) {
                        null -> stringResource(R.string.done)
                        TaskAnswerStatus.CORRECT -> stringResource(R.string.correct)
                        TaskAnswerStatus.INCORRECT -> stringResource(R.string.incorrect)
                        TaskAnswerStatus.PARTIALLY -> stringResource(R.string.partialy_correct)
                    },
                    style = typography.headlineSmall.copy(color = colorScheme.onBackground)
                )
                if (playersResults.isEmpty()) {
                    if (isCorrect != null) {
                        if (isCorrect == TaskAnswerStatus.CORRECT) {
                            Image(
                                painter = painterResource(id = R.drawable.correct_answer),
                                contentDescription = stringResource(R.string.correct)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.incorrect_answer),
                                contentDescription = stringResource(R.string.incorrect),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                } else {
                    val points = playersResults.toList().sortedByDescending { it.second }
                    LazyColumn(Modifier.height(100.dp)) {
                        items(points) {
                            Row(Modifier.padding(1.dp)) {
                                Text(
                                    it.first,
                                    modifier = Modifier.weight(1f),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Text(": "+it.second)
                            }
                        }
                    }
                }
                if (points != null) {
                    Row {
                        Text(
                            text = stringResource(R.string.points) + ":",
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium).copy(color = colorScheme.onBackground)
                        )
                        Text(
                            text = points.toString(),
                            style = typography.bodyMedium.copy(color = colorScheme.onBackground)
                        )
                    }
                }
                if (answers != null) {
                    Text(
                        text = stringResource(R.string.сorrect_answers) + ":",
                        style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium).copy(color = colorScheme.onBackground)
                    )
                    Column(
                        modifier = Modifier
                            .heightIn(max = 100.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        answers.forEach { answer ->
                            Text(
                                text = answer,
                                style = typography.bodyMedium.copy(color = colorScheme.onBackground)
                            )
                        }
                    }


                }

                TextButton(
                    onClick = {
                        proceedToNextTask()
                    },
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        stringResource(R.string.proceed),
                        style = typography.bodyLarge
                    )
                }
            }
        }
    }
}

