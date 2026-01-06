package com.eduplay.moblie.ui.screens.TaskScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
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

@Composable
fun TextTask() {
    var answer by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(
            value = answer,
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
                disabledTextColor = colorScheme.onSecondary,
                disabledContainerColor = colorScheme.secondary,
                focusedPlaceholderColor = colorScheme.tertiary,
                unfocusedPlaceholderColor = colorScheme.tertiary,
                errorPlaceholderColor = colorScheme.tertiary,
                disabledPlaceholderColor = colorScheme.tertiary,
            ),
            textStyle = typography.bodyMedium
                .copy(color=colorScheme.onSecondaryContainer),
            onValueChange = { answer = it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(20.dp)
                .fillMaxWidth(0.9f)
                .fillMaxHeight()
                .clip(shape = RoundedCornerShape(8.dp))
                .border(
                    width = 1.dp,
                    color = colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
                .background(color = colorScheme.secondaryContainer)
        )
    }
}

