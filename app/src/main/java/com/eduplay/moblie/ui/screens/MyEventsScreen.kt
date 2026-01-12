package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.eduplay.moblie.R
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.elements.QuestListElement
import com.eduplay.moblie.ui.viewmodel.EventListViewModel
import com.eduplay.moblie.ui.viewmodel.MyEventsViewModel

@Composable
fun MyEventsScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: MyEventsViewModel = hiltViewModel(),
    eventListViewModel: EventListViewModel = hiltViewModel()
) {
    val tabs = remember<List<Int>> {
        listOf<Int>(
            R.string.favourite,
            R.string.completed,
            R.string.my_created
        )
    }
    var selectedTabIdx by remember { mutableIntStateOf(0) }
    val onFavouriteToggle = { id: String, isFavourite: Boolean ->
        eventListViewModel.changeFavourite(id, isFavourite)
    }

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
                viewModel.favourite,
                navController,
                viewModel,
                MyEventsViewModel.ListType.FAVOURITE,
                onFavouriteToggle
            )

            1 -> ListOfEvents(
                viewModel.completed,
                navController,
                viewModel,
                MyEventsViewModel.ListType.COMPLETED,
                onFavouriteToggle
            )

            2 -> ListOfEvents(
                viewModel.created,
                navController,
                viewModel,
                MyEventsViewModel.ListType.CREATED,
                onFavouriteToggle
            )

            else -> Box {}
        }

    }

}

@Composable
private fun ListOfEvents(
    events: List<QuestShortInfo>,
    navController: NavController,
    viewModel: MyEventsViewModel,
    type: MyEventsViewModel.ListType,
    onFavouriteToggle: (String, Boolean) -> Unit
) {
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(events.size) { position ->
                val itemValue = events[position]
                val onEventClick = { navController.navigate("event_screen/" + itemValue.id) }
                QuestListElement(
                    itemValue,
                    onEventClick,
                    { onFavouriteToggle(itemValue.id, itemValue.isFavourite) },
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(top = 5.dp)
        ) {
            IconButton(onClick = { viewModel.getPrevPage(type) }) {
                Icon(Icons.Default.ChevronLeft, stringResource(R.string.previous_page))
            }
            IconButton(onClick = { viewModel.getNextPage(type) }) {
                Icon(Icons.Default.ChevronRight, stringResource(R.string.next_page))
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