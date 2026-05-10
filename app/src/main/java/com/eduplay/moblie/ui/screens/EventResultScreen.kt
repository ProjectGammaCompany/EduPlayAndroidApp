package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.eduplay.moblie.R
import com.eduplay.moblie.repository.responseTypes.PlayerStats
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.viewmodel.EventResultsViewModel
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel

@Composable
fun EventResultScreen(
    innerPaddingValues: PaddingValues,
    eventId: String,
    navController: NavController,
    viewModel: EventResultsViewModel = hiltViewModel()
) {
    val onExitScreen = {
        navController.popBackStack()
    }
    var gotResults by remember { mutableStateOf(false) }
    if (!gotResults) {
        viewModel.fetchResults(eventId)
        gotResults = true
    }
    if (viewModel.noInternetConnection.value) {
        NoInternetConnectionToast()
    }
    if (viewModel.unauthorised.value) {
        AuthScreenNavigator(navController)
    }

    EventResultScreen(
        innerPaddingValues,
        onExitScreen,
        viewModel.users,
        viewModel.groups
    )
}

@Composable
fun EventResultScreen(
    innerPaddingValues: PaddingValues,
    onExitScreen: () -> Boolean,
    users: SnapshotStateList<PlayerStats.StatUser>,
    groups: SnapshotStateList<PlayerStats.StatGroup>,
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
            .background(color = colorScheme.surface)
    ) {
        ResultTopBar(onExitScreen)

        LazyColumn (
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = colorScheme.surface)
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .weight(1f)
        ) {

            item {
                Text(
                    text = stringResource(R.string.congratulation),
                    style = typography.headlineMedium
                        .copy(color = colorScheme.onBackground),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                )
            }
            item {
                if (users.isNotEmpty()) {
                    UserList(users)
                } else if (groups.isNotEmpty()) {
                    Column {
                        groups.forEach { group ->
                        //items(groups.toList()) { group ->
                            Text(
                                text = group.name,
                                style = typography.titleLarge,
                                modifier = Modifier.testTag("groupName${group.id}")
                            )
                            UserList(group.users)
                            HorizontalDivider(color = colorScheme.secondary)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { onExitScreen() },
            modifier = Modifier
                .background(color = colorScheme.surface)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.9f)
        ) {
            Text(stringResource(R.string.ok))
        }
    }
}

@Composable
private fun UserList(
    users: List<PlayerStats.StatUser>,
    headersViewModel: ImageHeaderViewModel = hiltViewModel()
) {
    Column {
        users.forEach {
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(headersViewModel.getFullUrl(it.avatar ?: ""))
                        .httpHeaders(headersViewModel.headers.value)
                        .networkCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = it.username,
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
                        .testTag("quest_element_image")
                        .padding(horizontal = 3.dp)
                        .height(30.dp)
                        .width(30.dp)
                        .clip(_root_ide_package_.androidx.compose.foundation.shape.CircleShape)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = it.username + ":",
                    style = typography.titleLarge
                        .copy(color = colorScheme.onBackground, fontWeight = FontWeight.Medium),
                    modifier = Modifier.testTag("user_${it.id}")
                )
                Text(
                    text = " ${it.points} ",
                    style = typography.titleLarge
                        .copy(color = colorScheme.onBackground),
                    modifier = Modifier.testTag("points_${it.id}")
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultTopBar(onExitScreen: () -> Boolean) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = { onExitScreen() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        }
    )
}