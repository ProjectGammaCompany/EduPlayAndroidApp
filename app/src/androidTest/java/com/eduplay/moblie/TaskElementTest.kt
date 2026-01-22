package com.eduplay.moblie

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.eduplay.moblie.models.QuestShortInfo
import com.eduplay.moblie.ui.elements.QuestListElement
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TaskElementTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @SpyK
    lateinit var shortInfo: QuestShortInfo

    val tags = listOf("tag 1", "tag 2", "tag 3")
    val eventName = "Name"
    val eventRate = 3.00

    @Before
    fun setUp() {
        shortInfo = QuestShortInfo(
            "",
            "",
            "",
            5.00,
            false,
            listOf(),
            false,
        )
        MockKAnnotations.init(this)
    }

    @Test
    fun check_eventName_is_displayed() {
        every { shortInfo.name } returns eventName

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }

            onNodeWithText(eventName, useUnmergedTree = true).assertIsDisplayed()

        }
    }

    @Test
    fun check_eventRate_is_displayed() {
        every { shortInfo.rate } returns eventRate

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }

            onNodeWithTag(
                "quest_element_rate",
                useUnmergedTree = true
            ).assertTextContains(String.format("%.2f⭐", eventRate))

        }
    }

    @Test
    fun check_tag_list_is_displayed_when_there_are_tags() {
        every { shortInfo.tags } returns tags

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }
            for (tag in tags) {
                onNodeWithText(tag, useUnmergedTree = true).assertIsDisplayed()
            }
        }
    }


    @Test
    fun check_downloaded_icon_is_not_displayed_when_event_is_not_downloaded() {

        every { shortInfo.isDownloaded } returns false

        composeTestRule.apply {
            setContent {
                QuestListElement(shortInfo, {}, {})
            }
            onNodeWithTag("quest_element_isDownloaded", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun check_downloaded_icon_is_displayed_when_event_is_downloaded() {
        every { shortInfo.isDownloaded } returns true

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