package com.eduplay.moblie.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduplay.moblie.BuildConfig
import com.eduplay.moblie.R
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.ui.elements.ForgotPasswordForm
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.viewmodel.AuthViewModel
import com.eduplay.moblie.ui.viewmodel.AuthViewModel.ForgotPasswordStatus
import com.eduplay.moblie.ui.viewmodel.AuthViewModel.ForgotPasswordStatus.NONE

@Composable
fun AuthorizationScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {

    if (viewModel.noInternetConnection.value) {
        NoInternetConnectionToast()
        viewModel.noInternetConnection.value = false
    }

    if (viewModel.authResult.value == AuthResult.SUCCESSES) {
        navController.navigate("main_screen") {
            navController.currentBackStackEntry?.destination?.route?.let {
                popUpTo(
                    it
                ) { inclusive = true }
            }
        }
    }

    AuthorizationScreen(
        { it: String -> viewModel.emailHasErrors(it) },
        { it: String -> viewModel.passwordHasErrors(it) },
        { email: String, password: String ->
            viewModel.submitLoginForm(
                email,
                password,

                )
        },
        { email: String, password: String, repeatPassword: String, agree: Boolean ->
            viewModel.submitRegisterForm(
                email,
                password,
                repeatPassword
            )
        },
        viewModel.authResult,
        onSendCode = viewModel::requestCode,
        onCheckCode = viewModel::checkCode,
        onSendNewPassword = viewModel::updatePassword,
        repeatPasswordError = viewModel.passwordsAreNotTheSame,
        gotToPrevChangePasswordStatus = viewModel::changePasswordGoBack,
        forgotPasswordStatus = viewModel.currentForgotStatusFormState,
        areChangePasswordsIdentical = viewModel.changePasswordsIdentical,
        isChangePasswordSafe = viewModel.changePasswordsCorrect,
        correctChangeEmail = viewModel.changePasswordEmailIsCorrect,
        correctCode = viewModel.changePasswordCodeIsCorrect,
        onForgotPassword = viewModel::setForgotStatusToFirstStep
    )

}

