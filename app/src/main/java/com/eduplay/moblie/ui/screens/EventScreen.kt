package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.theme.Typography
import com.eduplay.moblie.ui.viewmodel.EventScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    innerPaddingValues: PaddingValues,
    eventId: String,
    navController: NavController,
    viewModel: EventScreenViewModel = hiltViewModel()
) {
    var dataFetched by remember { mutableStateOf(false) }
    if (!dataFetched) {
        viewModel.fetchData(eventId) { dataFetched = true }
    }
    val startEvent = {} //TODO("start event btn")
    var showEditDialog by remember { mutableStateOf(false) }
    val onEditEvent = {
        showEditDialog = true
    }
    val onCloseEditEvent = {
        showEditDialog = false
    }


    Column(
        modifier = Modifier
            .padding(
                top = 0.dp,
                bottom = innerPaddingValues.calculateBottomPadding(),
                start = innerPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPaddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
            .fillMaxSize()
    ) {
        TopAppBarEventScreen(
            viewModel.eventCreatorMode.value,
            viewModel.isEventFavourite.value,
            onEditEvent,
            navController
        )

        EventScreenHeader(
            viewModel.eventName.value,
            viewModel.author.value,
            viewModel.eventCreatorMode.value,
            viewModel.isCompleted.value,
            viewModel.cover.value
        )

        if (viewModel.eventCreatorMode.value) {
            EventCreatorBody(
                viewModel.tags,
                viewModel.info,
                viewModel.description.value,
                viewModel.privateEvent.value
            )
        } else {
            GeneralUserBody(
                viewModel.tags,
                viewModel.info,
                viewModel.description.value,
                viewModel.isOpen.value,
                viewModel.isContinuing.value,
                startEvent
            )
        }
    }

    if (showEditDialog) {
        EditDialog(onCloseEditEvent)
    }
}


@Composable
private fun EditDialog(onClose: () -> Unit) {
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.edit_event))
        },
        text = {
            Text(text = stringResource(R.string.to_edit_event))
        },
        onDismissRequest = {
            onClose()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onClose()
                }
            ) {
                Text(stringResource(R.string.proceed_to_website))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onClose()
                }
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarEventScreen(
    eventCreatorMode: Boolean,
    isFavourite: Boolean,
    onEditEvent: () -> Unit,
    navController: NavController
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primaryContainer,
            titleContentColor = colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
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
            } else {
                IconButton(onClick = { onEditEvent() }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.edit),
                        contentDescription = stringResource(R.string.edit_event)
                    )
                }
            }
        },
        title = {}
    )

}

@Composable
private fun EventScreenHeader(
    eventName: String,
    author: String,
    eventCreatorMode: Boolean,
    isCompleted: Boolean,
    cover: String?
) {
    Row(Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(cover)
                //.httpHeaders(headers = headers) //TODO("pass headers")
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = eventName,
            placeholder = painterResource(R.drawable.eduplaylogo),
            error = painterResource(id = R.drawable.ic_launcher_background),
            modifier = Modifier
                .width(130.dp)
                .weight(0.35f)
        )
        Text(
            eventName,
            style = typography.headlineMedium,
            maxLines = 3,
            modifier = Modifier
                .weight(0.6f)
                .align(Alignment.Bottom)
                .padding(horizontal = 10.dp)
        )
    }
    Row(modifier = Modifier.padding(horizontal = 10.dp)) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .weight(0.35f)
        ) {
            if (!eventCreatorMode && isCompleted) {
                AssistChip(
                    onClick = {}, //так и должно быть при нажатии ничего не происходит
                    label = {
                        Text(
                            stringResource(R.string.completed),
                            style = typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = stringResource(R.string.completed),
                            Modifier.size(AssistChipDefaults.IconSize)
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.Center)


                )
            }
        }
        if (!eventCreatorMode) {
            Text(
                author,
                style = typography.labelLarge,
                maxLines = 1,
                color = colorScheme.primary,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 10.dp)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun GeneralInfo(
    tags: List<String>,
    info: List<Pair<Int, String?>>,
    description: String
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        FlowRow(modifier = Modifier.fillMaxWidth()) {
            tags.forEach { tagName ->
                EventTag(tagName)
            }
        }
        Column(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.general_info),
                style = typography.titleLarge,
                modifier = Modifier.padding(bottom = 5.dp, top = 10.dp)
            )
            info.forEach { pair ->
                if (pair.second != null) {
                    Row {
                        Text(
                            text = stringResource(pair.first),
                            style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(
                            text = pair.second ?: "",
                            style = typography.bodyMedium
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.description),
                style = typography.titleLarge,
                modifier = Modifier.padding(bottom = 5.dp, top = 10.dp)
            )
            Text(
                text = description,
                style = typography.bodyMedium
            )
        }
    }

}

@Composable
private fun GeneralUserBody(
    tags: List<String>,
    info: List<Pair<Int, String?>>,
    description: String,
    isOpen: Boolean,
    isContinuing: Boolean,
    startEvent: () -> Unit
) {
    Column(verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.fillMaxHeight(if (isOpen) 0.85f else 1f)) {
            GeneralInfo(tags, info, description)
        }

        if (isOpen) {
            Button(
                onClick = startEvent,
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .weight(0.15f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    if (!isContinuing) {
                        stringResource(R.string.start_event)
                    } else {
                        stringResource(R.string.continue_event)
                    },
                    style = Typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun EventTag(tagName: String) {
    Text(
        text = tagName,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = colorScheme.onPrimaryContainer,
        style = typography.labelLarge,
        modifier = Modifier
            .padding(horizontal = 5.dp, vertical = 3.dp)
            .wrapContentWidth()
            .background(colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp))
            .border(1.dp, colorScheme.tertiary, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
private fun EventCreatorBody(
    tags: List<String>,
    info: List<Pair<Int, String?>>,
    description: String,
    privateEvent: Boolean
) {
    val tabs = remember<List<Int>> {
        listOf<Int>(
            R.string.general_info,
            R.string.statistics,
        )
    }
    var selectedTabIdx by remember { mutableIntStateOf(0) }
    Column {
        SecondaryTabRow(selectedTabIndex = selectedTabIdx) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIdx == index,
                    onClick = { selectedTabIdx = index },
                    text = {
                        Text(
                            text = stringResource(title),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
    val infoP = info.toMutableList()
    infoP.add(
        Pair(
            R.string.private_event_flag,
            if (privateEvent) stringResource(R.string.private_event) else stringResource(R.string.public_event)
        ),
    )
    when (selectedTabIdx) {
        0 -> GeneralInfo(tags, infoP, description)
        1 -> StatisticsInfo()
        else -> Box {}
    }

}

@Composable
private fun StatisticsInfo() {
    Text("Coming soon")
    //TODO("статистики на экране статистик")
}
