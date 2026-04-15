package com.eduplay.moblie

import android.content.res.Resources
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.eduplay.moblie.models.NotificationData.EndEventNotificationData
import com.eduplay.moblie.models.NotificationData.EndEventNotificationData.TimeLeft
import com.eduplay.moblie.models.NotificationData.FavoriteNotificationData
import com.eduplay.moblie.ui.elements.NotificationElement
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class NotificationElementUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val res: Resources? = getInstrumentation().targetContext.resources

    @Test
    fun element_displays_favorite_start_variant_when_notificationData_is_FavoriteNotificationData() {
        composeTestRule.apply {
            val data = FavoriteNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "",
                date = LocalDateTime.MIN
            )

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController()
                )
            }

            onNodeWithTag(
                "favorite_${data.notificationId}",
                useUnmergedTree = true
            ).assertIsDisplayed()
            onNodeWithTag(
                "ending_${data.notificationId}",
                useUnmergedTree = true
            ).assertDoesNotExist()
        }
    }

    @Test
    fun element_displays_event_ending_variant_when_notificationData_is_EndEventNotificationData() {
        composeTestRule.apply {
            val data = EndEventNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "",
                date = LocalDateTime.MIN,
                timeLeft = TimeLeft.DAY,
                notStartedFavorite = false
            )

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController()
                )
            }

            onNodeWithTag(
                "favorite_${data.notificationId}",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                "ending_${data.notificationId}",
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun favorite_start_variant_displays_delete_button_when_showDeleteButton_is_true() {
        composeTestRule.apply {
            val data = FavoriteNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "",
                date = LocalDateTime.MIN
            )

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = true
                )
            }

            onNodeWithTag(
                "fav_delete_${data.notificationId}",
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun favorite_start_variant_delete_button_triggers_onDelete() {
        composeTestRule.apply {
            var wasDeleted = false
            var notificationId = ""
            val onDelete = { id: String ->
                wasDeleted = true
                notificationId = id
            }
            val data = FavoriteNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "",
                date = LocalDateTime.MIN
            )

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = true,
                    onDelete = onDelete
                )
            }
            onNodeWithTag(
                "fav_delete_${data.notificationId}",
                useUnmergedTree = true
            ).performClick()

            assertEquals(true, wasDeleted)
            assertEquals(data.notificationId, notificationId)
        }
    }

    @Test
    fun favorite_start_variant_does_not_display_delete_button_when_showDeleteButton_is_false() {
        composeTestRule.apply {
            val data = FavoriteNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "",
                date = LocalDateTime.MIN
            )

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = false
                )
            }

            onNodeWithTag(
                "fav_delete_${data.notificationId}",
                useUnmergedTree = true
            ).assertDoesNotExist()
        }
    }

    @Test
    fun ending_event_variant_displays_delete_button_when_showDeleteButton_is_true() {
        composeTestRule.apply {
            val data = EndEventNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "",
                date = LocalDateTime.MIN,
                timeLeft = TimeLeft.DAY,
                notStartedFavorite = false
            )

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = true
                )
            }

            onNodeWithTag(
                "ending_delete_${data.notificationId}",
                useUnmergedTree = true
            ).assertIsDisplayed()
        }
    }

    @Test
    fun ending_event_variant_delete_button_triggers_onDelete() {
        composeTestRule.apply {
            var wasDeleted = false
            var notificationId = ""
            val onDelete = { id: String ->
                wasDeleted = true
                notificationId = id
            }
            val data = EndEventNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "",
                date = LocalDateTime.MIN,
                timeLeft = TimeLeft.DAY,
                notStartedFavorite = false
            )

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = true,
                    onDelete = onDelete
                )
            }

            onNodeWithTag(
                "ending_delete_${data.notificationId}",
                useUnmergedTree = true
            ).performClick()

            assertEquals(true, wasDeleted)
            assertEquals(data.notificationId, notificationId)
        }
    }

    @Test
    fun ending_event_variant_does_not_display_delete_button_when_showDeleteButton_is_false() {
        composeTestRule.apply {
            val data = EndEventNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "",
                date = LocalDateTime.MIN,
                timeLeft = TimeLeft.DAY,
                notStartedFavorite = false
            )

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = false
                )
            }

            onNodeWithTag(
                "ending_delete_${data.notificationId}",
                useUnmergedTree = true
            ).assertDoesNotExist()
        }
    }

    @Test
    fun ending_event_variant_title_when_notStartedFavorite_is_true() {

        composeTestRule.apply {
            val data = EndEventNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "The name",
                date = LocalDateTime.MIN,
                timeLeft = TimeLeft.DAY,
                notStartedFavorite = true
            )
            var expectedTitle = res?.getString(R.string.you_havent_started) ?: ""
            expectedTitle += " \"${data.eventName}\"."

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = false
                )
            }

            onNodeWithTag(
                "ending_title${data.notificationId}",
                useUnmergedTree = true
            ).assertTextContains(expectedTitle)
        }
    }

    @Test
    fun ending_event_variant_title_when_notStartedFavorite_is_false() {
        composeTestRule.apply {
            val data = EndEventNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "The name",
                date = LocalDateTime.MIN,
                timeLeft = TimeLeft.DAY,
                notStartedFavorite = false
            )
            var expectedTitle = "\"${data.eventName}\" "
            expectedTitle += res?.getString(R.string.event_is_ending_soon) ?: ""
            expectedTitle += "."

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = false
                )
            }

            onNodeWithTag(
                "ending_title${data.notificationId}",
                useUnmergedTree = true
            ).assertTextContains(expectedTitle)
        }
    }

    @Test
    fun ending_event_variant_description_when_timeLeft_is_DAY() {
        composeTestRule.apply {
            val data = EndEventNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "The name",
                date = LocalDateTime.MIN,
                timeLeft = TimeLeft.DAY,
                notStartedFavorite = false
            )
            var expectedDescription = "\"${data.eventName}\" " + res?.getString(R.string.is_ending) + " "
            expectedDescription += (res?.getString(R.string.tomorrow) ?: "") + "."

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = false
                )
            }

            onNodeWithTag(
                "ending_body${data.notificationId}",
                useUnmergedTree = true
            ).assertTextContains(expectedDescription)
        }
    }

    @Test
    fun ending_event_variant_description_when_timeLeft_is_HOUR() {
        composeTestRule.apply {
            val data = EndEventNotificationData(
                notificationId = "1",
                eventId = "1",
                eventName = "The name",
                date = LocalDateTime.MIN,
                timeLeft = TimeLeft.HOUR,
                notStartedFavorite = false
            )
            var expectedDescription = "\"${data.eventName}\" " + res?.getString(R.string.is_ending) + " "
            expectedDescription += (res?.getString(R.string.in_hour) ?: "") + "."

            setContent {
                NotificationElement(
                    notificationData = data,
                    navController = rememberNavController(),
                    showDeleteButton = false
                )
            }

            onNodeWithTag(
                "ending_body${data.notificationId}",
                useUnmergedTree = true
            ).assertTextContains(expectedDescription)
        }
    }


}