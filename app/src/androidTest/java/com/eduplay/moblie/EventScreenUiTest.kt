package com.eduplay.moblie

import android.content.res.Resources
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import coil3.network.NetworkHeaders
import com.eduplay.moblie.models.EventTag
import com.eduplay.moblie.ui.screens.EventScreen
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.SpyK
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
        val tags: SnapshotStateList<EventTag> = mutableStateListOf()
        val author=mutableStateOf("")
        val isCompleted = mutableStateOf(false)
        val cover: String = ""
        val info: SnapshotStateList<Pair<Int, String?>> = mutableStateListOf<Pair<Int, String?>>()
        val description = mutableStateOf("")
        val privateEvent = mutableStateOf(false)
        val isOpen = mutableStateOf(false)
        val isContinuing=mutableStateOf(false)
        val onAddToFavourite: () -> Unit = {}
        val onComplain: (String) -> Unit = {}
        val startEvent: () -> Unit = {}
        val showResults: () -> Unit = {}
        val onReturn: () -> Boolean = { false }
        val networkHeaders: State<NetworkHeaders> = mutableStateOf(NetworkHeaders.Builder().build())
        val isCompletionMode: State<Boolean> = mutableStateOf(false)
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

    val tags = mutableStateListOf(
        EventTag("id", "tag 1"), EventTag("id", "tag 2"), EventTag("id", "tag 3")
    )

    val info = mutableStateListOf<Pair<Int, String?>>(
        Pair(R.string.rating, 3.54.toString()),
        Pair(R.string.opens, LocalDateTime.now().toString()),
        Pair(R.string.closes, LocalDateTime.now().toString())
    )

    val description = """
        Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam id velit augue. Nunc sit amet nibh lacus. In pretium libero eu semper semper. Suspendisse euismod dignissim cursus. Phasellus egestas nulla orci, id fermentum erat posuere aliquet. Nullam vestibulum odio velit, in mattis est varius quis. Vestibulum tincidunt egestas sagittis. Aliquam non neque et neque congue finibus sit amet a nunc. Nulla facilisi. Donec ut lectus enim. Nunc ac suscipit arcu, sed molestie nisi. Duis ut nibh a quam commodo accumsan.

        Aenean laoreet venenatis orci id auctor. Maecenas accumsan libero eu ultricies bibendum. Aenean nec efficitur enim. Mauris pellentesque quam lorem, ut tincidunt augue tempus et. Mauris in ipsum fermentum, dictum elit sed, euismod turpis. Vestibulum molestie elementum lectus, vitae tempor sapien mollis vel. Nulla facilisi. Aenean ante mauris, pretium vitae tortor vel, cursus dictum libero. Ut consequat, dolor at laoreet dignissim, ipsum massa egestas odio, eu molestie diam dolor sed nunc. Sed eget justo nunc. Vestibulum iaculis sapien eget gravida sagittis. Donec scelerisque ullamcorper iaculis. Praesent eu consequat ante.
    """.trimIndent()

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
            toggleBluetooth= {},
        isCompetitionMode= eventData.isCompletionMode,
        canShowConnectionList= false
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

        composeTestRule.apply{
            onNodeWithTag("edit_dialog", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun check_top_bar_player_mode_only_edit_button_is_not_displayed() {
        composeTestRule.apply {
            every { eventData.eventCreatorMode } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithTag("report_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("download_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("favourite_btn", useUnmergedTree = true).assertIsDisplayed()

            onNodeWithTag("edit_btn", useUnmergedTree = true).assertDoesNotExist()

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
            every { eventData.tags } returns tags
            every { eventData.eventCreatorMode } returns mutableStateOf(false)
            every { eventData.info } returns info
            every { eventData.description } returns mutableStateOf(description)

            setContent {
                FillScreen()
            }

            for (tag in tags) {
                onNodeWithText(tag.name, useUnmergedTree = true).assertIsDisplayed()
            }

            for (item in info) {
                onNodeWithText(resources.getString(item.first), useUnmergedTree = true).assertIsDisplayed()
                onNodeWithText(item.second ?: "", useUnmergedTree = true).assertIsDisplayed()
            }
        }
    }

    @Test
    fun check_contains_general_info_for_creator() {
        composeTestRule.apply {
            every { eventData.tags } returns tags
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.info } returns info
            every { eventData.description } returns mutableStateOf(description)

            setContent {
                FillScreen()
            }

            for (tag in tags) {
                onNodeWithText(tag.name, useUnmergedTree = true).assertIsDisplayed()
            }

            for (item in info) {
                onNodeWithText(resources.getString(item.first), useUnmergedTree = true).assertIsDisplayed()
                onNodeWithText(item.second ?: "", useUnmergedTree = true).assertIsDisplayed()
            }
            onNodeWithText(resources.getString(R.string.private_event_flag), useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun check_privacy_setting_is_private_for_creator() {
        composeTestRule.apply {
            every { eventData.tags } returns tags
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.info } returns info
            every { eventData.description } returns mutableStateOf(description)
            every { eventData.privateEvent } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }

            onNodeWithText(resources.getString(R.string.private_event_flag), useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText(resources.getString(R.string.private_event), useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun check_privacy_setting_is_not_private_for_creator() {
        composeTestRule.apply {
            every { eventData.tags } returns tags
            every { eventData.eventCreatorMode } returns mutableStateOf(true)
            every { eventData.info } returns info
            every { eventData.description } returns mutableStateOf(description)
            every { eventData.privateEvent } returns mutableStateOf(false)

            setContent {
                FillScreen()
            }

            onNodeWithText(resources.getString(R.string.private_event_flag), useUnmergedTree = true).assertIsDisplayed()
            onNodeWithText(resources.getString(R.string.public_event), useUnmergedTree = true).assertIsDisplayed()
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
            onNodeWithText(resources.getString(R.string.start_event), useUnmergedTree = true).assertIsDisplayed()
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
            onNodeWithText(resources.getString(R.string.continue_event), useUnmergedTree = true).assertIsDisplayed()
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
    fun check_event_stats_are_displayed_creator_mode() {
        composeTestRule.apply {
            every { eventData.isCompleted } returns mutableStateOf(true)
            every { eventData.eventCreatorMode } returns mutableStateOf(true)

            setContent {
                FillScreen()
            }

            onNodeWithText(resources.getString(R.string.statistics), useUnmergedTree = true).performClick()
            onNodeWithText("Coming soon", useUnmergedTree = true).assertIsDisplayed()
        }
    }


}