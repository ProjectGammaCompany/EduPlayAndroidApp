package com.eduplay.moblie.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.elements.QuestListElement

@Composable
fun DownloadedEventsUpdateScreen(
    innerPaddingValues: PaddingValues,
    onGoBack: () -> Unit,
    events: SnapshotStateList<QuestShortInfo>,
    onNavigateToEvent: () -> Unit,
    networkError: State<Boolean>,
    onProceed: () -> Unit,
    onDownload: () -> Unit,
    onChooseEvent: (String),
    chosenEvents: SnapshotStateSet<String>
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
    ) {
        UpdateTopBar(onGoBack)
        if (networkError.value) {
            Text(stringResource(R.string.error_sync_answers))
        } else {
            Text(stringResource(R.string.update_events_before_ofline))
            LazyColumn {
                items(events) {
                    QuestListElement(
                        it,
                        onNavigateToEvent,
                        {},
                        false
                    )
                }
            }
        }
        Row {
            OutlinedButton() { } // продолжить без скачивания
            Button() { } // скачать все выбранное
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