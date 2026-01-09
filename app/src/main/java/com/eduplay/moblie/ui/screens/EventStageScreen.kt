package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.eduplay.moblie.R
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.ui.screens.TaskScreen.TaskScreen
import com.eduplay.moblie.ui.viewmodel.EventStageViewmodel

@Composable
fun EventStageScreen(
    eventId: String,
    innerPadding: PaddingValues,
    viewModel: EventStageViewmodel = hiltViewModel()
) {
    when (viewModel.currentStageType.value) {
        StageType.NONE -> {
            viewModel.getNextStage(eventId)
        }

        StageType.TASK -> {
            TaskScreen(
                innerPadding,
                eventId,
                viewModel
            )
        }

        StageType.BLOCK -> {
            // TODO("block screen")
//            BlockScreen(
//                block=viewModel.currentBlock,
//                onFetchTask={
//                    viewModel.getBlockTask(eventId)
//                },
//                onSubmitTask = {
//                        answers: List<String> ->
//                    viewModel.sendAnswer(eventId, answers),
//                    viewModel.goBackToBlock()
//                }
//            )
        }

        StageType.END -> {
            //navigate to end screen
        }
    }
    if (viewModel.showResults.value) {
        ResultDialog(
            isCorrect = viewModel.isAnswerCorrect,
            answers = viewModel.answers,
            points = viewModel.points,
            proceedToNextTask = {
                viewModel.currentStageType.value = StageType.NONE
            }

        )
    }
}

@Composable
private fun ResultDialog(
    isCorrect: Boolean?,
    answers: List<String>?,
    points: Int?,
    proceedToNextTask:()->Unit
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
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = when (isCorrect) {
                        true -> stringResource(R.string.correct)
                        false -> stringResource(R.string.incorrect)
                        null -> stringResource(R.string.done)
                    },
                    style = typography.headlineSmall
                )
                if (isCorrect!= null) {
                    if (isCorrect) {
                        Image(
                            painter = painterResource(id = R.drawable.correct_answer),
                            contentDescription = stringResource(R.string.correct)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.incorrect_answer),
                            contentDescription = stringResource(R.string.correct),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
                if (points != null) {
                    Row {
                        Text(
                            text = stringResource(R.string.points)+":",
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = points.toString(),
                            style = typography.bodyMedium
                        )
                    }
                }
                if (answers != null) {
                    Text(
                        text = stringResource(R.string.сorrect_answers)+":",
                        style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Column (
                        modifier = Modifier
                            .heightIn(max = 100.dp)
                            .verticalScroll(rememberScrollState())
                    ){
                        answers.forEach { answer ->
                            Text(
                                text = answer,
                                style = typography.bodyMedium
                            )
                        }
                    }


                }

                TextButton(
                    onClick = {
                        proceedToNextTask()
                    },
                    modifier = Modifier.padding(top=10.dp)
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