package com.eduplay.moblie.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.elements.QuestListElement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(innerPaddingValues: PaddingValues) {
    val context = LocalContext.current

    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    val quests = remember {
        listOf(
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "Название квеста",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                false,
                listOf("tag 1", "funny", "long as hell tag", "o"),
                false
            )
            ,
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "Название квеста",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                false,
                listOf("tag 1", "funny", "long as hell tag", "o"),
                false
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "Название квеста",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                false,
                listOf("tag 1", "funny", "long as hell tag", "o"),
                false
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "Название квеста",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                false,
                listOf("tag 1", "funny", "long as hell tag", "o"),
                false
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "Название квеста",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                false,
                listOf("tag 1", "funny", "long as hell tag", "o"),
                false
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "Название квеста",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                false,
                listOf("tag 1", "funny", "long as hell tag", "o"),
                false
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "Название квеста",
                "some url",
                4.3333333,
                true,
                listOf("tag 1", "funny", "long as hell tag"),
                true
            ),
            QuestShortInfo(
                "id_funny",
                "The very funny name",
                "some url",
                4.3333333,
                false,
                listOf("tag 1", "funny", "long as hell tag", "o"),
                false
            )
        )
    }

    Column (
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
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = stringResource(R.string.app_menu)
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* do something */ }) {
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
            modifier = Modifier.fillMaxSize()
        ) {
            items(quests) { it ->
                QuestListElement(it, {}, {})
            }
        }
    }
    }


//@Preview
//@Composable
//fun MainScreenPreview() {
//    MainScreen()
//}