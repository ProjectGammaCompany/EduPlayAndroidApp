package com.eduplay.moblie.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.viewmodel.AuthViewModel.ForgotPasswordStatus

@Composable
fun ForgotPasswordForm(
    onGoBack: () -> Unit,
    forgotPasswordStatus: State<ForgotPasswordStatus>,
    correctEmail: State<Boolean>,
    correctCode: State<Boolean>,
    hasEmailErrors: (String) -> Boolean,
    hasPasswordErrors: (String) -> Boolean,
    arePasswordsIdentical: State<Boolean>,
    isPasswordSafe: State<Boolean>,
    onSendCode: (String) -> Unit,
    onCheckCode: (String) -> Unit,
    onSendNewPassword: (String, String) -> Unit,
) {
    Row(modifier = Modifier
        .padding(top = 15.dp)
        .fillMaxWidth()
        .wrapContentHeight()) {
        IconButton(
            onClick = onGoBack,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.go_back)
            )
        }
        Text(
            text = stringResource(R.string.password_reset),
            style = typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .testTag("form_header")
        )
    }
    when (forgotPasswordStatus.value) {
        ForgotPasswordStatus.ENTER_EMAIL -> EmailForm(onSendCode, correctEmail, hasEmailErrors)
        ForgotPasswordStatus.ENTER_CODE -> CodeForm(onCheckCode, correctCode)
        ForgotPasswordStatus.CHANGE_PASSWORD -> ChangePasswordForm(
            hasPasswordErrors,
            arePasswordsIdentical,
            isPasswordSafe,
            correctCode,
            onSendNewPassword
        )

        ForgotPasswordStatus.NONE -> {}
    }
}

@Composable
private fun EmailForm(
    onSendCode: (String) -> Unit,
    correctEmail: State<Boolean>,
    hasEmailErrors: (String) -> Boolean,
) {
    val email = rememberTextFieldState()
    OutlinedTextField(
        state = email,
        isError = hasEmailErrors(email.text.toString()) || !correctEmail.value,
        label = { Text(stringResource(R.string.email)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        lineLimits = TextFieldLineLimits.SingleLine,
        supportingText = {
            if (!correctEmail.value) {
                Text(stringResource(R.string.incorrect_email))
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = colorScheme.secondary,
            unfocusedContainerColor = colorScheme.background,
            focusedContainerColor = colorScheme.background,
            disabledContainerColor = colorScheme.tertiary,
            errorContainerColor = colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 15.dp)
            .testTag("forgot_password_email_field")
    )

    Button(
        onClick = { onSendCode(email.text.toString()) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .testTag("forgot_password_get_code_btn")
    ) {
        Text(
            text = stringResource(R.string.recieve_reset_code),
            style = TextStyle(color = colorScheme.onPrimary),
        )
    }
}

@Composable
private fun CodeForm(
    onCheckCode: (String) -> Unit,
    correctCode: State<Boolean>
) {
    val code = rememberTextFieldState()
    OutlinedTextField(
        state = code,
        label = { Text(stringResource(R.string.reset_code)) },
        lineLimits = TextFieldLineLimits.SingleLine,
        supportingText = {
            if (!correctCode.value) {
                Text(stringResource(R.string.incorrect_reset_code))
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = colorScheme.secondary,
            unfocusedContainerColor = colorScheme.background,
            focusedContainerColor = colorScheme.background,
            disabledContainerColor = colorScheme.tertiary,
            errorContainerColor = colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 15.dp)
            .testTag("reset_code_field")
    )

    Button(
        onClick = { onCheckCode(code.text.toString()) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .testTag("reset_password_btn")
    ) {
        Text(
            text = stringResource(R.string.check_reset_code),
            style = TextStyle(color = colorScheme.onPrimary),
        )
    }
}

@Composable
private fun ChangePasswordForm(
    hasPasswordErrors: (String) -> Boolean,
    arePasswordsIdentical: State<Boolean>,
    isPasswordSafe: State<Boolean>,
    isCodeValid: State<Boolean>,
    onUpdatePassword: (String, String) -> Unit
) {

    var passwordVisible by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }
    var repeatPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { it -> password = it },
        isError = hasPasswordErrors(password) || !arePasswordsIdentical.value,
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
            if (!isPasswordSafe.value) {
                Text(stringResource(R.string.incorrect_password))
            } else if (!arePasswordsIdentical.value) {
                Text(stringResource(R.string.passwords_not_same))
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = colorScheme.secondary,
            unfocusedContainerColor = colorScheme.background,
            focusedContainerColor = colorScheme.background,
            disabledContainerColor = colorScheme.tertiary,
            errorContainerColor = colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 15.dp)
            .testTag("reset_password_field")
    )
// repeat password field
    OutlinedTextField(
        value = repeatPassword,
        onValueChange = { it -> repeatPassword = it },
        isError = hasPasswordErrors(repeatPassword) || !arePasswordsIdentical.value,
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
            if (arePasswordsIdentical.value) {
                Text(stringResource(R.string.passwords_not_same))
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = colorScheme.secondary,
            unfocusedContainerColor = colorScheme.background,
            focusedContainerColor = colorScheme.background,
            disabledContainerColor = colorScheme.tertiary,
            errorContainerColor = colorScheme.background
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 30.dp)
            .testTag("reset_repeat_password_field")
    )
    if (!isCodeValid.value) {
        Text(stringResource(R.string.incorrect_reset_code))
    }
    Button(
        onClick = { onUpdatePassword(password, repeatPassword) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .testTag("update_password_btn")
    ) {
        Text(
            text = stringResource(R.string.update_password),
            style = TextStyle(color = colorScheme.onPrimary),
        )
    }

}