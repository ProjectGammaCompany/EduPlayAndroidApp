package com.eduplay.moblie.ui.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ClipData
import android.content.ComponentName
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.compose.AndroidFragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eduplay.moblie.BuildConfig
import com.eduplay.moblie.R
import com.eduplay.moblie.models.EventGroup
import com.eduplay.moblie.repository.responseTypes.JoinCodeInfo
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.ResultStats
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.JoinGroupDialog
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.elements.StatisticsInfo
import com.eduplay.moblie.ui.theme.Typography
import com.eduplay.moblie.ui.viewmodel.BluetoothViewModel
import com.eduplay.moblie.ui.viewmodel.EventScreenViewModel
import com.eduplay.moblie.ui.viewmodel.EventScreenViewModel.EditorStatColumns
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.useCases.BluetoothConnectionFragment
import com.eduplay.moblie.useCases.DateConverter
import com.eduplay.moblie.useCases.OfflineModeManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.Flow
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun EventScreen(
    innerPaddingValues: PaddingValues,
    eventId: String,
    navController: NavController,
    manager: State<BluetoothManager?>,
    adapter: State<BluetoothAdapter?>,
    updateManger: (BluetoothManager?) -> Unit,
    updateAdapter: (BluetoothAdapter?) -> Unit,
    viewModel: EventScreenViewModel = hiltViewModel(),
    imageHeaderViewModel: ImageHeaderViewModel = hiltViewModel(),
    isCompetitionMode: State<Boolean>,
    toggleCompetitionMode: (Boolean) -> Unit,
    bluetoothViewModel: BluetoothViewModel,
    onDownloadEvent: (String, String) -> ComponentName?
) {
    var dataFetched by remember { mutableStateOf(false) }
    var noInternet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    if (!dataFetched) {
        viewModel.fetchData(
            eventId,
            {
                dataFetched = true
                isRefreshing = false
            },
            { noInternet = true },
            context
        )
    }
    if (
        viewModel.downloadStatusObserver.downloaded.contains(eventId) ||
        viewModel.downloadStatusObserver.downloading.keys.contains(eventId)
    ) {
        viewModel.isDownloaded.value = true
    }

    if (viewModel.failedToSendAnswers.value) {
        Toast.makeText(context, stringResource(R.string.no_internet), Toast.LENGTH_LONG).show()
        viewModel.failedToSendAnswers.value = true
    }

    if (viewModel.unauthorised.value) {
        Log.d("EVENT_SCREEN", "unauthorised")
        AuthScreenNavigator(navController)
    }
    if (noInternet) {
        NoInternetConnectionToast()
    }


    val onComplain = { reason: String ->
        viewModel.complain(eventId, reason)
    }
    val showResults = {
        navController.navigate("event_result/${eventId}")
    }
    val onAddToFavourite = {
        if (!viewModel.isEventFavourite.value) {
            viewModel.addToFavourite(eventId)
        } else {
            viewModel.removeFromFavourite(eventId)
        }
    }
    val onReturn = {
        navController.popBackStack()
    }
    val onDownload = {
        viewModel.downloadEvent(eventId, onDownloadEvent)
    }

    var requireAdapter by remember { mutableStateOf(false) }


    var canShowConnectionList by remember { mutableStateOf(false) }
    val showConnectionList = {
        canShowConnectionList = true
        requireAdapter = false
    }

    var supportsBluetooth by remember { mutableStateOf(true) }
    val onDoesNotSupportBluetooth = {
        supportsBluetooth = false
        requireAdapter = false
    }

    var connectionTookTooLong by remember { mutableStateOf(false) }
    val onConnectionTookTooLong = {
        connectionTookTooLong = true
        requireAdapter = false
    }


    var fragment by remember { mutableStateOf<BluetoothConnectionFragment?>(null) }
    AndroidFragment<BluetoothConnectionFragment> { connectionFragment ->
        fragment = connectionFragment

    }


    val turnOnBluetooth = {
        if (!isCompetitionMode.value) {
            requireAdapter = true
            toggleCompetitionMode(true)
            bluetoothViewModel.askForPermissions.value = true
        } else {
            if (adapter.value != null) {
                try {
                    toggleCompetitionMode(false)
                } catch (e: SecurityException) {
                    Log.d("cant_stop_scan", e.message ?: "")
                }
            }
        }
    }

    LaunchedEffect(fragment, requireAdapter) {
        if (requireAdapter && fragment != null) {
            fragment?.startBluetooth(
                manager,
                adapter,
                updateManger,
                updateAdapter,
                {},
                onDoesNotSupportBluetooth = onDoesNotSupportBluetooth,
                onConnectionTookTooLong = onConnectionTookTooLong
            )
            requireAdapter = false
        }
    }

    LaunchedEffect(canShowConnectionList) {
        if (canShowConnectionList && adapter.value != null) {
            bluetoothViewModel.discoverDevices(context, onConnectionTookTooLong)
        } else if (canShowConnectionList && adapter.value == null) {
            requireAdapter = true
            canShowConnectionList = false
        }
    }

    val onStopShowingDeviceList = {
        bluetoothViewModel.stopScan(context)
        canShowConnectionList = false
    }
    val proceedWithBluetooth = {
        onStopShowingDeviceList()
        navController.navigate("play_event/${eventId}")
    }

    val showGroupDialog = remember { mutableStateOf(false) }
    val turnOffDialog = {
        showGroupDialog.value = false
    }
    if (showGroupDialog.value) {
        val start = {
            if (isCompetitionMode.value) {
                showConnectionList()
            } else {
                navController.navigate("play_event/${eventId}")
            }
        }
        JoinGroupDialog(eventId, turnOffDialog, start)
    }

    val startEvent = {
        if (viewModel.needGroup.value) {
            showGroupDialog.value = true
        } else if (isCompetitionMode.value) {
            showConnectionList()
        } else {
            navController.navigate("play_event/${eventId}")
        }
    }


    if (viewModel.downloadStatusObserver.downloaded.contains(eventId)) {
        viewModel.needsUpdate.value = false
    }

    if (canShowConnectionList) {
        BluetoothDeviceListScreen(
            foundDevices = bluetoothViewModel.foundDevices,
            connect = { address, function ->
                bluetoothViewModel.connect(context, address, function)
            },
            devicesConnectionStatus = bluetoothViewModel.devicesConnectionStatus,
            onProceed = proceedWithBluetooth,
            innerPaddingValues = innerPaddingValues,
            onReturn = onStopShowingDeviceList
        )
    }


    val onRefresh = {
        dataFetched = false
        isRefreshing = true
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        EventScreen(
            innerPaddingValues,
            viewModel.eventCreatorMode,
            viewModel.isEventFavourite,
            viewModel.eventName,
            viewModel.tags,
            viewModel.author,
            viewModel.isCompleted,
            imageHeaderViewModel.getFullUrl(viewModel.cover.value),
            viewModel.info,
            viewModel.description,
            viewModel.privateEvent,
            viewModel.isOpen,
            viewModel.isContinuing,
            onAddToFavourite,
            onComplain,
            startEvent,
            showResults,
            onReturn,
            imageHeaderViewModel.headers,
            turnOnBluetooth,
            isCompetitionMode,
            canShowConnectionList,
            viewModel.password,
            viewModel.groups,
            eventId,
            viewModel.joinCode,
            onDownload,
            viewModel.canDownload,
            viewModel.isRated,
            { rating -> viewModel.rateEvent(rating, eventId) },
            viewModel.groupEvent,
            viewModel.downloadStatusObserver.downloading,
            viewModel.downloadStatusObserver.downloaded,
            viewModel.isDownloaded,
            { viewModel.deleteEventFromDevice(eventId) },
            viewModel.failedToSendAnswers,
            imageHeaderViewModel.appMode,
            viewModel.needsUpdate,
            viewModel.editorEventStats,
            viewModel::sortEventStatsByColumn
        )
    }
}

