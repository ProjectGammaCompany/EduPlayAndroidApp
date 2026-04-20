package com.eduplay.moblie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.ui.screens.AuthorizationScreen
import com.eduplay.moblie.ui.viewmodel.AuthViewModel
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.SpyK
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class AuthScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private class AuthScreenData {
        fun emailHasErrors(str: String): Boolean = false
        fun passwordHasErrors(str: String): Boolean = false
        fun onLogin(str1: String, str2: String) {}
        fun onRegister(str1: String, str2: String, str3: String, bool: Boolean) {}
        val authResult = mutableStateOf<AuthResult>(AuthResult.SUCCESSES)
        fun onSendCode(str: String) {}
        fun onCheckCode(str: String) {}
        fun onSendNewPassword(str1: String, str2: String) {}
        val repeatPasswordError = mutableStateOf(false)
        fun gotToPrevChangePasswordStatus() {}
        val forgotPasswordStatus = mutableStateOf(AuthViewModel.ForgotPasswordStatus.NONE)
        val areChangePasswordsIdentical = mutableStateOf(false)
        val isChangePasswordSafe = mutableStateOf(false)
        val correctChangeEmail = mutableStateOf(false)
        val correctCode = mutableStateOf(false)
        fun onForgotPassword() {}
    }


    @SpyK
    private lateinit var data: AuthScreenData

    @Before
    fun setUp() {
        data = AuthScreenData()
        MockKAnnotations.init(this)
    }

    @Composable
    private fun SetUpScreen() {
        AuthorizationScreen(
            emailHasErrors = data::emailHasErrors,
            passwordHasErrors = data::passwordHasErrors,
            onLogin = data::onLogin,
            onRegister = data::onRegister,
            authResult = data.authResult,
            onSendCode = data::onSendCode,
            onCheckCode = data::onCheckCode,
            onSendNewPassword = data::onSendNewPassword,
            repeatPasswordError = data.repeatPasswordError,
            gotToPrevChangePasswordStatus = data::gotToPrevChangePasswordStatus,
            forgotPasswordStatus = data.forgotPasswordStatus,
            areChangePasswordsIdentical = data.areChangePasswordsIdentical,
            isChangePasswordSafe = data.isChangePasswordSafe,
            correctChangeEmail = data.correctChangeEmail,
            correctCode = data.correctCode,
            onForgotPassword = data::onForgotPassword
        )
    }

    @Test
    fun login_form_is_displayed_first_test() {
        composeTestRule.apply {

            setContent {
                SetUpScreen()
            }


            onNodeWithTag("login_form_header", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("login_email_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("login_password_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("forgot_password_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("login_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("switch_to_registration_btn", useUnmergedTree = true).assertIsDisplayed()

            onNodeWithTag("registration_form_header", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("registration_email_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag(
                "registration_password_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                "registration_repeat_password_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag("agree_to_terms_checkBox", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("register_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("switch_to_login_form_btn", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun registration_form_is_displayed_after_click_on_switch_to_registration_btn() {
        composeTestRule.apply {
            setContent {
                SetUpScreen()
            }


            onNodeWithTag("switch_to_registration_btn", useUnmergedTree = true).performClick()


            onNodeWithTag("login_form_header", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("login_email_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("login_password_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("forgot_password_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("login_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("switch_to_registration_btn", useUnmergedTree = true).assertDoesNotExist()

            onNodeWithTag("registration_form_header", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("registration_email_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("registration_password_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag(
                "registration_repeat_password_field",
                useUnmergedTree = true
            ).assertIsDisplayed()
            onNodeWithTag("agree_to_terms_checkBox", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("register_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("switch_to_login_form_btn", useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun login_form_is_displayed_after_click_on_switch_to_login_form_btn() {
        composeTestRule.apply {
            setContent {
                SetUpScreen()
            }


            onNodeWithTag("switch_to_registration_btn", useUnmergedTree = true).performClick()
            onNodeWithTag("switch_to_login_form_btn", useUnmergedTree = true).performClick()


            onNodeWithTag("login_form_header", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("login_email_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("login_password_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("forgot_password_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("login_btn", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("switch_to_registration_btn", useUnmergedTree = true).assertIsDisplayed()

            onNodeWithTag("registration_form_header", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("registration_email_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag(
                "registration_password_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                "registration_repeat_password_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag("agree_to_terms_checkBox", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("register_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("switch_to_login_form_btn", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun forgot_password_enter_email_stage_is_displayed_when_forgotPasswordStatus_is_ENTER_EMAIL() {
        composeTestRule.apply {
            every { data.forgotPasswordStatus } returns mutableStateOf(AuthViewModel.ForgotPasswordStatus.ENTER_EMAIL)


            setContent {
                SetUpScreen()
            }
            onNodeWithTag("forgot_password_btn", useUnmergedTree = true).performClick()


            onNodeWithTag("forgot_password_email_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag(
                "forgot_password_get_code_btn",
                useUnmergedTree = true
            ).assertIsDisplayed()

            onNodeWithTag("reset_code_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("reset_password_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("reset_password_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag(
                "reset_repeat_password_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag("update_password_btn", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun forgot_password_enter_code_stage_is_displayed_when_forgotPasswordStatus_is_ENTER_CODE() {
        composeTestRule.apply {
            every { data.forgotPasswordStatus } returns mutableStateOf(AuthViewModel.ForgotPasswordStatus.ENTER_CODE)


            setContent {
                SetUpScreen()
            }
            onNodeWithTag("forgot_password_btn", useUnmergedTree = true).performClick()


            onNodeWithTag("reset_code_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("reset_password_btn", useUnmergedTree = true).assertIsDisplayed()

            onNodeWithTag(
                "forgot_password_email_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                "forgot_password_get_code_btn",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag("reset_password_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag(
                "reset_repeat_password_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag("update_password_btn", useUnmergedTree = true).assertDoesNotExist()
        }
    }

    @Test
    fun forgot_password_change_password_enter_code_stage_is_displayed_when_forgotPasswordStatus_is_CHANGE_PASSWORD() {
        composeTestRule.apply {
            every { data.forgotPasswordStatus } returns mutableStateOf(AuthViewModel.ForgotPasswordStatus.CHANGE_PASSWORD)


            setContent {
                SetUpScreen()
            }
            onNodeWithTag("forgot_password_btn", useUnmergedTree = true).performClick()


            onNodeWithTag("reset_password_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("reset_repeat_password_field", useUnmergedTree = true).assertIsDisplayed()
            onNodeWithTag("update_password_btn", useUnmergedTree = true).assertIsDisplayed()

            onNodeWithTag("reset_code_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("reset_password_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag(
                "forgot_password_email_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                "forgot_password_get_code_btn",
                useUnmergedTree = true
            ).assertDoesNotExist()
        }
    }

    @Test
    fun non_forgot_password_stages_are_displayed_when_forgotPasswordStatus_is_NONE() {
        composeTestRule.apply {
            every { data.forgotPasswordStatus } returns mutableStateOf(AuthViewModel.ForgotPasswordStatus.NONE)


            setContent {
                SetUpScreen()
            }
            onNodeWithTag("forgot_password_btn", useUnmergedTree = true).performClick()


            onNodeWithTag("reset_password_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag(
                "reset_repeat_password_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag("update_password_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("reset_code_field", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag("reset_password_btn", useUnmergedTree = true).assertDoesNotExist()
            onNodeWithTag(
                "forgot_password_email_field",
                useUnmergedTree = true
            ).assertDoesNotExist()
            onNodeWithTag(
                "forgot_password_get_code_btn",
                useUnmergedTree = true
            ).assertDoesNotExist()
        }
    }

}