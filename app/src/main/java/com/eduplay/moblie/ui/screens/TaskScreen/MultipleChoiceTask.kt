package com.eduplay.moblie.ui.screens.TaskScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.ui.viewmodel.EventStageViewModelInterface

@Composable
fun MultipleChoiceTask(viewModel: EventStageViewModelInterface) {
    val checkedOptions: SnapshotStateMap<String, Boolean> = remember {
        viewModel.currentTask.value!!.options!!.map { Pair<String, Boolean>(it.id, false) }
            .toMutableStateMap<String, Boolean>()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            viewModel.currentTask.value!!.options!!.forEach { option ->
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 2.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(7.dp))
                        .background(color = colorScheme.primaryContainer)
                ) {
                    Checkbox(
                        checked = checkedOptions.get(option.id) ?: false,
                        enabled = !viewModel.disableTask.value,
                        onCheckedChange = {
                            checkedOptions.set(option.id, !(checkedOptions.get(option.id)!!))
                            viewModel.answers.clear()
                            checkedOptions.forEach { (key, value) ->
                                if (value) viewModel.answers.add(
                                    key
                                )
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = option.value,
                        style = typography.bodyMedium
                            .copy(color = colorScheme.onPrimaryContainer),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 10.dp, top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}