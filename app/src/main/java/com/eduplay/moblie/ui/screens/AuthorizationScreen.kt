package com.eduplay.moblie.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduplay.moblie.R
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.ui.viewmodel.AuthViewModel

@Composable
fun AuthorizationScreen(navController: NavController, viewModel: AuthViewModel = hiltViewModel()) {

    val context = LocalContext.current

    val emailPasswordText = stringResource(R.string.badEmailOrPassword)
    val noAccText = stringResource(R.string.no_registration)
    val noInternetText = stringResource(R.string.no_internet)
    val wrongEmailOrPassword = {
        Toast.makeText(context, emailPasswordText, Toast.LENGTH_LONG).show()
    }
    val noAccount = {
        Toast.makeText(context, noAccText, Toast.LENGTH_LONG).show()
    }
    val noInternet = {
        Toast.makeText(context, noInternetText, Toast.LENGTH_LONG).show()
    }
    if (viewModel.noInternetConnection.value == true) {
        noInternet()
    }
    when (viewModel.authResult.value) {
        null -> {}
        AuthResult.SUCCESSES -> navController.navigate("main_screen")
        AuthResult.INVALID_USER -> noAccount()
        AuthResult.INVALID_PASSWORD -> wrongEmailOrPassword()
    }
    AuthorizationScreen(
        { it:String -> viewModel.emailHasErrors(it) },
        { it:String -> viewModel.passwordHasErrors(it) },
        { email: String, password: String ->
            viewModel.submitLoginForm(
                email,
                password,
                wrongEmailOrPassword
            )
        },
        { email: String, password: String ->
            viewModel.submitRegisterForm(
                email,
                password,
                wrongEmailOrPassword
            )
        },
    )

}

@Composable
private fun AuthorizationScreen(
    emailHasErrors: (String) -> Boolean,
    passwordHasErrors: (String) -> Boolean,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String) -> Unit
) {
    var isLoginForm by remember { mutableStateOf(true) }
    val switchForms = {
        isLoginForm = !isLoginForm
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
                    .fillMaxHeight(0.8f)
            ) {
                Image(
                    painter = painterResource(R.drawable.eduplaylogo),
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
                        .wrapContentHeight()
                        .border(1.dp, colorScheme.tertiary, RoundedCornerShape(10.dp))
                        .padding(1.dp)
                        .verticalScroll(formScrollState)
                ) {
                    if (isLoginForm) {
                        EmailPasswordForm(
                            switchForms,
                            isEmailError = emailHasErrors,
                            isPasswordError = passwordHasErrors,
                            onSubmitForm = onLogin,
                            R.string.login,
                            R.string.register
                        )
                    } else {
                        EmailPasswordForm(
                            switchForms,
                            isEmailError = emailHasErrors,
                            isPasswordError = passwordHasErrors,
                            onSubmitForm = onRegister,
                            R.string.register,
                            R.string.login
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun EmailPasswordForm(
    switchForms: () -> Unit,
    isEmailError: (String) -> Boolean,
    isPasswordError: (String) -> Boolean,
    onSubmitForm: (String, String) -> Unit,
    mainButtonLabel: Int,
    switchButtonLabel: Int,

    ) {
    var passwordVisible by remember { mutableStateOf(false) }
    val email = rememberTextFieldState()
    var password by remember { mutableStateOf("") }
    Text(
        text = stringResource(mainButtonLabel),
        style = typography.headlineLarge,
        modifier = Modifier
            .padding(vertical = 30.dp)
    )

    // email field
    OutlinedTextField(
        state = email,
        isError = isEmailError(email.text.toString()),
        label = { Text(stringResource(R.string.email)) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email
        ),
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 15.dp)
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
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 30.dp)
    )

    //submit btn
    Button(
        onClick = { onSubmitForm(email.text.toString(), password) },
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(
            text = stringResource(mainButtonLabel),
            style = TextStyle(color = colorScheme.onPrimary)

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
    ) {
        Text(
            text = stringResource(switchButtonLabel),
            style = TextStyle(color = colorScheme.onBackground)

        )
    }

}

@Preview
@Composable
private fun auth() {
    AuthorizationScreen(
        {false},
        {false},
        {a, b->},
        {a, b->}
    )
}