@Composable
fun EventScreen(
    innerPaddingValues: PaddingValues,
    eventCreatorMode: State<Boolean>,
    isEventFavourite: State<Boolean>,
    eventName: State<String>,
    tags: SnapshotStateList<String>,
    author: State<String>,
    isCompleted: State<Boolean>,
    cover: String,
    info: SnapshotStateList<Pair<Int, String?>>,
    description: State<String>,
    privateEvent: State<Boolean>,
    isOpen: State<Boolean>,
    isContinuing: State<Boolean>,
    onAddToFavourite: () -> Unit,
    onComplain: (String) -> Unit,
    startEvent: () -> Unit,
    showResults: () -> Unit,
    onReturn: () -> Boolean,
    headers: State<NetworkHeaders>,
    toggleBluetooth: () -> Unit,
    isCompetitionMode: State<Boolean>,
    canShowConnectionList: Boolean,
    password: State<String>,
    groups: SnapshotStateList<EventGroup>,
    eventId: String,
    joinCode: State<JoinCodeInfo>,
    onDownload: () -> Unit,
    canDownLoad: State<Boolean>,
    isRated: State<Boolean>,
    onRate: (Int) -> Unit,
    groupEvent: State<Boolean>,
    downloadingEvents: SnapshotStateMap<String, String>,
    downloadedEvents: SnapshotStateSet<String>,
    isDownloaded: State<Boolean>,
    onDeleteEvent: () -> Unit,
    failedToSendAnswers: State<Boolean>,
    appMode: State<Flow<OfflineModeManager.AppModes>>,
    needsUpdate: State<Boolean>,
    groupEditorStats: State<ResultStats>,
    sortEventsByColumn: (EditorStatColumns, Boolean) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    val onEditEvent = {
        showEditDialog = true
    }
    val onCloseEditEvent = {
        showEditDialog = false
    }
    var showComplaintDialog by remember { mutableStateOf(false) }
    val onShowComplaintDialog = {
        showComplaintDialog = true
    }
    val onHideComplaint = {
        showComplaintDialog = false
    }

    if (showEditDialog) {
        EditDialog(onCloseEditEvent, eventId)
    }
    if (showComplaintDialog) {
        ComplaintDialog(onHideComplaint, onComplain)
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
        if (!canShowConnectionList) {
            TopAppBarEventScreen(
                eventCreatorMode,
                isEventFavourite,
                onEditEvent,
                onAddToFavourite,
                onShowComplaintDialog,
                onReturn,
                toggleBluetooth,
                isCompetitionMode,
                onDownload,
                canDownLoad,
                downloadingEvents,
                downloadedEvents,
                eventId,
                isDownloaded,
                onDeleteEvent,
                needsUpdate
            )

            EventScreenHeader(
                eventName,
                author,
                eventCreatorMode,
                isCompleted,
                cover,
                headers,
                appMode,
                needsUpdate,
                onDownload
            )

            if (eventCreatorMode.value) {
                EventCreatorBody(
                    tags,
                    info,
                    description,
                    privateEvent,
                    password,
                    groups,
                    joinCode,
                    groupEvent,
                    groupEditorStats,
                    sortEventsByColumn
                )
            } else {
                GeneralUserBody(
                    tags,
                    info,
                    description,
                    isOpen,
                    isContinuing,
                    isCompleted,
                    startEvent,
                    showResults,
                    isRated,
                    onRate,
                    failedToSendAnswers
                )
            }
        }
    }
}


