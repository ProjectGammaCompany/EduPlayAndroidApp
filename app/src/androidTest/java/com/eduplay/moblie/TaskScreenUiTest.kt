package com.eduplay.moblie

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.eduplay.moblie.models.TaskType
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.StageType
import com.eduplay.moblie.repository.responseTypes.TaskAnswerStatus
import com.eduplay.moblie.repository.webrepository.responseTypes.Task
import com.eduplay.moblie.ui.screens.TaskScreen.TaskScreen
import com.eduplay.moblie.ui.viewmodel.EventStageViewModelInterface
import com.eduplay.moblie.useCases.DateConverter
import com.eduplay.moblie.useCases.FileDownloadStatus
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.flow.Flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class TaskScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val innerPaddingValues = PaddingValues()
    val eventId = ""
    val onGoBack = {}

    @SpyK
    var viewModel: EventStageViewModelInterface = object : EventStageViewModelInterface {
        override val fileStatusFlows: SnapshotStateMap<String, Flow<FileDownloadStatus>> =
            mutableStateMapOf()
        override val currentStageType: MutableState<StageType> =
            mutableStateOf(StageType.TASK)
        override var currentTask: MutableState<Task?> = mutableStateOf(null)
        override var currentBlock: MutableState<Block?> = mutableStateOf(null)
        override var taskStartTime: LocalDateTime = LocalDateTime.now()
        override val answers: SnapshotStateList<String> =
            mutableStateListOf("option 1", "option 2", "option 3")
        override val disableTask: MutableState<Boolean> = mutableStateOf(false)
        override val showResults: MutableState<Boolean> = mutableStateOf(false)
        override val correctAnswer: MutableList<String> =
            mutableStateListOf("option 1", "option 2", "option 3")
        override var points: Int? = null
        override var isAnswerCorrect: TaskAnswerStatus? = TaskAnswerStatus.CORRECT
        override val unauthorised: MutableState<Boolean> = mutableStateOf(false)
        override var bluetoothCallBack: (Int) -> Unit = {}
        override val noInternet: MutableState<Boolean> = mutableStateOf(false)
        override fun chooseTask(
            eventId: String,
            taskId: String
        ) {
        }

        override fun sendAnswer(eventId: String) {}

        override fun onDownloadFile(fileName: String, fileUri: String) {}

        override fun getNextStage(
            eventId: String,
            retry: Boolean
        ) {
        }

        override fun onOpenFile(fileUri: String, context: Context) {}
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Composable
    fun SetupScreen() {
        TaskScreen(
            innerPaddingValues = innerPaddingValues,
            eventId = eventId,
            viewModel = viewModel,
            onGoBack = onGoBack,
        )
    }

    @Test
    fun test_single_choice_task_is_displayed_when_task_type_is_single_choice() {
        composeTestRule.apply {
            val task = Task(
                id = "",
                blockId = "",
                name = "",
                description = "",
                type = TaskType.SINGLE_CHOICE.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
            )
            every { viewModel.currentTask }.returns(mutableStateOf(task))

            setContent {
                SetupScreen()
            }

            onNodeWithTag("single_choice_task", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("multiple_choice_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("qr_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("text_task", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun test_multiple_choice_task_is_displayed_when_task_type_is_multiple_choice() {
        composeTestRule.apply {
            val task = Task(
                id = "",
                blockId = "",
                name = "",
                description = "",
                type = TaskType.MULTIPLE_CHOICE.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
            )
            every { viewModel.currentTask }.returns(mutableStateOf(task))

            setContent {
                SetupScreen()
            }

            onNodeWithTag("single_choice_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("multiple_choice_task", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("qr_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("text_task", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun test_text_task_is_displayed_when_task_type_is_text() {
        composeTestRule.apply {
            val task = Task(
                id = "",
                blockId = "",
                name = "",
                description = "",
                type = TaskType.TEXT.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
            )
            every { viewModel.currentTask }.returns(mutableStateOf(task))

            setContent {
                SetupScreen()
            }

            onNodeWithTag("single_choice_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("multiple_choice_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("qr_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("text_task", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun test_qr_task_is_displayed_when_task_type_is_qr() {
        composeTestRule.apply {
            val task = Task(
                id = "",
                blockId = "",
                name = "",
                description = "",
                type = TaskType.QR.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
            )
            every { viewModel.currentTask }.returns(mutableStateOf(task))

            setContent {
                SetupScreen()
            }

            onNodeWithTag("single_choice_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("multiple_choice_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("qr_task", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("text_task", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun test_no_task_is_displayed_when_task_type_is_info() {
        composeTestRule.apply {
            val task = Task(
                id = "",
                blockId = "",
                name = "",
                description = "",
                type = TaskType.INFO.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
            )
            every { viewModel.currentTask }.returns(mutableStateOf(task))

            setContent {
                SetupScreen()
            }

            onNodeWithTag("single_choice_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("multiple_choice_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("qr_task", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("text_task", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun test_qr_task_first_scan_btn_is_displayed() {
        composeTestRule.apply {
            val task = Task(
                id = "",
                blockId = "",
                name = "",
                description = "",
                type = TaskType.QR.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
            )
            every { viewModel.currentTask }.returns(mutableStateOf(task))

            setContent {
                SetupScreen()
            }

            onNodeWithTag("scan_code_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("code_field", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun test_qr_task_when_cant_scan_btn_is_clicked_code_field_is_displayed() {
        composeTestRule.apply {
            val task = Task(
                id = "",
                blockId = "",
                name = "",
                description = "",
                type = TaskType.QR.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
            )
            every { viewModel.currentTask }.returns(mutableStateOf(task))

            setContent {
                SetupScreen()
            }
            onNodeWithTag("cant_scan_btn", useUnmergedTree = true).performClick()

            onNodeWithTag("scan_code_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("code_field", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun test_qr_task_when_can_scan_btn_is_paressed_scan_btn_is_displayed() {
        composeTestRule.apply {
            val task = Task(
                id = "",
                blockId = "",
                name = "",
                description = "",
                type = TaskType.QR.optionNumber,
                options = listOf(),
                files = listOf(),
                time = 0,
                timeStamp = DateConverter.convertToServerFormat(LocalDateTime.now())
            )
            every { viewModel.currentTask }.returns(mutableStateOf(task))

            setContent {
                SetupScreen()
            }
            onNodeWithTag("cant_scan_btn", useUnmergedTree = true).performClick()
            onNodeWithTag("can_scan_btn", useUnmergedTree = true).performClick()

            onNodeWithTag("scan_code_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("code_field", useUnmergedTree = true).assertDoesNotExist()
        }
    }

}