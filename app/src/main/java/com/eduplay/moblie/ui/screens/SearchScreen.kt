package com.eduplay.moblie.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
import com.eduplay.moblie.ui.elements.TryAgainLaterToast
import com.eduplay.moblie.ui.viewmodel.EventListViewModel
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.Flow

@Composable
fun SearchScreen(
    navController: NavController,
    innerPaddingValues: PaddingValues,
    viewModel: SearchViewModel = hiltViewModel(),
    eventListViewModel: EventListViewModel = hiltViewModel(),
    imageHeaderViewModel: ImageHeaderViewModel = hiltViewModel()
) {
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

    val onSearch = { tags: List<String>?,
                     decliningRating: Boolean,
                     active: Boolean,
                     favorites: Boolean,
                     title: String ->
        viewModel.searchEvents(
            tags,
            decliningRating,
            active,
            favorites,
            title
        )
    }
    val onEventClick = { eventId: String ->
        navController.navigate("event_screen/$eventId")
    }
    val onFavourite = { eventId: String, isFavorite: Boolean ->
        eventListViewModel.changeFavourite(
            eventId,
            isFavorite
        )
    }

    SearchScreen(
        innerPaddingValues,
        onSearch = onSearch,
        searchResults = viewModel.events,
        onEventClick = onEventClick,
        onFavourite = onFavourite,
        headers = imageHeaderViewModel.headers,
        imageUrl = { image: String -> imageHeaderViewModel.getFullUrl(image) },
        tags = viewModel.tags,
        noEventsFound = viewModel.didntFindEvents
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    innerPaddingValues: PaddingValues,
    onSearch: (List<String>?, Boolean, Boolean, Boolean, String) -> Unit,
    searchResults: State<Flow<PagingData<QuestShortInfo>>>,
    onEventClick: (String) -> Unit,
    onFavourite: (String, Boolean) -> Unit,
    headers: State<NetworkHeaders>,
    imageUrl: (String) -> String,
    tags: SnapshotStateList<String>,
    noEventsFound: State<Boolean>
) {
    var advancedSearch by rememberSaveable { mutableStateOf(true) }
    val chosenTags = remember { mutableStateSetOf<String>() }
    val isDecliningOrder = remember { mutableStateOf(false) }
    val isActive = remember { mutableStateOf(false) }
    val isFavorite = remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState()
    var searched by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(color = colorScheme.background)
            .padding(
                top = 0.dp,
                bottom = innerPaddingValues.calculateBottomPadding(),
                start = innerPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPaddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
            .fillMaxSize()

    ) {
        SearchBar(
            inputField = {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    SearchBarDefaults.InputField(
                        query = textFieldState.text.toString(),
                        onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                        onSearch = {
                            searched = true
                            onSearch(
                                chosenTags.toList(),
                                isDecliningOrder.value,
                                isActive.value,
                                isFavorite.value,
                                textFieldState.text.toString()
                            )
                        },
                        expanded = true,
                        onExpandedChange = { },
                        placeholder = { Text(stringResource(R.string.search_events)) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(R.string.search_events)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.primaryContainer
                        ),
                        leadingIcon = {
                            IconButton(onClick = { advancedSearch = !advancedSearch }) {
                                Icon(
                                    ImageVector.vectorResource(
                                        if (advancedSearch)
                                            R.drawable.filter_off
                                        else
                                            R.drawable.filter
                                    ),
                                    contentDescription = if (advancedSearch)
                                        stringResource(R.string.hide_filters)
                                    else
                                        stringResource(R.string.show_filters)
                                )
                            }
                        },
                        modifier = Modifier
                    )
                    if (advancedSearch) {
                        SwitchFilter(
                            isActive,
                            { isActive.value = it },
                            stringResource(R.string.active)
                        )
                        SwitchFilter(
                            isDecliningOrder,
                            { isDecliningOrder.value = it },
                            stringResource(R.string.declining_rate)
                        )
                        SwitchFilter(
                            isFavorite,
                            { isFavorite.value = it },
                            stringResource(R.string.favourite)
                        )
                        TagList(tags, chosenTags)
                    }

                }
            },
            colors = SearchBarDefaults.colors(
                containerColor = colorScheme.background,
                dividerColor = if (advancedSearch) colorScheme.primary else colorResource(android.R.color.transparent)
            ),
            expanded = true,
            onExpandedChange = { },
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            val eventsInfo = searchResults.value.collectAsLazyPagingItems()
            if (noEventsFound.value || eventsInfo.itemCount == 0 && searched) {
                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = stringResource(R.string.no_events_found),
                        style = typography.titleMedium
                            .copy(color = colorScheme.secondary)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.background)
                ) {
                    items(eventsInfo.itemCount) { position ->
                        val itemValue = eventsInfo[position]
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
    }
}

@Composable
private fun SwitchFilter(
    state: State<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    text: String
) {
    Row {
        Switch(
            checked = state.value,
            onCheckedChange = {
                onCheckedChange(it)
            },
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = colorScheme.secondary,
                uncheckedThumbColor = colorScheme.secondary,
                uncheckedTrackColor = colorScheme.background
            )
        )
        Text(
            text = text,
            style = typography.labelLarge,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 5.dp)
        )
    }
}

@Composable
private fun TagList(
    tags: SnapshotStateList<String>,
    chosenTags: SnapshotStateSet<String>
) {
    val onChooseTag = { tag: String ->
        if (chosenTags.contains(tag)) {
            chosenTags.remove(tag)
        } else {
            chosenTags.add(tag)
        }
    }
    FlowRow(Modifier.fillMaxWidth()) {
        tags.forEach { tag ->
            TextButton(
                onClick = { onChooseTag(tag) },
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(
                    text = tag,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = colorScheme.onPrimaryContainer,
                    style = typography.labelLarge.copy(color = colorScheme.onPrimaryContainer),
                    modifier = Modifier
                        .wrapContentWidth()
                        .background(colorScheme.primaryContainer, shape = RoundedCornerShape(8.dp))
                        .border(
                            if (chosenTags.contains(tag)) 4.dp else 1.dp,
                            colorScheme.tertiary,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)

                )
            }
        }
    }
}

//@Preview
//@Composable
//private fun SearchPrev() {
//    val headers = remember { mutableStateOf(NetworkHeaders.Builder().build()) }
//    val nothingB = { string: String, bool: Boolean -> }
//    EduPlayTheme {
//        SearchScreen(
//            innerPaddingValues = PaddingValues(),
//            onSearch = { _, _, _, _, _ -> },
//            searchResults = remember { mutableStateOf(flowOf()) },
//            onEventClick = {},
//            onFavourite = nothingB,
//            headers = headers,
//            imageUrl = { it },
//            tags = remember {
//                mutableStateListOf(
//                    "математика",
//                    "информатика",
//                    "алгебра",
//                    "тест",
//                    "инфо",
//                    "ии"
//                )
//            },
//            noEventsFound = remember { mutableStateOf(false) }
//        )
//    }
//}