package com.eduplay.moblie.ui.screens

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
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.viewmodel.AuthViewModel

@Composable
fun AuthorizationScreen(navController: NavController?, viewModel: AuthViewModel = hiltViewModel()) {
    val minViewWidth = 0.5f
    val maxViewWidth = 0.8f
    val logoImage = painterResource(R.drawable.eduplaylogo)
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var isLoginForm by remember { mutableStateOf(true) }
    val formScrollState = rememberScrollState()

    val switchForms = {
        isLoginForm = !isLoginForm
    }

    Scaffold { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val formWidth = if (maxWidth > maxHeight) minViewWidth else maxViewWidth
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxHeight(0.8f)
            ) {
                Image(
                    painter = logoImage,
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 5.dp)
                        .fillMaxWidth(0.8f)
                        .weight(0.5f)
                )
                Column(
                    verticalArrangement = Arrangement.Center,
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
                        LoginForm(colorScheme, typography, switchForms, viewModel)
                    } else {
                        RegistrationForm(colorScheme, typography, switchForms, viewModel)
                    }
                }
            }
        }

    }
}

@Composable
private fun LoginForm(
    colorScheme: ColorScheme,
    typography: Typography,
    switchForms: () -> Unit,
    viewModel: AuthViewModel
) {
    val login = rememberTextFieldState()
    val password = rememberTextFieldState()

    Text(
        text = stringResource(R.string.login),
        style = typography.headlineLarge,
        modifier = Modifier
            .padding(vertical = 30.dp)
    )

    // email field
    OutlinedTextField(
        state = login,
        label = { Text(stringResource(R.string.email)) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 15.dp)
    )

    // password field
    OutlinedTextField(
        state = password,
        label = { Text(text = stringResource(R.string.password)) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 30.dp)
    )

    //submit btn
    Button(
        onClick = { viewModel.submitLoginForm() },
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(
            text = stringResource(R.string.login),
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
            text = stringResource(R.string.register),
            style = TextStyle(color = colorScheme.onBackground)

        )
    }

}

@Composable
private fun RegistrationForm(
    colorScheme: ColorScheme,
    typography: Typography,
    switchForms: () -> Unit,
    viewModel: AuthViewModel
) {
    val login = rememberTextFieldState()
    val password = rememberTextFieldState()

    Text(
        text = stringResource(R.string.register),
        style = typography.headlineLarge,
        modifier = Modifier
            .padding(vertical = 30.dp)
    )

    // email field
    OutlinedTextField(
        state = login,
        label = { Text(stringResource(R.string.email)) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 15.dp)
    )

    // password field
    OutlinedTextField(
        state = password,
        label = { Text(text = stringResource(R.string.password)) },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(bottom = 30.dp)
    )

    //submit btn
    Button(
        onClick = { viewModel.submitRegisterForm() },
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Text(
            text = stringResource(R.string.register),
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
            text = stringResource(R.string.login),
            style = TextStyle(color = colorScheme.onBackground)
        )
    }
}