@Composable
private fun EditDialog(onClose: () -> Unit, eventId: String) {
    val uriHandler = LocalUriHandler.current
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
                    uriHandler.openUri(BuildConfig.FRONTEND_URL)
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
        },
        modifier = Modifier.testTag("edit_dialog")
    )

}

@Composable
private fun ComplaintDialog(onClose: () -> Unit, onComplain: (String) -> Unit) {
    val reasonSate = rememberTextFieldState()
    AlertDialog(
        title = {
            Text(text = stringResource(R.string.report_event))
        },
        text = {
            OutlinedTextField(
                state = reasonSate,
                label = { Text(stringResource(R.string.complaint)) },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
            )
        },
        onDismissRequest = {
            onClose()
            reasonSate.clearText()

        },
        confirmButton = {
            TextButton(
                onClick = {
                    onComplain(reasonSate.text.toString())
                    onClose()
                    reasonSate.clearText()
                }
            ) {
                Text(stringResource(R.string.send))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onClose()
                    reasonSate.clearText()
                }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        modifier = Modifier.testTag("report_dialog")
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarEventScreen(
    eventCreatorMode: State<Boolean>,
    isFavourite: State<Boolean>,
    onEditEvent: () -> Unit,
    onAddToFavourite: () -> Unit,
    onComplain: () -> Unit,
    onReturn: () -> Boolean,
    toggleBluetooth: () -> Unit,
    isCompetitionMode: State<Boolean>,
    onDownload: () -> Unit,
    canDownLoad: State<Boolean>,
    downloadingEvents: SnapshotStateMap<String, String>,
    downloadedEvents: SnapshotStateSet<String>,
    eventId: String,
    isDownloaded: State<Boolean>,
    onDelete: () -> Unit,
    needsUpdate: State<Boolean>
) {
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
        actions = {
            if (!eventCreatorMode.value) {
                IconButton(
                    onClick = toggleBluetooth,
                ) {
                    if (!isCompetitionMode.value) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.bluetooth),
                            contentDescription = stringResource(R.string.start_bluetooth)
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.bluetooth_disabled),
                            contentDescription = stringResource(R.string.turn_off_bluetooth)
                        )
                    }
                }
                IconButton(
                    onClick = { onComplain() },
                    modifier = Modifier.testTag("report_btn")
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.exclamation),
                        contentDescription = stringResource(R.string.report_event)
                    )
                }
                if (downloadingEvents.values.contains(eventId)) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0F,
                        targetValue = 360F,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing)
                        )
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.progress),
                        contentDescription = stringResource(R.string.downloading),
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .rotate(angle)
                    )
                } else if (isDownloaded.value || downloadedEvents.contains(eventId)) {
                    IconButton(
                        onClick = { onDelete() },
                        modifier = Modifier.testTag("delete_from_device_btn")
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.delete),
                            contentDescription = stringResource(R.string.delete_event)
                        )
                    }

                }

                if (
                    (!isDownloaded.value && canDownLoad.value)
                    || needsUpdate.value
                ) {
                    IconButton(
                        onClick = { onDownload() },
                        modifier = Modifier.testTag("download_btn")
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.download),
                            contentDescription = stringResource(R.string.download_event)
                        )
                    }
                }

                IconButton(
                    onClick = { onAddToFavourite() },
                    modifier = Modifier.testTag("favourite_btn")
                ) {
                    if (isFavourite.value) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.star_filled),
                            contentDescription = stringResource(R.string.add_to_favourite),
                            modifier = Modifier.testTag("is_favourite_btn")
                        )
                    } else {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.star),
                            contentDescription = stringResource(R.string.add_to_favourite),
                            modifier = Modifier.testTag("is_not_favourite_btn")
                        )
                    }
                }
            } else {
                IconButton(onClick = { onEditEvent() }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.edit),
                        contentDescription = stringResource(R.string.edit_event),
                        modifier = Modifier.testTag("edit_btn")
                    )
                }
            }
        },
        title = {}
    )

}

