package com.eduplay.moblie.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    imageHeaderViewModel: ImageHeaderViewModel = hiltViewModel()
) {

    var noInternet by remember { mutableStateOf(false) }
    var gotProfile by remember { mutableStateOf(false) }
    val onNoInternet = { noInternet = true }
    { }
    if (!gotProfile) {
        viewModel.fetchProfileInfo(onNoInternet)
        gotProfile = true
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
        viewModel.email,
        hasEmailErrors,
        onLogout,
        viewModel.avatar.value,
        imageHeaderViewModel.headers,
        { image: String -> imageHeaderViewModel.getFullUrl(image) }
    )

}

@Composable
private fun ProfileScreen(
    innerPaddingValues: PaddingValues,
    updateEmail: (String) -> Unit,
    email: State<String>,
    hasEmailErrors: (String) -> Boolean,
    onLogout: () -> Unit,
    avatar: String,
    headers: State<NetworkHeaders>,
    imageUrl: (String) -> String
) {

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

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl(avatar))
                .httpHeaders(headers = headers.value)
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = email.value,
            placeholder = painterResource(R.drawable.eduplaylogo),
            error = painterResource(id = R.drawable.ic_launcher_background),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp)
                .width(130.dp)
                .clip(CircleShape)

        )

        // email
        Text(
            text = stringResource(R.string.profile_info),
            style = typography.titleLarge.copy(color = colorScheme.onBackground),
            modifier = Modifier.padding(bottom = 5.dp, top = 10.dp)
        )
        Row {
            Text(
                text = stringResource(R.string.email),
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                    .copy(color = colorScheme.onBackground),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 5.dp)
            )
//            if (!editEmail) {
            Text(
                text = email.value,
                style = typography.bodyLarge.copy(color = colorScheme.onBackground),
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
        remember { mutableStateOf("email") },
        { false },
        {},
        "",
        remember { mutableStateOf(NetworkHeaders.Builder().build()) },
        { it }
    )
}