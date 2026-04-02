package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.network.NetworkHeaders
import com.eduplay.moblie.R
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.elements.QuestListElement
import com.eduplay.moblie.ui.viewmodel.EventListViewModel
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.ui.viewmodel.MyEventsViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun MyEventsScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: MyEventsViewModel = hiltViewModel(),
    eventListViewModel: EventListViewModel = hiltViewModel(),
    imageHeaderViewModel: ImageHeaderViewModel = hiltViewModel()
) {
    if (viewModel.noInternetConnection.value) {
        NoInternetConnectionToast()
    }

    if (viewModel.unauthorised.value || eventListViewModel.unauthorised.value) {
        AuthScreenNavigator(navController)
    }

    val onFavouriteToggle = { id: String, isFavourite: Boolean ->
        eventListViewModel.changeFavourite(id, isFavourite)
    }
    val onEventClick = { eventId: String -> navController.navigate("event_screen/$eventId") }

    MyEventsScreen(
        innerPaddingValues,
        onFavouriteToggle,
        viewModel.favourite,
        viewModel.completed,
        viewModel.created,
        onEventClick,
        imageHeaderViewModel.headers,
        { image: String -> imageHeaderViewModel.getFullUrl(image) }
    )

}

@Composable
private fun MyEventsScreen(
    innerPaddingValues: PaddingValues,
    onFavouriteToggle: (String, Boolean) -> Unit,
    favorite: State<Flow<PagingData<QuestShortInfo>>>,
    completed: State<Flow<PagingData<QuestShortInfo>>>,
    created: State<Flow<PagingData<QuestShortInfo>>>,
    onEventClick: (String) -> Unit,
    headers: State<NetworkHeaders>,
    imageUrl: (String) -> String
) {
    val tabs = remember<List<Int>> {
        listOf<Int>(
            R.string.favourite,
            R.string.completed,
            R.string.my_created
        )
    }
    var selectedTabIdx by remember { mutableIntStateOf(0) }


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
        MyEventsTopBar()
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
        when (selectedTabIdx) {
            0 -> ListOfEvents(
                favorite,
                onFavouriteToggle,
                onEventClick,
                headers,
                imageUrl
            )

            1 -> ListOfEvents(
                completed,
                onFavouriteToggle,
                onEventClick,
                headers,
                imageUrl
            )

            2 -> ListOfEvents(
                created,
                onFavouriteToggle,
                onEventClick,
                headers,
                imageUrl
            )

            else -> Box {}
        }

    }
}

@Composable
private fun ListOfEvents(
    events: State<Flow<PagingData<QuestShortInfo>>>,
    onFavouriteToggle: (String, Boolean) -> Unit,
    onEventClick: (String) -> Unit,
    headers: State<NetworkHeaders>,
    imageUrl: (String) -> String
) {
    val eventsInfo = events.value.collectAsLazyPagingItems()
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(eventsInfo.itemCount) { position ->
                val itemValue = eventsInfo[position]
                if (itemValue != null) {
                    QuestListElement(
                        itemValue,
                        { onEventClick(itemValue.id) },
                        { onFavouriteToggle(itemValue.id, itemValue.isFavourite) },
                        headers,
                        imageUrl
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyEventsTopBar() {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(stringResource(R.string.my_events))
        }
    )
}

//@Composable
//@Preview
//fun MyEventsPreview() {
//    val headers = remember { mutableStateOf(NetworkHeaders.Builder().build()) }
//    val events = remember {
//        mutableStateListOf<QuestShortInfo>(
//            QuestShortInfo(
//                "1",
//                "test",
//                "",
//                "",
//                1.0,
//                false,
//                listOf(),
//                false
//            ),
//            QuestShortInfo(
//                "2",
//                "test",
//                "",
//                "",
//                1.0,
//                true,
//                listOf(EventTag("id", "tag 1"), EventTag("id", "tag 2")),
//                true
//            )
//        )
//    }
//    val a = { str: String, b: Boolean -> str.forEach { _ -> } }
//    EduPlayTheme {
//        MyEventsScreen(
//            PaddingValues(),
//            a,
//            events,
//            events,
//            events,
//            {},
//            headers
//        )
//    }
//}