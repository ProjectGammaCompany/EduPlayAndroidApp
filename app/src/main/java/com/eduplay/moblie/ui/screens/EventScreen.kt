package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.theme.Typography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(innerPaddingValues: PaddingValues) {
    val eventCreatorMode = false
    val isEventFavourite = false
    val isCompleted = true
    val eventName = "Название события"
    val author = "Автор"
    Column(
        modifier = Modifier
            .padding(
                top = 0.dp, //innerPaddingValues.calculateTopPadding(),
                bottom = innerPaddingValues.calculateBottomPadding(),
                start = innerPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPaddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
            .fillMaxSize()
    ) {
        TopAppBarEventScreen(eventCreatorMode, isEventFavourite)

        EventScreenHeader(eventName, author, eventCreatorMode, isCompleted)



        if (eventCreatorMode) {
            EventCreatorView()
        } else {
            GeneralUserView()
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarEventScreen(eventCreatorMode: Boolean, isFavourite: Boolean) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(onClick = { TODO("навигация на предыдущий экран") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.app_menu)
                )
            }
        },
        actions = {
            if (!eventCreatorMode) {
                IconButton(onClick = { TODO("реализовать кнопку пожаловаться") }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.hand),
                        contentDescription = stringResource(R.string.report_event)
                    )
                }
                IconButton(onClick = { TODO("реализовать кнопку скачать событие") }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.download_24dp_1f1f1f_fill0_wght200_grad0_opsz24),
                        contentDescription = stringResource(R.string.download_event)
                    )
                }
                IconButton(onClick = { TODO("реализовать кнопку добавить в избранное") }) {
                    if (isFavourite) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.star_filled),
                            contentDescription = stringResource(R.string.add_to_favourite)
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.star),
                            contentDescription = stringResource(R.string.add_to_favourite)
                        )
                    }
                }
            }
        },
        title = {}
    )

}

@Composable
private fun EventScreenHeader(eventName:String, author:String, eventCreatorMode: Boolean, isCompleted:Boolean ) {
    Row(Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("")
                //.httpHeaders(headers = headers) //TODO("pass headers")
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = eventName,
            placeholder = painterResource(R.drawable.eduplaylogo),
            error = painterResource(id = R.drawable.ic_launcher_background),
            modifier = Modifier.weight(0.3f)
        )
        Text(
            eventName,
            style = typography.headlineMedium,
            maxLines = 3,
            modifier = Modifier
                .weight(0.6f)
                .align(Alignment.Bottom)
                .padding(horizontal=10.dp)
        )
    }
    Row( modifier = Modifier.padding(horizontal = 10.dp)) {
        Box(modifier = Modifier.weight(0.3f)) {
            if (!eventCreatorMode && isCompleted) {
                AssistChip(
                    onClick = {},
                    label = { Text(stringResource(R.string.completed)) },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.completed),
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    }
                )
            }
        }
        if (!eventCreatorMode) {
            Text(author,
                style = typography.labelLarge,
                maxLines = 1,
                color = colorScheme.primary,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal=10.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}
@Composable
fun GeneralUserView() {

}

@Composable
fun EventCreatorView() {

}

@Composable
@Preview
fun PreviewEventScreen() {
    EventScreen(PaddingValues(0.dp))
}