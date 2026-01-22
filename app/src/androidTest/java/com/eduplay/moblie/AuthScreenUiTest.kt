package com.eduplay.moblie

import android.content.res.Resources
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.eduplay.moblie.ui.screens.AuthorizationScreen
import io.mockk.MockKAnnotations
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class AuthScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    val passwordHasErrors = {false}
    val passwordHasNoErrors = {true}
    val emailHasErrors = {false}
    val emailHasNoErrors = {true}
    val register = "register"
    val login = "login"

    val res: Resources? = getInstrumentation().targetContext.resources

    fun empty(s1: String, s2: String) {

    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun afterInitialization_login_form_is_shown() {
        composeTestRule.apply {
            setContent { AuthorizationScreen(
                {passwordHasErrors()},
                {emailHasErrors()},
                {s1, s2 -> empty(s1, s2)},
                {s1, s2 -> empty(s1, s2)}
            ) }


            onNodeWithTag(
                "form_header",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.login) ?: login)
            onNodeWithTag(
                "main_btn_text",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.login) ?: login)
            onNodeWithTag(
                "secondary_btn_text",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.register) ?: register)
        }

    }

    @Test
    fun secondary_button_register_switches_login_form_to_registration_form() {
        composeTestRule.apply {
            setContent { AuthorizationScreen(
                {passwordHasErrors()},
                {emailHasErrors()},
                {s1, s2 -> empty(s1, s2)},
                {s1, s2 -> empty(s1, s2)}
            ) }

            onNodeWithTag(
                "secondary_btn",
                useUnmergedTree = true
            ).performClick()


            onNodeWithTag(
                "form_header",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.register) ?: register)
            onNodeWithTag(
                "main_btn_text",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.register) ?: register)
            onNodeWithTag(
                "secondary_btn_text",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.login) ?: login)
        }
    }

    @Test
    fun secondary_button_login_switches_registration_form_to_login_form() {
        composeTestRule.apply {
            setContent { AuthorizationScreen(
                {passwordHasErrors()},
                {emailHasErrors()},
                {s1, s2 -> empty(s1, s2)},
                {s1, s2 -> empty(s1, s2)}
            ) }

            onNodeWithTag(
                "secondary_btn",
                useUnmergedTree = true
            ).performClick()
            onNodeWithTag(
                "secondary_btn",
                useUnmergedTree = true
            ).performClick()


            onNodeWithTag(
                "form_header",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.login) ?: login)
            onNodeWithTag(
                "main_btn_text",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.login) ?: login)
            onNodeWithTag(
                "secondary_btn_text",
                useUnmergedTree = true
            ).assertTextEquals(res?.getString(R.string.register) ?: register)
        }
    }


    @Test
    fun afterInitialization_email_filed_is_displayed() {
        composeTestRule.apply {
            setContent {
                AuthorizationScreen(
                    { passwordHasErrors() },
                    { emailHasErrors() },
                    { s1, s2 -> empty(s1, s2) },
                    { s1, s2 -> empty(s1, s2) }
                )
            }

            onNodeWithTag("email_field").assertIsDisplayed()
        }
    }

    @Test
    fun afterInitialization_password_filed_is_displayed() {
        composeTestRule.apply {
            setContent {
                AuthorizationScreen(
                    { passwordHasErrors() },
                    { emailHasErrors() },
                    { s1, s2 -> empty(s1, s2) },
                    { s1, s2 -> empty(s1, s2) }
                )
            }

            onNodeWithTag("password_field").assertIsDisplayed()
        }
    }


}