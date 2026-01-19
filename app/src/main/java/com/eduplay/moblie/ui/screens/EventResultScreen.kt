package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.theme.EduPlayTheme
import com.eduplay.moblie.ui.viewmodel.EventResultsViewModel

@Composable
fun EventResultScreen(
    innerPaddingValues: PaddingValues,
    eventId: String,
    navController: NavController,
    viewModel: EventResultsViewModel = hiltViewModel()
) {
    val onExitScreen = {
        navController.navigate("event_screen/$eventId")
    }
    var gotResults by remember { mutableStateOf(false) }
    var noInternet by remember{mutableStateOf(false)}
    val onNoInternet = {noInternet = true}
    if (!gotResults) {
        viewModel.fetchResults(eventId, onNoInternet)
        gotResults = true
    }
    if (noInternet) {
        NoInternetConnectionToast()
    }

    EventResultScreen(
        innerPaddingValues,
        onExitScreen,
        viewModel.points.intValue
    )
}

@Composable
private fun EventResultScreen(
    innerPaddingValues: PaddingValues,
    onExitScreen: () -> Unit,
    points: Int
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

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .background(color = colorScheme.surface)
                .fillMaxSize()
                .weight(1f)
        ) {


            Text(
                text = stringResource(R.string.congratulation),
                style = typography.headlineMedium
                    .copy(color = colorScheme.onBackground),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            )
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(R.string.you_got) + ":",
                    style = typography.titleLarge
                        .copy(color = colorScheme.onBackground),
                )
                Text(
                    text = " $points ",
                    style = typography.titleLarge
                        .copy(color = colorScheme.onBackground)
                )
                Text(
                    text = stringResource(R.string.points),
                    style = typography.titleLarge
                        .copy(color = colorScheme.onBackground),
                )
            }
            Image(
                painter = painterResource(id = R.drawable.correct_answer),
                contentDescription = stringResource(R.string.correct),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .sizeIn(
                        minHeight = 200.dp,
                        minWidth = 200.dp,
                        maxWidth = 500.dp,
                        maxHeight = 500.dp
                    )

            )
        }

        Button(
            onClick = onExitScreen,
            modifier = Modifier
                .background(color = colorScheme.surface)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.9f)
        ) {
            Text(stringResource(R.string.ok))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultTopBar(onExitScreen: () -> Unit) {
    TopAppBar(
        title = {},
        navigationIcon = {
            IconButton(
                onClick = onExitScreen
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back)
                )
            }
        }
    )
}

@Preview
@Composable
private fun EventResultScreenPreview() {
    EduPlayTheme {
        EventResultScreen(PaddingValues(), {}, 30)
    }
}