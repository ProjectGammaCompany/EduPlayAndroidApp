package com.eduplay.moblie

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.eduplay.moblie.repository.responseTypes.Block
import com.eduplay.moblie.repository.responseTypes.ShortTask
import com.eduplay.moblie.ui.screens.ParallelBlockScreen
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class ParallelBlockScreenUiTest {

    val innerPaddingValues = PaddingValues()
    val onGoBack: () -> Unit = {}

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun check_task_is_clickable_when_isCompleted_is_false() {
        composeTestRule.apply{
            var clicked = false
            val onChooseTask = {_:String->clicked = true}
            val task = ShortTask(
                id = "t1",
                name = "t1",
                time = 0,
                isCompleted = false
            )
            val block = Block(
                id = "b1",
                name = "",
                tasks = listOf(task)
            )

            setContent {
                ParallelBlockScreen(
                    block,
                    onChooseTask,
                    innerPaddingValues,
                    onGoBack
                )
            }
            onNodeWithTag("task_${task.name}", useUnmergedTree = true).performClick()

            assertEquals(true, clicked)
        }
    }

    @Test
    fun check_task_is_not_clickable_when_isCompleted_is_true() {
        composeTestRule.apply{
            var clicked = false
            val onChooseTask = {_:String->clicked = true}
            val task = ShortTask(
                id = "t1",
                name = "t1",
                time = 0,
                isCompleted = true
            )
            val block = Block(
                id = "b1",
                name = "",
                tasks = listOf(task)
            )

            setContent {
                ParallelBlockScreen(
                    block,
                    onChooseTask,
                    innerPaddingValues,
                    onGoBack
                )
            }

            onNodeWithTag("task_${task.name}", useUnmergedTree = true).performClick()

            assertEquals(false, clicked)
        }
    }

    @Test
    fun check_time_is_displayed_when_time_is_not_0() {
        composeTestRule.apply{
            val task = ShortTask(
                id = "t1",
                name = "t1",
                time = 10,
                isCompleted = true
            )
            val block = Block(
                id = "b1",
                name = "",
                tasks = listOf(task)
            )

            setContent {
                ParallelBlockScreen(
                    block,
                    {},
                    innerPaddingValues,
                    onGoBack
                )
            }

            onNodeWithTag("time_${task.name}", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun check_time_is_not_displayed_when_time_is_0() {
        composeTestRule.apply{
            val task = ShortTask(
                id = "t1",
                name = "t1",
                time = 0,
                isCompleted = true
            )
            val block = Block(
                id = "b1",
                name = "",
                tasks = listOf(task)
            )

            setContent {
                ParallelBlockScreen(
                    block,
                    {},
                    innerPaddingValues,
                    onGoBack
                )
            }

            onNodeWithTag("time_${task.name}", useUnmergedTree = true).assertDoesNotExist()
        }
    }
}