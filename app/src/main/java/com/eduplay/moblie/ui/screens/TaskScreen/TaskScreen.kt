package com.eduplay.moblie.ui.screens.TaskScreen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.models.TaskType
import kotlin.math.max

//TODO("optional scroll bar для column")

@Composable
fun TaskScreen(innerPaddingValues: PaddingValues) {
    val taskType = TaskType.INFO
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
            TaskTopBar()


            // header
            TaskHeader(height, taskType)

            //task

            Box(modifier = Modifier.weight(2f)) {
                when (taskType) {
                    TaskType.INFO -> {}
                    TaskType.SINGLE_CHOICE -> SingleChoiceTask()
                    TaskType.MULTIPLE_CHOICE -> MultipleChoiceTask()
                    TaskType.TEXT -> TextTask()
                    TaskType.QR -> Box {} //TODO("qr task text type")
                }
            }

            //next btn
            SubmitBtn(taskType)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTopBar() {
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

@Composable
private fun TaskHeader(maxHeight: Dp, taskType: TaskType) {
    val title = "very Loooooooooooong and hard question"
    val description =
        """
          Description aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
            aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
            aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
            aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
        """.trimIndent()

    var showTimer by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableFloatStateOf(0.5f) }
    val timeLeft by remember { mutableIntStateOf(30) }


    Column(modifier =
       if (taskType == TaskType.INFO) Modifier.fillMaxWidth().fillMaxHeight(0.9f)
        else Modifier.fillMaxWidth()
    ) {
        // Timer
        if (showTimer) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.9f)
            ) {
                LinearProgressIndicator(
                    progress = { currentProgress },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(35.dp)
                        .padding(vertical = 10.dp)

                )
                Text(
                    text = "${timeLeft / 60}:${timeLeft % 60}",
                    style = typography.titleLarge,
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
                    style = typography.headlineSmall
                        .copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )

                //description
                Text(
                    text = description,
                    style = typography.bodyMedium,
                    //.copy(fontSize = 20.sp),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 15.dp)
                )
                FileView()
            }
        }
    }
}

@Composable
private fun SubmitBtn(taskType: TaskType) {
    Box(modifier = Modifier.fillMaxWidth().height(65.dp)) {
        Button(
            onClick = {},
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
fun FileView() {
    val uriHandler = LocalUriHandler.current
    val files = listOf("file 1")
        Column() {
            files.forEach {
                TextButton(
                    onClick = {
                        uriHandler
                            .openUri(uri = "https://developers.google.com/ml-kit/vision/barcode-scanning/android#try-it-out")
                    }
                ) {
                    Text(text = it)
                }
            }
        }
}

@Preview
@Composable
fun TaskPreview() {
    TaskScreen(PaddingValues())
}

