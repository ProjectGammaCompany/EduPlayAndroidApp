package com.eduplay.moblie

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import coil3.network.NetworkHeaders
import com.eduplay.moblie.models.EventGroup
import com.eduplay.moblie.repository.responseTypes.JoinCodeInfo
import com.eduplay.moblie.repository.webrepository.responseTypes.EventEditorStats.ResultStats
import com.eduplay.moblie.ui.screens.EventScreen
import com.eduplay.moblie.ui.viewmodel.EventScreenViewModel
import com.eduplay.moblie.useCases.managers.OfflineModeManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class EventScreenUiTest {

    private class EventData {
        val innerPaddingValues: PaddingValues = PaddingValues()
        val eventCreatorMode = mutableStateOf(false)
        val isEventFavourite = mutableStateOf(false)
        val eventName = mutableStateOf("")
        val tags: SnapshotStateList<String> = mutableStateListOf()
        val author = mutableStateOf("")
        val isCompleted = mutableStateOf(false)
        val cover: String = ""
        val info: SnapshotStateList<Pair<Int, String?>> = mutableStateListOf<Pair<Int, String?>>()
        val description = mutableStateOf("")
        val privateEvent = mutableStateOf(false)
        val isOpen = mutableStateOf(false)
        val isContinuing = mutableStateOf(false)
        val onAddToFavourite: () -> Unit = {}
        val onComplain: (String) -> Unit = {}
        val startEvent: () -> Unit = {}
        val showResults: () -> Unit = {}
        val onReturn: () -> Boolean = { false }
        val networkHeaders: State<NetworkHeaders> = mutableStateOf(NetworkHeaders.Builder().build())
        val isCompletionMode: State<Boolean> = mutableStateOf(false)
        val password = mutableStateOf("")
        val groups = mutableStateListOf<EventGroup>()
        val eventId = ""
        val joinCode = mutableStateOf(JoinCodeInfo("", ""))
        val onDownload = {}
        val canDownLoad = mutableStateOf(false)
        val isRated = mutableStateOf(false)
        val onRate = { _: Int -> }
        val groupEvent = mutableStateOf(false)
        val downloadingEvents = mutableStateMapOf<String, String>()
        val downloadedEvents = mutableStateSetOf<String>()
        val isDownloaded = mutableStateOf(false)
        val onDeleteEvent = {}
        val failedToSendAnswers = mutableStateOf(false)
        val appMode: State<Flow<OfflineModeManager.AppModes>> =
            mutableStateOf(flowOf(OfflineModeManager.AppModes.ONLINE))
        val needsUpdate = mutableStateOf(false)
        val groupEditorStats = mutableStateOf<ResultStats>(
            ResultStats(
                groupEvent = false,
                users = null,
                groups = null
            )
        )
        val sortEventsByColumn = { _:EventScreenViewModel.EditorStatColumns, _:Boolean ->}
    }

    @get:Rule
    val composeTestRule = createComposeRule()

    @SpyK
    private lateinit var eventData: EventData

    @Before
    fun setUp() {
        eventData = EventData()
        MockKAnnotations.init(this)
    }


    var resources: Resources = getInstrumentation().targetContext.resources

    @Composable
    fun FillScreen() {
        EventScreen(
            innerPaddingValues = eventData.innerPaddingValues,
            eventCreatorMode = eventData.eventCreatorMode,
            isEventFavourite = eventData.isEventFavourite,
            eventName = eventData.eventName,
            tags = eventData.tags,
            author = eventData.author,
            isCompleted = eventData.isCompleted,
            cover = eventData.cover,
            info = eventData.info,
            description = eventData.description,
            privateEvent = eventData.privateEvent,
            isOpen = eventData.isOpen,
            isContinuing = eventData.isContinuing,
            onAddToFavourite = eventData.onAddToFavourite,
            onComplain = eventData.onComplain,
            startEvent = eventData.startEvent,
            showResults = eventData.showResults,
            onReturn = eventData.onReturn,
            headers = eventData.networkHeaders,
            toggleBluetooth = {},
            isCompetitionMode = eventData.isCompletionMode,
            canShowConnectionList = false,
            password = eventData.password,
            groups = eventData.groups,
            eventId = eventData.eventId,
            joinCode = eventData.joinCode,
            onDownload = eventData.onDownload,
            canDownLoad = eventData.canDownLoad,
            isRated = eventData.isRated,
            onRate = eventData.onRate,
            groupEvent = eventData.groupEvent,
            downloadingEvents = eventData.downloadingEvents,
            downloadedEvents = eventData.downloadedEvents,
            isDownloaded = eventData.isDownloaded,
            onDeleteEvent = eventData.onDeleteEvent,
            failedToSendAnswers = eventData.failedToSendAnswers,
            appMode = eventData.appMode,
            needsUpdate = eventData.needsUpdate,
            groupEditorStats = eventData.groupEditorStats,
            sortEventsByColumn = eventData.sortEventsByColumn
        )
    }

    @Test
    fun check_top_bar_event_creator_mode_only_edit_button_is_displayed() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }

            onNodeWithTag("report_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("download_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("favourite_btn", useUnmergedTree = true).assertDoesNotExist()

            onNodeWithTag("edit_btn", useUnmergedTree = true).assertIsDisplayed()

        }
    }

    @Test
    fun check_edit_button_onclick_displays_edit_dialog_creator_mode() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }
            onNodeWithTag("edit_btn", useUnmergedTree = true).performClick()

            onNodeWithTag("edit_dialog", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun check_top_bar_player_mode_report_btn_and_favourite_btn_are_displayed() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("report_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("favourite_btn", useUnmergedTree = true).assertIsDisplayed()

            onNodeWithTag("edit_btn", useUnmergedTree = true).assertDoesNotExist()

        }
    }

    @Test
    fun check_top_bar_player_mode_download_btn_is_displayed_when_can_download_is_true() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(false)
            every { eventData.canDownLoad } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }
            onNodeWithTag("download_btn", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun check_top_bar_player_mode_download_btn_is_not_displayed_when_can_download_is_false() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(false)
            every { eventData.canDownLoad } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }
            onNodeWithTag("download_btn", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun check_complain_button_onclick_displays_complain_dialog_player_mode() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }
            onNodeWithTag("report_btn", useUnmergedTree = true).performClick()

            onNodeWithTag("report_dialog", useUnmergedTree = true).assertIsDisplayed()

        }
    }

    @Test
    fun check_header_contains_info_for_all_users() {
        val eventTitle = "Event 1"
        composeTestRule.apply {
            every { eventData.eventName } returns mutableStateOf(eventTitle)


            setContent {
                FillScreen()
            }

            onNodeWithTag("event_image", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("event_title", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("event_title", useUnmergedTree = true).assertTextContains(eventTitle)
        }
    }

    @Test
    fun check_header_not_contains_author_in_event_creator_mode() {
        val author = "Author"
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.author } returns mutableStateOf(author)

            setContent {
                FillScreen()
            }

            onNodeWithTag("author", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun check_header_contains_author_in_event_player_mode() {
        val author = "Author"
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(false)
            every { eventData.author } returns mutableStateOf(author)

            setContent {
                FillScreen()
            }

            onNodeWithTag("author", useUnmergedTree = true).assertTextContains(author)
        }
    }

    @Test
    fun check_header_not_contains_completed_chip_in_event_creator_mode() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.isCompleted } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }

            onNodeWithTag("is_completed_chip", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun check_header_contains_completed_chip_in_event_player_mode_event_is_completed() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(false)
            every { eventData.isCompleted } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }

            onNodeWithTag("is_completed_chip", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun check_header_not_contains_completed_chip_in_event_player_mode_event_is_not_completed() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(false)
            every { eventData.isCompleted } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("is_completed_chip", useUnmergedTree = true).assertDoesNotExist()
        }
    }


    @Test
    fun check_contains_general_info_for_player() {
        composeTestRule.apply {
            val tags = mutableStateListOf("tag 1",  "tag 2",  "tag 3")
            val info = mutableStateListOf<Pair<Int, String?>>(
                Pair(R.string.rating, 3.54.toString()),
                Pair(R.string.opens, LocalDateTime.now().toString()),
                Pair(R.string.closes, LocalDateTime.now().toString())
            )
            every { eventData.tags } returns tags
            every { eventData.eventCreatorMode } returns mutableStateOf(false)
            every { eventData.info } returns info

            setContent {
                FillScreen()
            }

            for (tag in tags) {
                onNodeWithText(tag, useUnmergedTree = true).assertIsDisplayed()
            }

            for (item in info) {
                onNodeWithText(
                    resources.getString(item.first),
                    useUnmergedTree = true
                ).assertIsDisplayed()
                onNodeWithText(item.second ?: "", useUnmergedTree = true).assertIsDisplayed()
            }
        }
    }

    @Test
    fun check_contains_general_info_for_creator() {
        composeTestRule.apply {
            val tags = mutableStateListOf("tag 1", "tag 2", "tag 3")

            val info = mutableStateListOf<Pair<Int, String?>>(
                Pair(R.string.rating, 3.54.toString()),
                Pair(R.string.opens, LocalDateTime.now().toString()),
                Pair(R.string.closes, LocalDateTime.now().toString())
            )
            every { eventData.tags } returns tags
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.info } returns info

            setContent {
                FillScreen()
            }

            for (tag in tags) {
                onNodeWithText(tag, useUnmergedTree = true).assertIsDisplayed()
            }

            for (item in info) {
                onNodeWithText(
                    resources.getString(item.first),
                    useUnmergedTree = true
                ).assertIsDisplayed()
                onNodeWithText(item.second ?: "", useUnmergedTree = true).assertIsDisplayed()
            }
        }
    }

    @Test
    fun check_creator_mode_privacy_settings_section_tab_is_displayed_when_event_is_private_and_not_group() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.privateEvent } returns mutableStateOf(true)
            every { eventData.groupEvent } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithText(
                resources.getString(R.string.privacy_settings),
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun check_creator_mode_privacy_settings_section_tab_is_displayed_when_event_is_not_private_and_group() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.privateEvent } returns mutableStateOf(false)
            every { eventData.groupEvent } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }

            onNodeWithText(
                resources.getString(R.string.privacy_settings),
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun check_creator_mode_privacy_settings_section_tab_is_displayed_when_event_is__private_and_group() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.privateEvent } returns mutableStateOf(true)
            every { eventData.groupEvent } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }

            onNodeWithText(
                resources.getString(R.string.privacy_settings),
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun check_creator_mode_privacy_settings_section_tab_is_not_displayed_when_event_is_not_private_and_not_group() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.privateEvent } returns mutableStateOf(false)
            every { eventData.groupEvent } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithText(
                resources.getString(R.string.privacy_settings),
                useUnmergedTree = true
            ).assertDoesNotExist()
        }
    }


    @Test
    fun check_start_event_btn_is_not_displayed_when_event_is_not_open_player_mode() {
        composeTestRule.apply {
            every { eventData.isOpen } returns mutableStateOf(false)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("start_event_btn", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun check_results_btn_is_not_displayed_when_event_is_not_completed_player_mode() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(false)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("results_btn", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun check_start_event_btn_is_displayed_with_start_event_text_when_event_is_open_and_not_started_player_mode() {
        composeTestRule.apply {
            every { eventData.isOpen } returns mutableStateOf(true)
            every { eventData.isContinuing } returns mutableStateOf(false)
            every { eventData.isCompleted } returns mutableStateOf(false)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("start_event_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText(
                resources.getString(R.string.start_event),
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun check_start_event_btn_is_displayed_with_continue_event_text_when_event_is_open_and_started_player_mode() {
        composeTestRule.apply {
            every { eventData.isOpen } returns mutableStateOf(true)
            every { eventData.isContinuing } returns mutableStateOf(true)
            every { eventData.isCompleted } returns mutableStateOf(false)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("start_event_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText(
                resources.getString(R.string.continue_event),
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun check_start_event_btn_is_not_displayed_when_event_completed_player_mode() {
        composeTestRule.apply {
            every { eventData.isOpen } returns mutableStateOf(true)
            every { eventData.isCompleted } returns mutableStateOf(true)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("start_event_btn", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun check_results_btn_is_displayed_when_event_event_is_completed_player_mode() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(true)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("results_btn", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun creator_mode_check_event_stats_are_displayed_when_stats_section_is_clicked() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(true)
            every { eventData.eventCreatorMode } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }
            onNodeWithTag(
                resources.getString(R.string.statistics),
                useUnmergedTree = true
            ).performClick()

            onNodeWithTag("statistics_section", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("general_info", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("privacy_settings_section", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun creator_mode_check_event_privacy_settings_are_displayed_when_privacy_settings_section_is_clicked() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(true)
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.privateEvent } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }
            onNodeWithTag(
                resources.getString(R.string.privacy_settings),
                useUnmergedTree = true
            ).performClick()

            onNodeWithTag("statistics_section", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("general_info", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("privacy_settings_section", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun creator_mode_check_event_general_info_is_displayed_when_privacy_general_info_is_clicked() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(true)
            every { eventData.eventCreatorMode } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }
            onNodeWithTag(
                resources.getString(R.string.statistics),
                useUnmergedTree = true
            ).performClick()
            onNodeWithTag(
                resources.getString(R.string.general_info),
                useUnmergedTree = true
            ).performClick()

            onNodeWithTag("statistics_section", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("general_info", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("privacy_settings_section", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun player_mode_rate_bar_is_not_displayed_when_isRated_is_false_and_isCompleted_is_false() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(false)
            every { eventData.isRated } returns mutableStateOf(false)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("rate_bar", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun player_mode_rate_bar_is_not_displayed_when_isRated_is_true_and_isCompleted_is_false() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(false)
            every { eventData.isRated } returns mutableStateOf(true)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("rate_bar", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun player_mode_rate_bar_is_not_displayed_when_isRated_is_true_and_isCompleted_is_true() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(true)
            every { eventData.isRated } returns mutableStateOf(true)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("rate_bar", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun player_mode_rate_bar_is_displayed_when_isRated_is_false_and_isCompleted_is_true() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(true)
            every { eventData.isRated } returns mutableStateOf(false)
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("rate_bar", useUnmergedTree = true).assertIsDisplayed()
        }
    }




}