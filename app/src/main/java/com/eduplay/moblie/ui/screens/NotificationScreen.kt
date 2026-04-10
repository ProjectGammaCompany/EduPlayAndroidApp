package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.eduplay.moblie.R
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.ui.elements.NotificationElement
import com.eduplay.moblie.ui.viewmodel.NotificationViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


@Composable
fun NotificationScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel(),
) {
    NotificationScreen(
        innerPaddingValues,
        {navController.popBackStack()},
        navController,
        viewModel.notifications,
        viewModel::deleteNotification,
        viewModel.deletedNotifications
    )
}

@Composable
fun NotificationScreen(
    innerPaddingValues: PaddingValues,
    onReturn: () -> Unit,
    navController: NavController,
    notifications: State<Flow<PagingData<NotificationData>>>,
    onDelete: (String)->Unit,
    deletedNotifications: SnapshotStateSet<String>
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
    ) {
        NotificationsTopBar(onReturn)
        val items = notifications.value.collectAsLazyPagingItems()
        if (items.itemCount == 0) {
            Text(
                stringResource(R.string.no_notifications),
                style = typography.bodyMedium.copy(colorScheme.secondary),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items.itemCount) { it ->
                    if (!deletedNotifications.contains((items[it]?.notificationId) ?: "")) {
                        NotificationElement(
                            notificationData = items[it] ?: NotificationData.EmptyNotification(),
                            navController = navController,
                            showDeleteButton = true,
                            onDelete = onDelete,
                            showNotification = true
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsTopBar(onReturn: () -> Unit) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primaryContainer,
            titleContentColor = colorScheme.primary,
        ),
        navigationIcon = {
            IconButton(onClick = { onReturn() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        },
        actions = {},
        title = {Text(stringResource(R.string.notifications))}
    )
}