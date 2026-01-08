package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
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
}