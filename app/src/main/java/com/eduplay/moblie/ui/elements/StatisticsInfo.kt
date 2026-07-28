package com.eduplay.moblie.ui.elements

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.eduplay.moblie.R
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.ResultStats
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.SingleUserStat
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.UserFullEditorStat
import com.eduplay.moblie.ui.theme.Typography
import com.eduplay.moblie.ui.theme.danger
import com.eduplay.moblie.ui.theme.success
import com.eduplay.moblie.ui.theme.warning
import com.eduplay.moblie.ui.viewmodel.EventScreenViewModel.EditorStatColumns
import com.eduplay.moblie.ui.viewmodel.ImageHeaderInterface
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.ui.viewmodel.SingleUserStatViewModel
import com.eduplay.moblie.ui.viewmodel.SingleUserStatViewModel.DisplayOption
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StatisticsInfo(
    stats: State<ResultStats>,
    sortEventStatsByColumn: (EditorStatColumns, Boolean) -> Unit,
    eventId: String,
    getStat: (String, String)->Unit,
    blocks: StateFlow<SingleUserStat>,
    options: StateFlow<Map<String, List<SingleUserStatViewModel.DisplayOption>>>
) {
    var showPersonalStats by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .testTag("statistics_section")
    ) {
        val currentUserName = remember { mutableStateOf("") }
        val currentUserGroup = remember { mutableStateOf<String?>(null) }
        val onUserClick = { userId: String, userName: String, group: String? ->
            getStat(eventId, userId)
            currentUserName.value = userName
            currentUserGroup.value = group
            showPersonalStats = true
        }
        if (!showPersonalStats) {
            AllUserResults(stats, sortEventStatsByColumn, onUserClick)
        } else {
            PersonalStats(
                blocks,
                options,
                currentUserName,
                currentUserGroup,
                { showPersonalStats = false }
            )
        }
    }
}

@Composable
private fun AllUserResults(
    stats: State<ResultStats>,
    sortEventStatsByColumn: (EditorStatColumns, Boolean) -> Unit,
    onUserClick: (String, String, String?) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
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
                sortEventStatsByColumn,
                { userId: String, userName: String ->
                    onUserClick(userId, userName, stats.value.groups?.get(currentGroupIdx)?.name)
                }
            )
        } else {
            TableOfUserResults(
                stats.value.users ?: listOf(),
                sortEventStatsByColumn,
                { userId: String, userName: String ->
                    onUserClick(userId, userName, null)
                }
            )
        }
    }
}

@Composable
private fun TableOfUserResults(
    users: List<UserFullEditorStat>,
    sortEventStatsByColumn: (EditorStatColumns, Boolean) -> Unit,
    onUserClick: (String, String) -> Unit
) {
    var descendingSorting by remember { mutableStateOf(false) }
    var currentSortingColumn by remember { mutableStateOf(EditorStatColumns.USERNAME) }
    val onSort = {
        sortEventStatsByColumn(currentSortingColumn, descendingSorting)
    }
    val onHeaderClick = { column: EditorStatColumns ->
        if (currentSortingColumn == column) {
            descendingSorting = !descendingSorting
        } else {
            currentSortingColumn = column
        }
        onSort()
    }
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell("")
                for (idx in users.indices) {
                    TableCell((idx + 1).toString())
                }
            }
        }

        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell("")
                for (idx in users.indices) {
                    IndexTableCell(
                        users[idx].username,
                        { onUserClick(users[idx].id, users[idx].username) }
                    )
                }
            }
        }
        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell(
                    stringResource(R.string.players),
                    { onHeaderClick(EditorStatColumns.USERNAME) },
                    true,
                    currentSortingColumn == EditorStatColumns.USERNAME,
                    descendingSorting
                )
                for (idx in users.indices) {
                    TableCell(users[idx].username, image = users[idx].avatar)
                }
            }
        }

        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell(
                    stringResource(R.string.correct_answer_cnt),
                    { onHeaderClick(EditorStatColumns.CORRECT_ANSWERS) },
                    true,
                    currentSortingColumn == EditorStatColumns.CORRECT_ANSWERS,
                    descendingSorting
                )
                val answerCountText = StringBuilder()
                for (idx in users.indices) {
                    answerCountText.clear()
                        .append(users[idx].answers.correct)
                        .append("/")
                        .append(users[idx].answers.total)
                    TableCell(answerCountText.toString())
                }
            }
        }

        item {
            Column(Modifier.width(intrinsicSize = IntrinsicSize.Max)) {
                HeaderTableCell(
                    stringResource(R.string.points),
                    { onHeaderClick(EditorStatColumns.POINTS) },
                    true,
                    currentSortingColumn == EditorStatColumns.POINTS,
                    descendingSorting

                )
                for (idx in users.indices) {
                    TableCell(users[idx].points.toString())
                }
            }
        }
    }
    if (users.isEmpty()) {
        Text(
            text = stringResource(R.string.no_results_yet),
            style = Typography.labelLarge.copy(color = colorScheme.secondary)
        )
    }
}

