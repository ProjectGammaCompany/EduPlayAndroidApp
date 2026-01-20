package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    var noInternet by remember { mutableStateOf(false) }
    var gotProfile by remember { mutableStateOf(false) }
    val onNoInternet = { noInternet = true }
    val onFetchedData = { gotProfile = true }
    if (!gotProfile) {
        viewModel.fetchProfileInfo(onFetchedData, onNoInternet)
    }

    if (noInternet) {
        NoInternetConnectionToast()
    }
    if (viewModel.unauthorised.value) {
        AuthScreenNavigator(navController)
    }

    val updateEmail: (String) -> Unit = { newEmail: String ->
        viewModel.email.value = newEmail
    }
    val hasEmailErrors = { email: String -> viewModel.checkEmail(email) }
    val onLogout = {
        viewModel.logout(
            onNoInternet
        )
    }

    if (viewModel.canLogout.value) {
        AuthScreenNavigator(navController)
    }

    ProfileScreen(
        innerPaddingValues,
        updateEmail,
        viewModel.email.value,
        hasEmailErrors,
        onLogout
    )

}

@Composable
private fun ProfileScreen(
    innerPaddingValues: PaddingValues,
    updateEmail: (String) -> Unit,
    email: String,
    hasEmailErrors: (String) -> Boolean,
    onLogout: () -> Unit
) {
    var editEmail by remember { mutableStateOf(false) }
    var emailValue by remember { mutableStateOf(email) }

    Column(
        modifier = Modifier
            .padding(
                top = 0.dp, //innerPaddingValues.calculateTopPadding(),
                bottom = innerPaddingValues.calculateBottomPadding(),
                start = innerPaddingValues.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPaddingValues.calculateEndPadding(LayoutDirection.Ltr)
            )
            .fillMaxSize()
    ) {
        ProfileTopBar()

        // email
        Text(
            text = stringResource(R.string.profile_info),
            style = typography.titleLarge,
            modifier = Modifier.padding(bottom = 5.dp, top = 10.dp)
        )
        Row {
            Text(
                text = stringResource(R.string.email),
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )
//            if (!editEmail) {
            Text(
                text = emailValue,
                style = typography.bodyLarge,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )
//                IconButton(onClick = { editEmail = true }) {
//                    Icon(
//                        ImageVector.vectorResource(R.drawable.edit),
//                        stringResource(R.string.edit_email)
//                    )
//                }
//            } else {
//                OutlinedTextField(
//                    value = emailValue,
//                    onValueChange = { newEmail ->
//                        emailValue = newEmail
//                        updateEmail(newEmail)
//                    },
//                    maxLines = 1,
//                    isError = hasEmailErrors(emailValue),
//                    label = { Text(stringResource(R.string.email)) },
//                    keyboardOptions = KeyboardOptions(
//                        keyboardType = KeyboardType.Email
//                    ),
//                    modifier = Modifier
//                        .fillMaxWidth(0.9f)
//                        .padding(bottom = 15.dp)
//                )
//            }
        }



        OutlinedButton(
            onClick = { onLogout() },
            colors = ButtonColors(
                containerColor = colorScheme.errorContainer,
                contentColor = colorScheme.error,
                disabledContainerColor = colorScheme.errorContainer,
                disabledContentColor = colorScheme.error
            ),
            shape = RoundedCornerShape(5.dp),
            border = BorderStroke(1.dp, colorScheme.error),
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
        ) {
            Text(
                stringResource(R.string.logout),
                style = typography.labelLarge.copy(color = colorScheme.error)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primaryContainer,
            titleContentColor = colorScheme.primary,
        ),
        title = {
            Text(stringResource(R.string.profile))
        }
    )
}

@Composable
@Preview
fun ProfilePreview() {
    ProfileScreen(
        PaddingValues(),
        {},
        "email",
        { false },
        {}
    )
}