@Composable
fun AuthorizationScreen(
    emailHasErrors: (String) -> Boolean,
    passwordHasErrors: (String) -> Boolean,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, Boolean) -> Unit,
    authResult: State<AuthResult?>,
    onSendCode: (String) -> Unit,
    onCheckCode: (String) -> Unit,
    onSendNewPassword: (String, String) -> Unit,
    repeatPasswordError: State<Boolean>,
    gotToPrevChangePasswordStatus: () -> Unit,
    forgotPasswordStatus: State<ForgotPasswordStatus>,
    areChangePasswordsIdentical: State<Boolean>,
    isChangePasswordSafe: State<Boolean>,
    correctChangeEmail: State<Boolean>,
    correctCode: State<Boolean>,
    onForgotPassword: ()->Unit
) {
    var isLoginForm by remember { mutableStateOf(true) }
    val switchForms = {
        isLoginForm = !isLoginForm
    }

    var showForgotPasswordForm by remember { mutableStateOf(false) }
    val onForgotPassword = {
        onForgotPassword()
        showForgotPasswordForm = true
    }
    if (forgotPasswordStatus.value == NONE) {
        showForgotPasswordForm = false
    }

    val minViewWidth = 0.5f
    val maxViewWidth = 0.8f
    val formScrollState = rememberScrollState()
    Scaffold { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val formWidth = if (maxWidth > maxHeight) minViewWidth else maxViewWidth
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxHeight(0.9f)
            ) {
                Image(
                    painter = painterResource(
                        R.drawable.eduplay_logo_primary
                    ),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(0.8f)
                        .weight(0.3f)
                )
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,

                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(formWidth)
                        .fillMaxHeight(0.9f)
                        .border(1.dp, colorScheme.secondary, RoundedCornerShape(10.dp))
                        .padding(1.dp)
                        .verticalScroll(formScrollState)
                ) {
                    if (showForgotPasswordForm) {
                        ForgotPasswordForm(
                            forgotPasswordStatus = forgotPasswordStatus,
                            correctEmail = correctChangeEmail,
                            correctCode = correctCode,
                            hasEmailErrors = emailHasErrors,
                            hasPasswordErrors = passwordHasErrors,
                            arePasswordsIdentical = areChangePasswordsIdentical,
                            isPasswordSafe = isChangePasswordSafe,
                            onSendCode = onSendCode,
                            onCheckCode = onCheckCode,
                            onSendNewPassword = onSendNewPassword,
                            onGoBack = gotToPrevChangePasswordStatus
                        )
                    } else if (isLoginForm) {
                        LoginForm(
                            switchForms,
                            isEmailError = emailHasErrors,
                            isPasswordError = passwordHasErrors,
                            onSubmitForm = onLogin,
                            onForgotPassword = onForgotPassword,
                            authResult = authResult
                        )
                    } else {
                        RegistrationForm(
                            switchForms,
                            isEmailError = emailHasErrors,
                            isPasswordError = passwordHasErrors,
                            onRegister = onRegister,
                            authResult = authResult,
                            repeatPasswordIsWrong = repeatPasswordError,
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun LoginForm(
    switchForms: () -> Unit,
    isEmailError: (String) -> Boolean,
    isPasswordError: (String) -> Boolean,
    authResult: State<AuthResult?>,
    onSubmitForm: (String, String) -> Unit,
    onForgotPassword: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val email = rememberTextFieldState()
    var password by remember { mutableStateOf("") }

    Text(
        text = stringResource(R.string.login),
        style = typography.headlineLarge,
        modifier = Modifier
            .padding(vertical = 30.dp)
            .testTag("login_form_header")
    )

    // email field
    OutlinedTextField(
        state = email,
        isError = isEmailError(email.text.toString()),
        label = { Text(stringResource(R.string.email)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        lineLimits = TextFieldLineLimits.SingleLine,
        supportingText = {
            if (authResult.value == AuthResult.INCORRECT_EMAIL) {
                Text(stringResource(R.string.incorrect_email))
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 10.dp)
            .testTag("login_email_field")
    )

    // password field
    OutlinedTextField(
        value = password,
        onValueChange = { it -> password = it },
        isError = isPasswordError(password),
        label = { Text(text = stringResource(R.string.password)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        maxLines = 1,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = image, if (passwordVisible)
                        stringResource(R.string.hide_password)
                    else stringResource(R.string.show_password)
                )
            }
        },
        supportingText = {
            if (authResult.value == AuthResult.UNSAFE_PASSWORD) {
                Text(stringResource(R.string.incorrect_password))
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .testTag("login_password_field")
    )
    // forgot password btn
    Column(Modifier.fillMaxWidth()) {
        TextButton(
            onClick = onForgotPassword,
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 10.dp)
                .testTag("forgot_password_btn")
        ) {
            Text(
                text = stringResource(R.string.forgot_password),
                style = TextStyle(color = colorScheme.primary)
            )

        }
    }

    if (authResult.value == AuthResult.USER_NOT_FOUND) {
        Text(
            text = stringResource(R.string.no_registration),
        )
    }

    //submit btn
    Button(
        onClick = { onSubmitForm(email.text.toString(), password) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .testTag("login_btn")
    ) {
        Text(
            text = stringResource(R.string.login),
            style = TextStyle(color = colorScheme.onPrimary),
        )
    }

    //switch screens btn
    Button(
        onClick = switchForms,
        colors = ButtonColors(
            containerColor = colorScheme.background,
            contentColor = colorScheme.background,
            disabledContainerColor = colorScheme.background,
            disabledContentColor = colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(colorScheme.background)
            .padding(bottom = 20.dp)
            .testTag("switch_to_registration_btn")
    ) {
        Text(
            text = stringResource(R.string.register),
            style = TextStyle(color = colorScheme.onBackground),
        )
    }

}

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
private fun RegistrationForm(
    switchForms: () -> Unit,
    isEmailError: (String) -> Boolean,
    isPasswordError: (String) -> Boolean,
    onRegister: (String, String, String, Boolean) -> Unit,
    authResult: State<AuthResult?>,
    repeatPasswordIsWrong: State<Boolean>
) {
    val email = rememberTextFieldState()
    var passwordVisible by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var repeatPasswordVisible by remember { mutableStateOf(false) }
    Text(
        text = stringResource(R.string.register),
        style = typography.headlineLarge,
        modifier = Modifier
            .padding(vertical = 30.dp)
            .testTag("registration_form_header")
    )

    // email field
    OutlinedTextField(
        state = email,
        isError = isEmailError(email.text.toString()),
        label = { Text(stringResource(R.string.email)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        lineLimits = TextFieldLineLimits.SingleLine,
        supportingText = {
            if (authResult.value == AuthResult.INCORRECT_EMAIL) {
                Text(stringResource(R.string.incorrect_email))
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 10.dp)
            .testTag("registration_email_field")
    )

    // password field
    OutlinedTextField(
        value = password,
        onValueChange = { it -> password = it },
        isError = isPasswordError(password) || repeatPasswordIsWrong.value,
        label = { Text(text = stringResource(R.string.password)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        maxLines = 1,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = image, if (passwordVisible)
                        stringResource(R.string.hide_password)
                    else stringResource(R.string.show_password)
                )
            }
        },
        supportingText = {
            if (authResult.value == AuthResult.UNSAFE_PASSWORD) {
                Text(stringResource(R.string.incorrect_password))
            } else if (repeatPasswordIsWrong.value) {
                Text(stringResource(R.string.passwords_not_same))
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 10.dp)
            .testTag("registration_password_field")
    )
// repeat password field
    OutlinedTextField(
        value = repeatPassword,
        onValueChange = { it -> repeatPassword = it },
        isError = isPasswordError(repeatPassword) || repeatPasswordIsWrong.value,
        label = { Text(text = stringResource(R.string.password)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password
        ),
        maxLines = 1,
        visualTransformation = if (repeatPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (repeatPasswordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            IconButton(onClick = { repeatPasswordVisible = !repeatPasswordVisible }) {
                Icon(
                    imageVector = image, if (repeatPasswordVisible)
                        stringResource(R.string.hide_password)
                    else stringResource(R.string.show_password)
                )
            }
        },
        supportingText = {
            if (repeatPasswordIsWrong.value) {
                Text(stringResource(R.string.passwords_not_same))
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 5.dp)
            .testTag("registration_repeat_password_field")
    )

    var agreed by remember { mutableStateOf(false) }
    val linkColor = colorScheme.primary
    val context = LocalContext.current
    val agreementText = remember {
        buildAnnotatedString {
            append(context.getText(R.string.platform_agreement_part_1))

            withLink(
                LinkAnnotation.Url(
                    BuildConfig.PLATFORM_TERMS_URL,
                    TextLinkStyles(style = SpanStyle(color = linkColor))
                )
            ) {
                append(context.getText(R.string.platform_agreement_part_2_user_agreement))
            }
            append(context.getText(R.string.platform_agreement_part_3))
            withLink(
                LinkAnnotation.Url(
                    BuildConfig.PLATFORM_POLICY_URL,
                    TextLinkStyles(style = SpanStyle(color = linkColor))
                )
            ) {
                append(context.getText(R.string.platform_agreement_part_4))
            }
        }
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        Checkbox(
            checked = agreed,
            onCheckedChange = { agreed = !agreed },
            modifier = Modifier.testTag("agree_to_terms_checkBox")
        )
        Text(
            agreementText,
            style = typography.labelSmall
        )
    }

    //submit btn
    Button(
        onClick = { onRegister(email.text.toString(), password, repeatPassword, true) },
        enabled = agreed,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .testTag("register_btn")
    ) {
        Text(
            text = stringResource(R.string.register),
            style = TextStyle(color = colorScheme.onPrimary),
        )
    }

    //switch screens btn
    Button(
        onClick = switchForms,
        colors = ButtonColors(
            containerColor = colorScheme.background,
            contentColor = colorScheme.background,
            disabledContainerColor = colorScheme.background,
            disabledContentColor = colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .background(colorScheme.background)
            .padding(bottom = 20.dp)
            .testTag("switch_to_login_form_btn")
    ) {
        Text(
            text = stringResource(R.string.login),
            style = TextStyle(color = colorScheme.onBackground)

        )
    }
}