@Composable
private fun IndexTableCell(
    userName: String,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .border(1.dp, color = colorScheme.outline)
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(vertical = 5.dp)
            .padding(horizontal = 10.dp)
            .height(40.dp)
    ) {
        IconButton(onClick) {
            Icon(
                ImageVector.vectorResource(R.drawable.visibility),
                "${stringResource(R.string.expand)} $userName",
                tint = colorScheme.secondary
            )
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    image: String? = null,
    imageHeaderViewModel: ImageHeaderInterface = hiltViewModel<ImageHeaderViewModel>()
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .border(1.dp, color = colorScheme.outline)
            .fillMaxWidth()
            .background(colorScheme.background)
            .padding(vertical = 5.dp)
            .padding(horizontal = 10.dp)
            .height(40.dp)
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
private fun HeaderTableCell(
    text: String,
    onClick: () -> Unit = {},
    allowSortingByHeader: Boolean = false,
    sortingByCell: Boolean = false,
    descendingSorting: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(true, onClick = onClick)
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
        if (allowSortingByHeader) {
            Icon(
                imageVector = if (sortingByCell) {
                    ImageVector.vectorResource(R.drawable.list_arrow)
                } else {
                    ImageVector.vectorResource(R.drawable.menu)
                },
                contentDescription = if (!sortingByCell) {
                    ""
                } else if (descendingSorting)
                    stringResource(R.string.descending)
                else stringResource(
                    R.string.ascending
                ),
                tint = colorScheme.onPrimary,
                modifier = Modifier
                    .rotate(if (descendingSorting) 180f else 0f)
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.menu),
                contentDescription = "",
                tint = colorScheme.onPrimary

            )
        }
    }
}

@Composable
private fun PersonalStats(
    blocksFlow: StateFlow<SingleUserStat>,
    optionsFlow: StateFlow<Map<String, List<DisplayOption>>>,
    userName: State<String>,
    groupName: State<String?>,
    onGoBack: () -> Unit
) {
    val blocks = blocksFlow.collectAsState()
    val options = optionsFlow.collectAsState()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row {
                IconButton(onGoBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.go_back)
                    )
                }

                Text(
                    text = userName.value,
                    style = typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(vertical = 3.dp)
                        .weight(1f)
                )
            }
        }
        item {
            if (groupName.value != null) {
                Text(
                    text = "${stringResource(R.string.group)}: ${groupName.value}",
                    style = typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary,
                    ),
                    modifier = Modifier.padding(vertical = 3.dp)
                )
            }

        }
        items(blocks.value.blocks) { block ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .border(1.dp, color = colorScheme.outline, RoundedCornerShape(10.dp))
                    .padding(3.dp)
            ) {
                // block title
                var blockExpanded by remember { mutableStateOf(false) }
                Row {
                    Text(
                        text = block.name,
                        style = typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onBackground,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)

                    )
                    IconButton(
                        onClick = { blockExpanded = !blockExpanded },
                    ) {
                        if (!blockExpanded) {
                            Icon(Icons.Default.ArrowDropDown, stringResource(R.string.collapse))
                        } else {
                            Icon(Icons.Default.ArrowDropUp, stringResource(R.string.expand))
                        }
                    }
                }
                if (blockExpanded) {
                    block.tasks.forEach { task ->
                        if (task.id.isNotBlank()) {
                            TaskAnswerStat(task, options.value[task.id])
                        } else {
                            EmptyTaskAnswerStat()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTaskAnswerStat() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .border(1.dp, color = colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {
        Text(
            stringResource(R.string.no_results),
            style = typography.bodyLarge.copy(
                color = colorScheme.onBackground,
            )
        )
    }
}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
private fun TaskAnswerStat(task: SingleUserStat.SingleUserTask, options: List<DisplayOption>?) {
    val context = LocalContext.current
    val statusStrings = mapOf<TaskAnswerStatus, Int>(
        Pair(TaskAnswerStatus.CORRECT, R.string.correct),
        Pair(TaskAnswerStatus.PARTIALLY, R.string.partialy_correct),
        Pair(TaskAnswerStatus.INCORRECT, R.string.incorrect),
    )

    val statusColors = mapOf<TaskAnswerStatus, Color>(
        Pair(TaskAnswerStatus.CORRECT, colorScheme.success),
        Pair(TaskAnswerStatus.PARTIALLY, colorScheme.warning),
        Pair(TaskAnswerStatus.INCORRECT, colorScheme.danger),
    )
    var taskExpanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .border(1.dp, color = colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {

        Row {
            Column(Modifier.weight(1f)) {
                Text(
                    task.name,
                    style = typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onBackground,
                    )
                )
                val textColor = colorScheme.onBackground
                val statusText = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = textColor
                        )
                    ) {
                        append(context.getText(R.string.answer_status))
                        append(": ")
                    }

                    withStyle(
                        SpanStyle(
                            color = statusColors[TaskAnswerStatus.valueByStatus(task.status)]!!
                        )
                    ) {
                        append(context.getText(statusStrings[TaskAnswerStatus.valueByStatus(task.status)]!!))
                    }
                }

                Text(statusText)
                Text(text = "${stringResource(R.string.points)}: ${task.userPoints}/${task.points}")
            }
            IconButton(
                onClick = { taskExpanded = !taskExpanded },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                if (!taskExpanded) {
                    Icon(Icons.Default.ArrowDropDown, stringResource(R.string.collapse))
                } else {
                    Icon(Icons.Default.ArrowDropUp, stringResource(R.string.expand))
                }
            }
        }

        if (taskExpanded) {
            when (TaskType.valueOf(task.type)) {
                TaskType.INFO -> {}
                TaskType.SINGLE_CHOICE -> OptionList(options)
                TaskType.MULTIPLE_CHOICE -> OptionList(options)
                TaskType.TEXT -> TextAnswer(task.options, task.userAnswers)
                TaskType.QR -> TextAnswer(task.options, task.userAnswers)
            }
        }
    }
}

