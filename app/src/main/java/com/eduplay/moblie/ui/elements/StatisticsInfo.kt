package com.eduplay.moblie.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.GroupEditorStats
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.ResultStats
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.UserEditorStat
import com.eduplay.moblie.ui.theme.EduPlayTheme
import com.eduplay.moblie.ui.viewmodel.ImageHeaderInterface
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.ui.viewmodel.ThemeViewModel
import com.eduplay.moblie.useCases.AppSettingsManager
import com.eduplay.moblie.useCases.OfflineModeManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun StatisticsInfo(
    stats: State<ResultStats>,
    imageHeaderViewModel: ImageHeaderInterface = hiltViewModel<ImageHeaderViewModel>()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        if (stats.value.groupEvent) {
            var currentGroupIdx by remember { mutableIntStateOf(0) }
            var groupChoiceExpanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.9f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colorScheme.secondary, RoundedCornerShape(8.dp))
                        .padding(5.dp)
                        .clickable(true, onClick = { groupChoiceExpanded = !groupChoiceExpanded })
                ) {
                    Text(
                        text = stats.value.groups?.get(currentGroupIdx)?.name ?: "",
                        style = typography.labelLarge.copy(color = colorScheme.primary),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                    )
                    Icon(
                        if (groupChoiceExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        "",
                        tint = colorScheme.primary,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                DropdownMenu(
                    expanded = groupChoiceExpanded,
                    onDismissRequest = { groupChoiceExpanded = false },
                    containerColor = colorScheme.primaryContainer,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(6.dp)
                        .fillMaxWidth(0.9f)
                ) {
                    stats.value.groups?.forEachIndexed { idx, group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                currentGroupIdx = idx
                                groupChoiceExpanded = false
                            },
                            colors = MenuDefaults.itemColors(textColor = colorScheme.onPrimaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            TableOfUserResults(
                stats.value.groups?.get(currentGroupIdx)?.users ?: listOf(),
                imageHeaderViewModel
            )
        } else {
            TableOfUserResults(stats.value.users ?: listOf(), imageHeaderViewModel)
        }
    }
}

@Composable
private fun TableOfUserResults(
    users: List<UserEditorStat>,
    imageHeaderViewModel: ImageHeaderInterface
) {
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .scrollable(rememberScrollState(), orientation = Orientation.Vertical)
    ) {
        // header
        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell("№")
                for (idx in users.indices) {
                    TableCell((idx + 1).toString(), imageHeaderViewModel)
                }
            }
        }
        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell("user")
                for (idx in users.indices) {
                    TableCell(users[idx].username, imageHeaderViewModel, image = users[idx].avatar)
                }
            }
        }

        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell("answers")
                val answerCountText = StringBuilder()
                for (idx in users.indices) {
                    answerCountText.clear()
                        .append(users[idx].answers.correct)
                        .append("/")
                        .append(users[idx].answers.total)
                    TableCell(answerCountText.toString(), imageHeaderViewModel)
                }
            }
        }

        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell("points")
                for (idx in users.indices) {
                    TableCell(users[idx].points.toString(), imageHeaderViewModel)
                }
            }
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    imageHeaderViewModel: ImageHeaderInterface,
    image: String? = null,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .border(1.dp, color = colorScheme.outline)
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(vertical = 5.dp)
            .padding(horizontal = 10.dp)
    ) {
        if (image != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageHeaderViewModel.getFullUrl(image))
                    .httpHeaders(headers = imageHeaderViewModel.headers.value)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = text,
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
                    .padding(start = 3.dp)
                    .width(15.dp)
                    .height(15.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
                    .testTag("user_image")
            )
        }
        Text(
            text = text,
            style = typography.bodyLarge.copy(
                fontWeight = FontWeight.Normal,
                color = colorScheme.onBackground,
            ),
            modifier = Modifier
                .padding(horizontal = 5.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
private fun HeaderTableCell(text: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color = colorScheme.outline)
            .background(colorScheme.primary)
            .padding(vertical = 5.dp)
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = text,
            style = typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onPrimary,
            ),
            modifier = Modifier
                .padding(start = 3.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Composable
@Preview
fun TableOfUserResultsPreview() {
    val users = mutableListOf<UserEditorStat>(
        UserEditorStat(
            id = "user1",
            username = "user",
            answers = UserEditorStat.Answer(
                correct = 10,
                total = 100
            ),
            points = 450,
            avatar = ""
        )
    )
    for (i in 0..10) {
        users.add(
            UserEditorStat(
                id = "user1",
                username = "user",
                answers = UserEditorStat.Answer(
                    correct = 10,
                    total = 100
                ),
                points = 450,
                avatar = ""
            )
        )
    }
    val stat = ResultStats(
        true,
        null,
        groups = listOf(
            GroupEditorStats(
                id = "g1",
                name = "group 1",
                users = users
            ),
            GroupEditorStats(
                id = "g2",
                name = "group 2",
                users = users
            ),
            GroupEditorStats(
                id = "g3",
                name = "group 3",
                users = users
            )
        )
    )
    EduPlayTheme(
        object : ThemeViewModel {
            override fun getTheme(): Flow<AppSettingsManager.Themes> {
                return flowOf(AppSettingsManager.Themes.LIGHT)
            }
        }
    ) {
        StatisticsInfo(
            remember { mutableStateOf(stat) },
            object : ImageHeaderInterface {
                override fun getFullUrl(fileName: String): String {
                    return fileName
                }

                override val headers: MutableState<NetworkHeaders> =
                    remember { mutableStateOf(NetworkHeaders.EMPTY) }
                override val appMode: MutableState<Flow<OfflineModeManager.AppModes>> =
                    remember { mutableStateOf(flowOf(OfflineModeManager.AppModes.ONLINE)) }

            }
        )
    }
}