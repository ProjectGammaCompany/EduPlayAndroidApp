package com.eduplay.moblie.ui.screens

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
import com.eduplay.moblie.R
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.theme.Typography
import com.eduplay.moblie.ui.viewmodel.BluetoothViewModel
import com.eduplay.moblie.ui.viewmodel.EventScreenViewModel
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.useCases.BluetoothConnectionFragment
import com.eduplay.moblie.utils.hasPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

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
    bluetoothViewModel: BluetoothViewModel
) {
    var dataFetched by remember { mutableStateOf(false) }
    var noInternet by remember { mutableStateOf(false) }

    if (!dataFetched) {
        viewModel.fetchData(
            eventId,
            { dataFetched = true },
            { noInternet = true })
    }

    if (viewModel.unauthorised.value) {
        Log.d("EVENT_SCREEN", "unauthorised")
        AuthScreenNavigator(navController)
    }
    if (noInternet) {
        NoInternetConnectionToast()
    }

    val startEvent = {
        navController.navigate("play_event/${eventId}")
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

    val bluetoothScanPermission =
        rememberPermissionState(permission = Manifest.permission.BLUETOOTH_SCAN)
    var askScanPermission by remember { mutableStateOf(false) }

    val bluetoothConnectPermission =
        rememberPermissionState(permission = Manifest.permission.BLUETOOTH_CONNECT)
    var askConnectPermission by remember { mutableStateOf(false) }

    val fineLocationPermission =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    var askLocationPermission by remember { mutableStateOf(false) }

    val advertisePermission =
        rememberPermissionState(permission = Manifest.permission.BLUETOOTH_ADVERTISE)
    var askAdvertisePermission by remember { mutableStateOf(false) }

    LaunchedEffect(askScanPermission) {
        if (askScanPermission) {
            bluetoothScanPermission.launchPermissionRequest()
            askScanPermission = false
        }
    }
    LaunchedEffect(askConnectPermission) {
        if (askConnectPermission) {
            bluetoothConnectPermission.launchPermissionRequest()
            askConnectPermission = false
        }
    }
    LaunchedEffect(askLocationPermission) {
        if (askLocationPermission) {
            fineLocationPermission.launchPermissionRequest()
            askLocationPermission = false
        }
    }
    LaunchedEffect(askAdvertisePermission) {
        if (askAdvertisePermission) {
            advertisePermission.launchPermissionRequest()
            askAdvertisePermission = false
        }
    }


    val context = LocalContext.current
    val runGetPermissions = {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        askScanPermission = !context.hasPermission(bluetoothScanPermission)
        askConnectPermission = !context.hasPermission(bluetoothConnectPermission)

        if (
            context.hasPermission(bluetoothScanPermission) &&
            context.hasPermission(bluetoothConnectPermission)
        ) {
            Log.d("BLUETOOTH_TEST", "permission granted")
            // fragment?.connect()
        }
        //}
        askLocationPermission = !context.hasPermission(fineLocationPermission)
        askAdvertisePermission = !context.hasPermission(advertisePermission)
        if (context.hasPermission(fineLocationPermission)) {
            Log.d("BLUETOOTH_TEST", "permission granted")
            // fragment?.connect()
        }

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
    AndroidFragment<BluetoothConnectionFragment>() { connectionFragment ->
        fragment = connectionFragment

    }

    val turnOnBluetooth = {
        if (!isCompetitionMode.value) {
            runGetPermissions()
            requireAdapter = true
            toggleCompetitionMode(true)
        } else {
            if (adapter.value != null) {
                try {
                    bluetoothViewModel.stopScan()
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
                showConnectionList,
                onDoesNotSupportBluetooth = onDoesNotSupportBluetooth,
                onConnectionTookTooLong = onConnectionTookTooLong
            )
            requireAdapter = false
        }
    }

    LaunchedEffect(canShowConnectionList) {
        if (canShowConnectionList && adapter.value != null) {
            try {
                bluetoothViewModel.discoverDevices(
                    onConnectionTookTooLong
                )
            } catch (_: SecurityException) {
                runGetPermissions()
                canShowConnectionList = false
                requireAdapter = false
                return@LaunchedEffect
            }
        } else if (canShowConnectionList && adapter.value == null) {
            requireAdapter = true
            canShowConnectionList = false
        } else {
            canShowConnectionList = false
        }
    }
    LaunchedEffect(canShowConnectionList) {
        Log.d("ADAPTER", bluetoothViewModel.foundDevices.size.toString())
    }
    if (canShowConnectionList) {
        ConnectionList(
            bluetoothViewModel.foundDevices,
            {
                turnOnBluetooth()
                canShowConnectionList = false
            },

            { address: String ->
                try {
                    bluetoothViewModel.connect(
                        address,
                        { Log.e("CONNECT", "couldnot connect") }
                    )
                } catch (e: SecurityException) {
                    Log.e("CONNECT", e.message ?: "", e)
                }
            }
        )
    }


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
        isCompetitionMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConnectionList(
    foundDevices: SnapshotStateMap<String, String?>,
    turnOnBluetooth: () -> Unit,
    onConnectToDevice: (String) -> Unit,

) {
    ModalBottomSheet(turnOnBluetooth) {
        LazyColumn {
            items(foundDevices.entries.toList()) {
                TextButton(
                    onClick = { onConnectToDevice(it.key) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(it.value ?: it.key)
                }

            }
        }
    }
}

@Composable
fun EventScreen(
    innerPaddingValues: PaddingValues,
    eventCreatorMode: State<Boolean>,
    isEventFavourite: State<Boolean>,
    eventName: State<String>,
    tags: SnapshotStateList<EventTag>,
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
    isCompetitionMode: State<Boolean>
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
        EditDialog(onCloseEditEvent)
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
        TopAppBarEventScreen(
            eventCreatorMode,
            isEventFavourite,
            onEditEvent,
            onAddToFavourite,
            onShowComplaintDialog,
            onReturn,
            toggleBluetooth,
            isCompetitionMode
        )

        EventScreenHeader(
            eventName,
            author,
            eventCreatorMode,
            isCompleted,
            cover,
            headers
        )

        if (eventCreatorMode.value) {
            EventCreatorBody(
                tags,
                info,
                description,
                privateEvent
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
                showResults
            )
        }
    }
}


@Composable
private fun EditDialog(onClose: () -> Unit) {
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
                    uriHandler.openUri("http://localhost/")//TODO("попросить настоящий url фронта")
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
    isCompetitionMode: State<Boolean>
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
                        imageVector = ImageVector.vectorResource(R.drawable.hand),
                        contentDescription = stringResource(R.string.report_event)
                    )
                }
                IconButton(
                    onClick = { TODO("реализовать кнопку скачать событие") },
                    modifier = Modifier.testTag("download_btn")
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.download),
                        contentDescription = stringResource(R.string.download_event)
                    )
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
) {
    Row(Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(cover)
                .httpHeaders(headers = headers.value)
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = eventName.value,
            placeholder = painterResource(R.drawable.eduplaylogo),
            error = painterResource(id = R.drawable.ic_launcher_background),
            modifier = Modifier
                .width(130.dp)
                .weight(0.35f)
                .testTag("event_image")

        )
        Text(
            eventName.value,
            style = typography.headlineMedium
                .copy(color = colorScheme.onBackground),
            maxLines = 3,
            modifier = Modifier
                .weight(0.6f)
                .align(Alignment.Bottom)
                .padding(horizontal = 10.dp)
                .testTag("event_title")
        )
    }
    Row(modifier = Modifier.padding(horizontal = 10.dp)) {
        Box(
            modifier = Modifier
                .width(120.dp)
                .weight(0.35f)
        ) {
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
                        .align(Alignment.Center)
                        .testTag("is_completed_chip")


                )
            }
        }
        if (!eventCreatorMode.value) {
            Text(
                author.value,
                style = typography.labelLarge
                    .copy(color = colorScheme.onBackground),
                maxLines = 1,
                color = colorScheme.primary,
                modifier = Modifier
                    .weight(0.6f)
                    .padding(horizontal = 10.dp)
                    .align(Alignment.CenterVertically)
                    .testTag("author")
            )
        }
    }
}

