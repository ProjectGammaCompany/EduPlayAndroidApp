package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.ShortTask

@Composable
fun ParallelBlockScreen(
    block: Block,
    onChooseTask: (String) -> Unit,
    innerPaddingValues: PaddingValues
) {
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
        BlockTopBar()
        Text(
            text = block.name,
            style = typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            block.tasks.forEach { task ->
                TaskItem(
                    name = task.name,
                    time = task.time,
                    isEnabled = task.isCompleted,
                    onChooseTask = { onChooseTask(task.id) }
                )
            }
        }
    }
}

@Composable
private fun TaskItem(name: String, time: Int, isEnabled: Boolean, onChooseTask: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, shape = RoundedCornerShape(8.dp), color = colorScheme.primaryContainer)
            .background(if (isEnabled) colorScheme.surface else colorScheme.secondaryContainer)
            .clickable(isEnabled, onClick = onChooseTask)
            .padding(5.dp)
    ) {
        Text(
            text = name,
            style = typography.bodyMedium,
            textAlign = TextAlign.Start,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = if (isEnabled) colorScheme.onSurface else colorScheme.onSecondaryContainer,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = ( time / 60 ).toString() + ":" + ( time % 60 ).toString(),
            style = typography.bodySmall,
            textAlign = TextAlign.End,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = if (isEnabled) colorScheme.onSurface else colorScheme.onSecondaryContainer,
            modifier = Modifier.fillMaxWidth()
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BlockTopBar() {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = { TODO("task screen btn back") }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        }
    )
}


@Preview
@Composable
fun previewBlock() {
    ParallelBlockScreen(
        Block(
            "aaaa",
            "The Block",
            listOf(
                ShortTask(
                    id = "",
                    name = "task",
                    time = 145,
                    isCompleted = true
                ),
                ShortTask(
                    id = "",
                    name = "task",
                    time = 145,
                    isCompleted = false
                ),
                ShortTask(
                    id = "",
                    name = "task",
                    time = 145,
                    isCompleted = true
                )
            )
        ),
        { str -> str + "" },
        innerPaddingValues = PaddingValues(0.dp),
    )
}