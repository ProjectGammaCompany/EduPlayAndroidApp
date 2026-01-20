package com.eduplay.moblie.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.eduplay.moblie.R
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.elements.QuestListElement
import com.eduplay.moblie.ui.viewmodel.EventListViewModel
import com.eduplay.moblie.ui.viewmodel.MainScreenViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun MainScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: MainScreenViewModel = hiltViewModel(),
    eventListViewModel: EventListViewModel = hiltViewModel()
) {
    var noInternetConnection by remember { mutableStateOf(false) }

    val onEventClick = { eventId: String -> navController.navigate("event_screen/$eventId") }
    val onFavourite = { eventId: String, isFavorite: Boolean ->
        eventListViewModel.changeFavourite(
            eventId,
            isFavorite
        )
    }

    if (noInternetConnection) {
        NoInternetConnectionToast()
    }
    if (viewModel.unauthorised.value || eventListViewModel.unauthorised.value) {
        navController.navigate("auth_screen")
    }

    MainScreen(
        innerPaddingValues,
        viewModel.getEventList { noInternetConnection = true }.collectAsLazyPagingItems(),
        onEventClick,
        onFavourite
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScreen(
    innerPaddingValues: PaddingValues,
    events: LazyPagingItems<QuestShortInfo>,
    onEventClick: (String) -> Unit,
    onFavourite: (String, Boolean) -> Unit
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
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            navigationIcon = {
                IconButton(onClick = { }) { //TODO("меню на главном экане")
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = stringResource(R.string.app_menu)
                    )
                }
            },
            actions = {
                IconButton(onClick = { }) { //TODO("поиск")
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = stringResource(R.string.search_events)
                    )
                }
            },
            title = {
                Text(stringResource(R.string.app_name))
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(events.itemCount) { position ->
                val itemValue = events[position]
                if (itemValue != null) {
                    val onEventClick = { onEventClick(itemValue.id) }
                    QuestListElement(
                        itemValue,
                        onEventClick,
                        { isFavourite -> onFavourite(itemValue.id, isFavourite) }
                    )
                }
            }
        }

    }
}


@Preview
@Composable
fun MainScreenPreview() {
    val events = flowOf<PagingData<QuestShortInfo>>(
        PagingData.from(
            listOf<QuestShortInfo>(
                QuestShortInfo(
                    "1",
                    "test",
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
                    1.0,
                    true,
                    listOf("tag 1", "tag 2"),
                    true
                )
            )
        )
    ).collectAsLazyPagingItems()
    val nothing = {string:String -> string.forEach {  }}
    val nothingB = {string:String, bool: Boolean -> string.forEach {  }}
    MainScreen(
        PaddingValues(0.dp),
        events,
        nothing,
        nothingB
    )


}