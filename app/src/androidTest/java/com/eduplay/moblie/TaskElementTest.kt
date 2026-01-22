package com.eduplay.moblie

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.elements.QuestListElement
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskElementTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @MockK
    lateinit var shortInfo: QuestShortInfo

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun check_tag_list_is_displayed_when_there_are_tags() {
        val tags = listOf("tag 1", "tag 2", "tag 3")
        every { shortInfo.isFavourite } returns false
        every { shortInfo.imageUrl } returns ""
        every { shortInfo.id } returns ""
        every { shortInfo.name } returns ""
        every { shortInfo.tags } returns tags
        every { shortInfo.isDownloaded } returns false
        every { shortInfo.rate } returns 5.00

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }
            for (tag in tags) {
                onNodeWithText(tag).assertIsDisplayed()
            }
        }
    }


    @Test
    fun check_downloaded_icon_is_not_displayed_when_event_is_not_downloaded() {
        every { shortInfo.isFavourite } returns false
        every { shortInfo.imageUrl } returns ""
        every { shortInfo.id } returns ""
        every { shortInfo.name } returns ""
        every { shortInfo.tags } returns listOf()
        every { shortInfo.isDownloaded } returns false
        every { shortInfo.rate } returns 5.00

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }
            onNodeWithTag("quest_element_isDownloaded", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun check_downloaded_icon_is_displayed_when_event_is_downloaded() {
        every { shortInfo.isFavourite } returns false
        every { shortInfo.imageUrl } returns ""
        every { shortInfo.id } returns ""
        every { shortInfo.name } returns ""
        every { shortInfo.tags } returns listOf()
        every { shortInfo.isDownloaded } returns true
        every { shortInfo.rate } returns 5.00

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }
            onNodeWithTag("quest_element_isDownloaded", useUnmergedTree = true).assertIsDisplayed()
        }
    }


    @Test
    fun check_not_favourite_icon_is_displayed_when_event_is_not_favourite() {
        every { shortInfo.isFavourite } returns false
        every { shortInfo.imageUrl } returns ""
        every { shortInfo.id } returns ""
        every { shortInfo.name } returns ""
        every { shortInfo.tags } returns listOf()
        every { shortInfo.isDownloaded } returns false
        every { shortInfo.rate } returns 5.00

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }

            onNodeWithTag(
                "quest_element_isNotFavourite",
                useUnmergedTree = true
            ).assertIsDisplayed()
            onNodeWithTag(
                "quest_element_is_favourite",
                useUnmergedTree = true
            ).assertDoesNotExist()
        }
    }

    @Test
    fun check_favourite_icon_is_displayed_when_event_is_favourite() {
        every { shortInfo.isFavourite } returns true
        every { shortInfo.imageUrl } returns ""
        every { shortInfo.id } returns ""
        every { shortInfo.name } returns ""
        every { shortInfo.tags } returns listOf()
        every { shortInfo.isDownloaded } returns false
        every { shortInfo.rate } returns 5.00

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }

            onNodeWithTag(
                "quest_element_isNotFavourite",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                "quest_element_is_favourite",
                useUnmergedTree = true
            ).assertIsDisplayed()

        }
    }

    @Test
    fun when_event_is_not_favourite_after_click_icon_changes_to_favourite() {
        every { shortInfo.isFavourite } returns false
        every { shortInfo.imageUrl } returns ""
        every { shortInfo.id } returns ""
        every { shortInfo.name } returns ""
        every { shortInfo.tags } returns listOf()
        every { shortInfo.isDownloaded } returns false
        every { shortInfo.rate } returns 5.00

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }

            onNodeWithTag("quest_element_favourite_btn", useUnmergedTree = true).performClick()

            onNodeWithTag(
                "quest_element_isNotFavourite",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                "quest_element_is_favourite",
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun when_event_is_favourite_after_click_icon_changes_to_not_favourite() {
        every { shortInfo.isFavourite } returns true
        every { shortInfo.imageUrl } returns ""
        every { shortInfo.id } returns ""
        every { shortInfo.name } returns ""
        every { shortInfo.tags } returns listOf()
        every { shortInfo.isDownloaded } returns false
        every { shortInfo.rate } returns 5.00

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }

            onNodeWithTag("quest_element_favourite_btn", useUnmergedTree = true).performClick()

            onNodeWithTag(
                "quest_element_isNotFavourite",
                useUnmergedTree = true
            ).assertIsDisplayed()
            onNodeWithTag(
                "quest_element_is_favourite",
                useUnmergedTree = true
            ).assertDoesNotExist()

        }
    }
}