@Composable
private fun EventScreenHeader(
    eventName: State<String>,
    author: State<String>,
    eventCreatorMode: State<Boolean>,
    isCompleted: State<Boolean>,
    cover: String?,
    headers: State<NetworkHeaders>,
    appMode: State<Flow<OfflineModeManager.AppModes>>,
    needsUpdate: State<Boolean>,
    onDownload: () -> Unit
) {
    val context = LocalContext.current
    val isOffline = appMode.value.collectAsState(OfflineModeManager.AppModes.ONLINE)
    Row(Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {

        AsyncImage(
            model = if (isOffline.value == OfflineModeManager.AppModes.ONLINE) {
                ImageRequest.Builder(LocalContext.current)
                    .data(cover)
                    .httpHeaders(headers = headers.value)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
            } else {
                ImageRequest.Builder(LocalContext.current)
                    .data(File(context.filesDir, cover ?: ""))
                    .crossfade(true)
                    .build()
            },
            contentDescription = eventName.value,
            placeholder = BrushPainter(
                Brush.linearGradient(
                    listOf(
                        colorScheme.primary,
                        colorScheme.secondary,
                        colorScheme.tertiary
                    )
                )
            ),
            error = BrushPainter(
                Brush.linearGradient(
                    listOf(
                        colorScheme.primary,
                        colorScheme.secondary,
                        colorScheme.tertiary
                    )
                )
            ),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(130.dp)
                .height(130.dp)
                .weight(0.35f)
                .testTag("event_image")

        )
        Column(
            verticalArrangement = Arrangement.Bottom, modifier = Modifier
                .weight(0.6f)
                .align(Alignment.Bottom)
                .heightIn(min = 130.dp)
        ) {
            Text(
                eventName.value,
                style = typography.headlineMedium
                    .copy(color = colorScheme.onBackground),
                modifier = Modifier
                    //.weight(0.6f)
                    .align(Alignment.Start)
                    .padding(horizontal = 10.dp)
                    .testTag("event_title")
            )
            if (!eventCreatorMode.value) {
                Text(
                    author.value,
                    style = typography.labelLarge
                        .copy(color = colorScheme.onBackground),
                    maxLines = 1,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .align(Alignment.Start)
                        .testTag("author")
                )
            }
        }
    }
    FlowRow(modifier = Modifier.padding(horizontal = 10.dp)) {
        if (!eventCreatorMode.value && isCompleted.value) {
            AssistChip(
                onClick = {}, //так и должно быть при нажатии ничего не происходит
                label = {
                    Text(
                        stringResource(R.string.completed),
                        style = typography.labelSmall
                            .copy(color = colorScheme.onBackground)
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
                    .align(Alignment.CenterVertically)
                    .testTag("is_completed_chip")
                    .padding(horizontal = 3.dp)
            )
        }

        if (needsUpdate.value) {
            AssistChip(
                onClick = onDownload,
                label = {
                    Text(
                        stringResource(R.string.update_event),
                        style = typography.labelSmall
                            .copy(color = colorScheme.onBackground)
                    )
                },
                leadingIcon = {
                    Icon(
                        ImageVector.vectorResource(R.drawable.download),
                        contentDescription = stringResource(R.string.update_event),
                        Modifier.size(AssistChipDefaults.IconSize)
                    )
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .testTag("hasUpdated_chip")
                    .padding(horizontal = 3.dp)
            )
        }
    }

}

@Composable
private fun GeneralInfo(
    tags: SnapshotStateList<String>,
    info: SnapshotStateList<Pair<Int, String?>>,
    description: State<String>,
    needToRate: Boolean? = null,
    onRate: ((Int) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .testTag("general_info")
    ) {
        if (needToRate != null && needToRate && onRate != null) {
            RateBar(onRate)
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tags")
        ) {
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
                style = typography.titleLarge
                    .copy(color = colorScheme.onBackground),
                modifier = Modifier.padding(bottom = 5.dp, top = 10.dp)
            )
            info.forEach { pair ->
                if (pair.second != null) {
                    Row {
                        Text(
                            text = stringResource(pair.first),
                            style = typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onBackground
                            ),
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(
                            text = pair.second ?: "",
                            style = typography.bodyMedium.copy(color = colorScheme.onBackground)
                        )
                    }
                }
            }
            Text(
                text = stringResource(R.string.description),
                style = typography.titleLarge.copy(color = colorScheme.onBackground),
                modifier = Modifier.padding(bottom = 5.dp, top = 10.dp)
            )
            Text(
                text = description.value,
                style = typography.bodyMedium.copy(color = colorScheme.onBackground)
            )
        }
    }

}

@Composable
private fun GeneralUserBody(
    tags: SnapshotStateList<String>,
    info: SnapshotStateList<Pair<Int, String?>>,
    description: State<String>,
    isOpen: State<Boolean>,
    isContinuing: State<Boolean>,
    isCompleted: State<Boolean>,
    startEvent: () -> Unit,
    showResults: () -> Unit,
    isRated: State<Boolean>,
    onRate: ((Int) -> Unit),
    failedToSendAnswers: State<Boolean>
) {
    Column(verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.fillMaxHeight(if (isOpen.value || isCompleted.value) 0.85f else 1f)) {
            GeneralInfo(tags, info, description, !isRated.value && isCompleted.value, onRate)
        }

        if (isOpen.value && !isCompleted.value) {
            if (failedToSendAnswers.value) {
                Text(
                    stringResource(R.string.failed_send_answers),
                    style = typography.labelSmall.copy(colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
            Button(
                onClick = startEvent,
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .weight(0.15f)
                    .align(Alignment.CenterHorizontally)
                    .testTag("start_event_btn")
            ) {
                Text(
                    if (!isContinuing.value) {
                        stringResource(R.string.start_event)
                    } else {
                        stringResource(R.string.continue_event)
                    },
                    style = Typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        if (isCompleted.value) {
            if (failedToSendAnswers.value) {
                Text(
                    stringResource(R.string.failed_send_answers),
                    style = typography.labelSmall.copy(colorScheme.secondary),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
            Button(
                onClick = { showResults() },
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .weight(0.15f)
                    .align(Alignment.CenterHorizontally)
                    .testTag("results_btn")
            ) {
                Text(
                    stringResource(R.string.show_result),
                    style = Typography.titleMedium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun RateBar(onRate: (Int) -> Unit) {
    val rateStates = remember {
        val list = List(5) { false }
        mutableStateListOf<Boolean>().apply {
            addAll(list)
        }
    }
    val currentlyRated = remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .padding(horizontal = 15.dp)
            //.border(width = 1.dp, shape = RoundedCornerShape(10.dp), color = colorScheme.secondary)
            .fillMaxWidth()
            .testTag("rate_bar")
    ) {
        Text(
            text = stringResource(R.string.rate_event),
            style = typography.titleMedium
                .copy(color = colorScheme.onBackground),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 5.dp, top = 10.dp)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            rateStates.forEachIndexed { idx, state ->
                IconButton(
                    onClick = {
                        currentlyRated.value = idx + 1
                        for (i in 0..idx) {
                            rateStates[i] = true
                        }
                        for (i in idx + 1..<rateStates.size) {
                            rateStates[i] = false
                        }
                    },
                    Modifier.padding(horizontal = 3.dp)
                ) {
                    if (state) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.star_filled),
                            "$idx",
                            tint = colorScheme.primary
                        )
                    } else {
                        Icon(
                            ImageVector.vectorResource(R.drawable.star),
                            "$idx",
                            tint = colorScheme.primary
                        )
                    }
                }
            }
        }
        OutlinedButton(
            onClick = { onRate(currentlyRated.value) },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.proceed)
            )
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
        style = typography.labelLarge.copy(color = colorScheme.onPrimaryContainer),
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
    tags: SnapshotStateList<String>,
    info: SnapshotStateList<Pair<Int, String?>>,
    description: State<String>,
    privateEvent: State<Boolean>,
    password: State<String>,
    groups: SnapshotStateList<EventGroup>,
    joinCode: State<JoinCodeInfo>,
    groupEvent: State<Boolean>,
    groupEditorStats: State<ResultStats>,
    sortEventsByColumn: (EditorStatColumns, Boolean) -> Unit
) {
    val tabs = remember<List<Int>> {
        listOf<Int>(
            R.string.general_info,
            R.string.statistics,
            R.string.privacy_settings
        )
    }
    var selectedTabIdx by remember { mutableIntStateOf(0) }
    Column {
        SecondaryTabRow(selectedTabIndex = selectedTabIdx) {
            tabs.forEachIndexed { index, title ->
                if (index == 2 && (!privateEvent.value && !groupEvent.value)) return@forEachIndexed
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
                    selectedContentColor = colorScheme.primary,
                    unselectedContentColor = colorScheme.onSurface,
                    modifier = Modifier
                        .testTag(stringResource(title))
                )
            }
        }
    }

    when (selectedTabIdx) {
        0 -> GeneralInfo(tags, info, description)
        1 -> StatisticsInfo(groupEditorStats, sortEventsByColumn)
        2 -> PrivacySettings(password, groups, joinCode, privateEvent, groupEvent)
        else -> Box {}
    }

}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun PrivacySettings(
    password: State<String>,
    groups: SnapshotStateList<EventGroup>,
    joinCode: State<JoinCodeInfo>,
    privateEvent: State<Boolean>,
    groupEvent: State<Boolean>
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val onCopy = { str: String ->
        val clipData =
            ClipData.newPlainText(context.getString(R.string.join_code), str)
        val clipEntry = ClipEntry(clipData)
        clipboardManager.setClip(clipEntry)
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .testTag("privacy_settings_section")
    ) {
        if (privateEvent.value) {
            Column {

                Text(
                    text = stringResource(R.string.join_code),
                    style = typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(end = 5.dp)
                )
                Row {
                    Text(
                        text = joinCode.value.joinCode,
                        style = typography.bodyLarge.copy(
                            color = colorScheme.onBackground
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 5.dp)
                    )
                    IconButton(
                        onClick = { onCopy(joinCode.value.joinCode) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.copy),
                            contentDescription = stringResource(R.string.copy_join_code)
                        )
                    }
                }
                val time = try {
                    DateConverter.convertForDisplay(joinCode.value.expiresAt)
                } catch (e: Exception) {
                    null
                }
                if (time != null) {
                    val stringBuilder = StringBuilder()
                    stringBuilder.append(context.getString(R.string.expires_at))
                    stringBuilder.append(time)


                    Text(
                        text = stringBuilder.toString(),
                        style = typography.bodyLarge.copy(
                            color = colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
            }


            Row(modifier = Modifier.padding(top = 5.dp)) {
                Text(
                    text = stringResource(R.string.event_password),
                    style = typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = password.value,
                    style = typography.bodyLarge.copy(color = colorScheme.onBackground)
                )
            }
        }

        if (groupEvent.value && groups.isNotEmpty()) {
            Text(
                text = stringResource(R.string.groups),
                style = typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onBackground
                ),
                modifier = Modifier
                    .padding(end = 5.dp)
                    .padding(top = 3.dp)
            )

            groups.forEach { group ->
                FlowRow {
                    Text(
                        text = group.login,
                        style = typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = colorScheme.onBackground
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 5.dp)
                    )

                    Text(
                        text = stringResource(R.string.password) + ": " + group.password,
                        style = typography.bodyLarge.copy(color = colorScheme.onBackground),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    IconButton(
                        onClick = { onCopy(group.password) },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.copy),
                            contentDescription = stringResource(R.string.copy_join_code)
                        )

                    }
                }
            }
        }
    }
}
