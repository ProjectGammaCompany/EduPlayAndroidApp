package com.eduplay.moblie.ui.screens

import android.content.ComponentName
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduplay.moblie.R
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.elements.QuestListElement
import com.eduplay.moblie.ui.viewmodel.DownloadedEventsUpdateScreenViewModel

@Composable
fun DownloadedEventsUpdateScreen(
    innerPaddingValues: PaddingValues,
    onDownloadEvent: (String, String) -> ComponentName?,
    navController: NavController,
    viewModel: DownloadedEventsUpdateScreenViewModel = hiltViewModel()
) {
    if (viewModel.isOfflineOn.value) {
        navController.popBackStack()
    }
    val turnOnOffline: ()->Unit = {
        viewModel.turnOnOfflineMode()
        navController.popBackStack()
    }
    val onDownloadEvent = { eventId: String ->
        viewModel.updateEvent(eventId, onDownloadEvent)
    }

    if (viewModel.gotUpdates.value && viewModel.events.isEmpty() && !viewModel.noInternet.value) {
        turnOnOffline()
    }

    DownloadedEventsUpdateScreen(
        innerPaddingValues,
        { navController.popBackStack() },
        viewModel.events,
        viewModel.noInternet,
        turnOnOffline,
        onDownloadEvent,
        viewModel::deleteEventFromDevice,
        { eventId: String -> navController.navigate("event_screen/$eventId") }
    )
}

@Composable
fun DownloadedEventsUpdateScreen(
    innerPaddingValues: PaddingValues,
    onGoBack: () -> Unit,
    events: SnapshotStateList<QuestShortInfo>,
    networkError: State<Boolean>,
    onProceed: () -> Unit,
    onChooseEvent: (String) -> Unit,
    onDeleteEvent: (String) -> Unit,
    onNavigateToEvent: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                top = 0.dp, //innerPaddingValues.calculateTopPadding(),
                bottom = innerPaddingValues.calculateBottomPadding(),
                start = innerPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPaddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
            .fillMaxSize()
            .padding(10.dp)
    ) {
        UpdateTopBar(onGoBack)
        if (networkError.value) {
            Text(
                stringResource(R.string.error_sync_answers),
                modifier = Modifier.testTag("network_error_text")
            )
        } else {
            Text(
                stringResource(R.string.update_events_before_ofline),
                modifier = Modifier.testTag("need_update_text")
            )
            LazyColumn(Modifier.weight(1f).testTag("update_list")) {
                items(events) {
                    Row(Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = { onChooseEvent(it.id) },
                            modifier = Modifier.testTag("download_btn_${it.id}")
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.download),
                                stringResource(R.string.update_event)
                            )
                        }
                        IconButton(
                            onClick = { onDeleteEvent(it.id) },
                            modifier = Modifier.testTag("delete_btn_${it.id}")
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.delete),
                                stringResource(R.string.delete_event)
                            )
                        }
                        QuestListElement(
                            questShortInfo = it,
                            onClick = { onNavigateToEvent(it.id) },
                            onFavouriteToggle = {},
                            showFavoriteBtn = false,
                            modifier = Modifier.testTag("element_${it.id}")
                        )
                    }
                }
            }
        }
        TextButton(
            onClick = onProceed,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.proceed_to_offline),
                style = TextStyle(color = colorScheme.primary)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateTopBar(onGoBack: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = { onGoBack() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        }
    )
}