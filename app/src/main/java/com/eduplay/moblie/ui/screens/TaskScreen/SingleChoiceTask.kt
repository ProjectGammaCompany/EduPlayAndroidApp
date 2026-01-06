package com.eduplay.moblie.ui.screens.TaskScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.models.AnswerOption
import kotlin.math.max

@Composable
fun SingleChoiceTask(maxHeight: Dp) {
    val options = listOf<AnswerOption>(
        AnswerOption("0", "as1", false),
        AnswerOption(
            "1",
            "as2 qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq",
            false
        ),
        AnswerOption("2", "as3", false),
        AnswerOption("3", "as4", false),
        AnswerOption("4", "as4", false),
        AnswerOption("5", "as4", false),
        AnswerOption("6", "as4", false),
        AnswerOption("7", "as4", true),
    )
    var selectedBtn by remember { mutableStateOf("") }
    Box(modifier = Modifier
        .fillMaxSize()
        //.heightIn(50.dp, max(50, maxHeight.value.toInt()/3).dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            options.forEach { option ->
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 2.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(7.dp))
                        .background(color = colorScheme.primaryContainer)
                ) {
                    RadioButton(
                        selected = selectedBtn == option.id,
                        onClick = {
                            selectedBtn = option.id
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
                            .padding(end=10.dp, top=5.dp, bottom=5.dp)
                    )
                }
            }
        }
    }
}