@Composable
fun TextAnswer(correctOptions: List<SingleUserStat.StatOption>, answer: List<String>) {
    FlowRow(Modifier.padding(2.dp)) {
        Text(
            stringResource(R.string.player_answer),
            style = typography.bodyLarge.copy(colorScheme.onBackground)
        )
        Text(
            answer.first() ?: "",
            style = typography.bodyLarge.copy(colorScheme.onBackground)
        )
    }
    FlowRow(Modifier.padding(2.dp)) {
        Text(
            stringResource(R.string.correct_answer),
            style = typography.bodyLarge.copy(colorScheme.onBackground)
        )
        Text(
            correctOptions.first().value ?: "",
            style = typography.bodyLarge.copy(colorScheme.onBackground)
        )
    }
}

@Composable
private fun OptionList(options: List<DisplayOption>?) {
    if (options == null) {
        Text(stringResource(R.string.no_internet))
    } else {
        Column {
            options.forEach { option ->
                val optionColor =
                    if (option.isCorrect) {
                        if (option.isChosen) {
                            colorScheme.success
                        } else {
                            colorScheme.danger
                        }
                    } else {
                        if (option.isChosen) {
                            colorScheme.danger
                        } else {
                            colorScheme.primaryContainer
                        }
                    }
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 2.dp)
                        .fillMaxWidth()
                        .border(4.dp, shape = RoundedCornerShape(7.dp), color = optionColor)
                        .clip(RoundedCornerShape(7.dp))
                        .background(color = colorScheme.primaryContainer)
                ) {
                    Checkbox(
                        checked = option.isChosen,
                        enabled = false,
                        onCheckedChange = {},
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                    Text(
                        text = option.value,
                        style = typography.bodyMedium.copy(color = colorScheme.onBackground)
                            .copy(color = colorScheme.onPrimaryContainer),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 10.dp, top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}


//@Composable
//@Preview
//fun PersonalStatsPreview() {
//    val tasks = SingleUserStat.SingleUserTask(
//        id = "1",
//        name = "task1",
//        type = TaskType.SINGLE_CHOICE.optionNumber,
//        status = TaskAnswerStatus.PARTIALLY.status,
//        options = listOf(),
//        userAnswers = listOf(),
//        userPoints = 10,
//        points = 30
//    )
//    val blocks = SingleUserStat(
//        listOf(
//            SingleUserStat.SingleUserBlock(
//                id = "1",
//                name = "block",
//                tasks = listOf(tasks)
//            )
//        )
//    )
//    PersonalStats(
//        MutableStateFlow(blocks).asStateFlow()
//    )
//}