@Composable
private fun GeneralInfo(
    tags: SnapshotStateList<EventTag>,
    info: List<Pair<Int, String?>>,
    description: String
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tags")
        ) {
            tags.forEach { tagName ->
                EventTag(tagName.name)
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
                text = description,
                style = typography.bodyMedium.copy(color = colorScheme.onBackground)
            )
        }
    }

}

@Composable
private fun GeneralUserBody(
    tags: SnapshotStateList<EventTag>,
    info: List<Pair<Int, String?>>,
    description: State<String>,
    isOpen: State<Boolean>,
    isContinuing: State<Boolean>,
    isCompleted: State<Boolean>,
    startEvent: () -> Unit,
    showResults: () -> Unit
) {
    Column(verticalArrangement = Arrangement.Center) {
        Box(modifier = Modifier.fillMaxHeight(if (isOpen.value || isCompleted.value) 0.85f else 1f)) {
            GeneralInfo(tags, info, description.value)
        }

        if (isOpen.value && !isCompleted.value) {
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
    tags: SnapshotStateList<EventTag>,
    info: List<Pair<Int, String?>>,
    description: State<String>,
    privateEvent: State<Boolean>
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
                    selectedContentColor = colorScheme.primary,
                    unselectedContentColor = colorScheme.onSurface,
                    modifier = Modifier.testTag(stringResource(title))
                )
            }
        }
    }
    val infoP = info.toMutableList()
    infoP.add(
        Pair(
            R.string.private_event_flag,
            if (privateEvent.value) stringResource(R.string.private_event) else stringResource(R.string.public_event)
        ),
    )
    when (selectedTabIdx) {
        0 -> GeneralInfo(tags, infoP, description.value)
        1 -> StatisticsInfo()
        else -> Box {}
    }

}

@Composable
private fun StatisticsInfo() {
    Text("Coming soon")
    //TODO("статистики на экране статистик")
}

//@Composable
//@Preview
//private fun Event() {
//    EventScreen(
//        PaddingValues(),
//        isEventFavourite = statetrue,
//        eventCreatorMode = false,
//        eventName = "Событие",
//        tags = remember { mutableStateListOf<EventTag>(EventTag("", "tag1")) },
//        author = "Author",
//        isCompleted = false,
//        cover = "",
//        info = remember { mutableStateListOf() },
//        description = "Some information",
//        privateEvent = false,
//        isOpen = false,
//        isContinuing = false,
//        onAddToFavourite = { },
//        onComplain = { _: String -> },
//        startEvent = {},
//        showResults = { },
//        onReturn = { false },
//        headers = remember { mutableStateOf(NetworkHeaders.Builder().build()) },
//        toggleBluetooth = {},
//        isCompetitionMode = remember { mutableStateOf(false) }
//    )
//}
