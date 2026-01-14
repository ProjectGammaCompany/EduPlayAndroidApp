package com.eduplay.moblie.ui.screens.TaskScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.viewmodel.EventStageViewModelInterface

@Composable
fun TextTask(viewModel: EventStageViewModelInterface) {
    var answer by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(
            value = answer,
            onValueChange = { newText: String ->
                answer = newText
                viewModel.answers.clear()
                viewModel.answers.add(newText)
            },
            enabled = !viewModel.disableTask.value,
            placeholder = {
                Text(
                    text = stringResource(R.string.your_answer),
                    style = typography.bodyMedium
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = colorScheme.onBackground,
                unfocusedTextColor = colorScheme.onBackground,
                focusedContainerColor = colorScheme.background,
                unfocusedContainerColor = colorScheme.background,
                disabledTextColor = colorScheme.onPrimaryContainer,
                disabledContainerColor = colorScheme.primaryContainer,
                focusedPlaceholderColor = colorScheme.primary,
                unfocusedPlaceholderColor = colorScheme.primary,
                disabledPlaceholderColor = colorScheme.primary,
            ),
            textStyle = typography.bodyMedium
                .copy(color = colorScheme.onSecondaryContainer),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(20.dp)
                .fillMaxWidth(0.9f)
                .fillMaxHeight()
                .clip(shape = RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
        )
    }
}

