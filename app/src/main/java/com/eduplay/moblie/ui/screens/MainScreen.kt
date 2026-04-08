package com.eduplay.moblie.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.network.NetworkHeaders
import com.eduplay.moblie.R
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.JoinByCodeDialog
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.elements.QuestListElement
import com.eduplay.moblie.ui.elements.TryAgainLaterToast
import com.eduplay.moblie.ui.theme.EduPlayTheme
import com.eduplay.moblie.ui.viewmodel.CurrentModeViewModel
import com.eduplay.moblie.ui.viewmodel.EventListViewModel
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.ui.viewmodel.MainScreenViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun MainScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    isCompetitionMode: State<Boolean>,
    onStopCompetition: () -> Unit,
    viewModel: MainScreenViewModel = hiltViewModel(),
    eventListViewModel: EventListViewModel = hiltViewModel(),
    imageHeaderViewModel: ImageHeaderViewModel = hiltViewModel(),
    currentModeViewModel: CurrentModeViewModel = hiltViewModel()
) {
    val onEventClick = { eventId: String ->
        navController.navigate("event_screen/$eventId")
    }
    val onFavourite = { eventId: String, isFavorite: Boolean ->
        eventListViewModel.changeFavourite(
            eventId,
            isFavorite
        )
    }

    if (viewModel.noInternetConnection.value || eventListViewModel.noInternetConnection.value) {
        NoInternetConnectionToast()
    }
    if (viewModel.unauthorised.value || eventListViewModel.unauthorised.value) {
        Log.d("main_screen", "unauthorised")
        AuthScreenNavigator(navController)
    }
    if (eventListViewModel.unknownError.value) {
        TryAgainLaterToast()
        eventListViewModel.unknownError.value = false
    }

    val events = viewModel.events

    val onSearch = {
        navController.navigate("search")
    }

    var joinByCode by remember { mutableStateOf(false) }
    val onJoinByCode = {
        joinByCode = true
    }
    if (joinByCode) {
        JoinByCodeDialog({ joinByCode = false }, navController)
    }

    val currentMode = currentModeViewModel.currentMode.value.collectAsState(OfflineModeManager.AppModes.ONLINE)

    MainScreen(
        innerPaddingValues,
        events,
        onEventClick,
        onFavourite,
        imageHeaderViewModel.headers,
        { image: String -> imageHeaderViewModel.getFullUrl(image) },
        isCompetitionMode,
        onStopCompetition,
        onSearch,
        onJoinByCode,
        currentMode
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    innerPaddingValues: PaddingValues,
    events: State<Flow<PagingData<QuestShortInfo>>>,
    onEventClick: (String) -> Unit,
    onFavourite: (String, Boolean) -> Unit,
    headers: State<NetworkHeaders>,
    imageUrl: (String) -> String,
    isCompetitionMode: State<Boolean>,
    onStopCompetition: () -> Unit,
    onSearch: () -> Unit,
    onJoinByCode: () -> Unit,
    currentMode: State<OfflineModeManager.AppModes>
) {
    Column(
        modifier = Modifier
            .background(color = colorScheme.background)
            .padding(
                top = 0.dp, //innerPaddingValues.calculateTopPadding(),
                bottom = innerPaddingValues.calculateBottomPadding(),
                start = innerPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPaddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
            .fillMaxSize()
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.primary,
                titleContentColor = colorScheme.onPrimaryFixed,
            ),
            actions = {
                if (isCompetitionMode.value) {
                    IconButton(onClick = { onStopCompetition() }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.bluetooth_disabled),
                            contentDescription = stringResource(R.string.turn_off_bluetooth),
                            tint = colorScheme.onPrimaryFixed
                        )
                    }
                }
                IconButton(onClick = { onSearch() }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_events),
                        tint = colorScheme.onPrimaryFixed
                    )
                }
            },
            title = {
                Text(stringResource(R.string.app_name))
            }
        )



        if (currentMode.value == OfflineModeManager.AppModes.ONLINE) {
            Button(
                onClick = { onJoinByCode() },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(stringResource(R.string.join_by_code))
            }
        }


        val eventsInfo = events.value.collectAsLazyPagingItems()
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(eventsInfo.itemCount) { position ->
                val itemValue = eventsInfo[position]
                if (itemValue != null) {
                    val onEventClick = { onEventClick(itemValue.id) }
                    QuestListElement(
                        itemValue,
                        onEventClick,
                        { isFavourite -> onFavourite(itemValue.id, isFavourite) },
                        headers,
                        imageUrl
                    )
                }
            }
        }

    }
}


@Preview
@Composable
fun MainScreenPreview() {
    val headers = remember { mutableStateOf(NetworkHeaders.Builder().build()) }
    val events = flowOf<PagingData<QuestShortInfo>>(
        PagingData.from(
            listOf<QuestShortInfo>(
                QuestShortInfo(
                    "1",
                    "test",
                    "",
                    "",
                    1.0,
                    false,
                    listOf(),
                    false
                ),
                QuestShortInfo(
                    "2",
                    "test",
                    "",
                    "",
                    1.0,
                    true,
                    listOf(EventTag("id", "tag 1"), EventTag("id", "tag 2")),
                    true
                )
            )
        )
    )


    val nothing = { string: String -> }
    val nothingB = { string: String, bool: Boolean -> }
    EduPlayTheme {
        MainScreen(
            PaddingValues(0.dp),
            remember { mutableStateOf(events) },
            nothing,
            nothingB,
            headers,
            { it },
            remember { mutableStateOf(false) },
            {},
            {},
            {},
            remember { mutableStateOf(OfflineModeManager.AppModes.OFFLINE) }
        )
    }


}

