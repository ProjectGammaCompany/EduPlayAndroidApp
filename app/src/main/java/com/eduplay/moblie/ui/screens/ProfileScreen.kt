package com.eduplay.moblie.ui.screens

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.eduplay.moblie.R
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.ui.elements.AuthScreenNavigator
import com.eduplay.moblie.ui.elements.NoInternetConnectionToast
import com.eduplay.moblie.ui.elements.NotificationElement
import com.eduplay.moblie.ui.viewmodel.ImageHeaderViewModel
import com.eduplay.moblie.ui.viewmodel.ProfileViewModel
import com.eduplay.moblie.useCases.AppSettingsManager
import com.eduplay.moblie.useCases.OfflineModeManager
import com.eduplay.moblie.useCases.OfflineModeManager.AppModes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Composable
fun ProfileScreen(
    innerPaddingValues: PaddingValues,
    navController: NavController,
    contentResolver: ContentResolver,
    viewModel: ProfileViewModel = hiltViewModel(),
    imageHeaderViewModel: ImageHeaderViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    if (viewModel.noInternet.value) {
        NoInternetConnectionToast()
    }
    if (viewModel.unauthorised.value) {
        AuthScreenNavigator(navController)
    }

    val updateEmail: (String) -> Unit = { newEmail: String ->
        viewModel.updateEmail(newEmail)
    }
    val hasEmailErrors = { email: String -> viewModel.checkEmail(email) }
    val onLogout = {
        viewModel.logout()
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
        imageHeaderViewModel::getFullUrl,
        isOffline = viewModel.isOffline,
        onToggleOffline = { isOffline: Boolean ->
            viewModel.toggleAppMode(
                isOffline,
                navController
            )
        },
        theme = viewModel.theme.value.collectAsState(AppSettingsManager.Themes.SYSTEM),
        onChooseTheme = viewModel::changeTheme,
        notifications = viewModel.notifications,
        onAvatarPicked = { uri: Uri -> viewModel.updateAvatar(uri, contentResolver, context) },
        navController = navController,
        viewModel::sendAnswers,
        viewModel.hasUnsentAnswers
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
    imageUrl: (String) -> String,
    isOffline: State<Flow<AppModes>>,
    onToggleOffline: (Boolean) -> Unit,
    theme: State<AppSettingsManager.Themes>,
    onChooseTheme: (AppSettingsManager.Themes) -> Unit,
    notifications: SnapshotStateList<NotificationData>,
    onAvatarPicked: (Uri) -> Unit,
    navController: NavController,
    onSyncAnswers: () -> Unit,
    hasUnsyncedAnswers: State<Boolean>
) {
    val currentMode = isOffline.value.collectAsState(AppModes.OFFLINE)
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
        val pickImage = remember { mutableStateOf(false) }

        val pickMediaLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                pickImage.value = false
                if (uri != null) {
                    onAvatarPicked(uri)
                }
            }
        if (pickImage.value) {
            pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 10.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl(avatar))
                    .httpHeaders(headers = headers.value)
                    .networkCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build(),
                contentDescription = email.value,
                placeholder = BrushPainter(
                    Brush.linearGradient(
                        listOf(
                            colorScheme.primary,
                            colorScheme.secondary,
                            colorScheme.tertiary
                        )
                    )
                ),
                error = BrushPainter(
                    Brush.linearGradient(
                        listOf(
                            colorScheme.primary,
                            colorScheme.secondary,
                            colorScheme.tertiary
                        )
                    )
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
                    .width(130.dp)
                    .height(130.dp)
                    .clip(CircleShape)
            )
            if (currentMode.value == AppModes.ONLINE) {
                IconButton(
                    onClick = { pickImage.value = true },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        Icons.Default.ImageSearch,
                        stringResource(R.string.change_avatar)
                    )
                }
            }

            // email
            Text(
                text = stringResource(R.string.profile_info),
                style = typography.titleLarge.copy(color = colorScheme.onBackground),
                modifier = Modifier.padding(bottom = 5.dp, top = 10.dp)
            )

            val showEditEmailField = remember { mutableStateOf(false) }
            if (showEditEmailField.value && currentMode.value == AppModes.ONLINE) {
                val newEmail = rememberTextFieldState(email.value)
                Row {
                    OutlinedTextField(
                        state = newEmail,
                        label = { Text(stringResource(R.string.email)) },
                        lineLimits = TextFieldLineLimits.SingleLine,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        isError = hasEmailErrors(newEmail.text.toString()),
                        trailingIcon = {
                            IconButton({ showEditEmailField.value = false })
                            {
                                Icon(
                                    Icons.Default.Cancel,
                                    stringResource(R.string.close),
                                    tint = colorScheme.secondary
                                )
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                    )
                    TextButton(
                        onClick = { updateEmail(newEmail.text.toString()) },
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = colorScheme.primaryContainer,
                            contentColor = colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            //.weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 5.dp)
                            .border(
                                1.dp,
                                color = colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .background(color = colorScheme.primaryContainer)
                    ) {
                        Text(
                            stringResource(R.string.edit_email),
                            style = typography.labelMedium, //.copy(colorScheme.secondary),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            } else {
                Row {
                    Text(
                        text = stringResource(R.string.email),
                        style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                            .copy(color = colorScheme.onBackground),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 5.dp)
                    )
                    Text(
                        text = email.value,
                        style = typography.bodyLarge.copy(color = colorScheme.onBackground),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 5.dp)
                    )
                    if (currentMode.value == AppModes.ONLINE) {
                        IconButton(
                            onClick = { showEditEmailField.value = true }
                        ) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.edit),
                                stringResource(R.string.edit_email)
                            )
                        }
                    }
                }
            }

            Settings(
                isOffline,
                onToggleOffline,
                theme,
                onChooseTheme,
                onSyncAnswers,
                hasUnsyncedAnswers
            )
            if (currentMode.value == AppModes.ONLINE) {
                LatestNotifications(notifications, navController)
            }

            val appMode = isOffline.value.collectAsState(AppModes.ONLINE)
            if (appMode.value == AppModes.ONLINE) {
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileTopBar() {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary,
            titleContentColor = colorScheme.onPrimaryFixed,
        ),
        title = {
            Text(stringResource(R.string.profile))
        }
    )
}

@Composable
private fun LatestNotifications(
    notifications: SnapshotStateList<NotificationData>,
    navController: NavController
) {
    Column {
        HorizontalDivider(
            color = colorScheme.primaryContainer,
            modifier = Modifier.padding(top = 5.dp)
        )
        TextButton(
            onClick = { navController.navigate("notifications") },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {
            Text(
                text = stringResource(R.string.all_notifications),
                style = typography.titleMedium.copy(color = colorScheme.onBackground),
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.AutoMirrored.Default.ArrowForwardIos,
                "",
                tint = colorScheme.onBackground
            )
        }
        if (notifications.isNotEmpty()) {
            for (notification in notifications) {
                NotificationElement(notification, navController)
            }
        } else {
            Text(
                stringResource(R.string.no_notifications),
                style = typography.bodyMedium.copy(colorScheme.secondary),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        HorizontalDivider(
            color = colorScheme.primaryContainer,
            modifier = Modifier.padding(top = 3.dp)
        )

    }
}

@Composable
private fun Settings(
    isOffline: State<Flow<OfflineModeManager.AppModes>>,
    onToggleOffline: (Boolean) -> Unit,
    theme: State<AppSettingsManager.Themes>,
    onChooseTheme: (AppSettingsManager.Themes) -> Unit,
    onSyncAnswers: () -> Unit,
    hasUnsyncedAnswers: State<Boolean>
) {
    HorizontalDivider(
        color = colorScheme.primaryContainer,
        modifier = Modifier.padding(top = 3.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            style = typography.titleMedium.copy(color = colorScheme.onBackground),
        )
        val offlineState = isOffline.value.collectAsState(OfflineModeManager.AppModes.ONLINE)
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.trun_offline),
                style = typography.bodyLarge
                    .copy(color = colorScheme.onBackground),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(5.dp)
                    .weight(1f)
            )
            Switch(
                checked = offlineState.value == OfflineModeManager.AppModes.OFFLINE,
                onCheckedChange = {
                    onToggleOffline(it)
                },
                modifier = Modifier
            )
        }

        // theme settings
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.theme),
                style = typography.bodyLarge
                    .copy(color = colorScheme.onBackground),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 5.dp)
                    .weight(1f)
            )
            val themeTypes = remember {
                mapOf(
                    Pair(AppSettingsManager.Themes.SYSTEM, R.string.system),
                    Pair(AppSettingsManager.Themes.LIGHT, R.string.light),
                    Pair(AppSettingsManager.Themes.DARK, R.string.dark),
                )
            }
            val themeExpanded = remember { mutableStateOf(false) }
            Box {
                Row(
                    modifier = Modifier
                        .border(1.dp, colorScheme.tertiary, RoundedCornerShape(8.dp))
                        .padding(5.dp)
                        .clickable(true, onClick = { themeExpanded.value = !themeExpanded.value })
                ) {
                    Text(
                        text = stringResource(themeTypes[theme.value]!!),
                        style = typography.labelLarge.copy(color = colorScheme.primary),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(end = 30.dp)
                    )
                    Icon(
                        if (themeExpanded.value) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        "",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                DropdownMenu(
                    expanded = themeExpanded.value,
                    onDismissRequest = { themeExpanded.value = false },
                    containerColor = colorScheme.primaryContainer
                ) {
                    themeTypes.forEach { themeType ->
                        DropdownMenuItem(
                            text = { Text(stringResource(themeType.value)) },
                            onClick = { onChooseTheme(themeType.key) },
                            colors = MenuDefaults.itemColors(
                                textColor = colorScheme.onPrimaryContainer,

                                )
                        )
                    }
                }
            }
        }


        if (offlineState.value == AppModes.ONLINE) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (hasUnsyncedAnswers.value) {
                    TextButton(
                        onClick = onSyncAnswers,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)

                    ) {
                        Text(
                            text = stringResource(R.string.send_event_results),
                            style = typography.bodyLarge
                                .copy(color = colorScheme.primary)
                        )
                    }
                    Text(
                        text = stringResource(R.string.need_to_send_results),
                        style = typography.bodyMedium
                            .copy(color = colorScheme.onBackground),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.all_answers_sent),
                        style = typography.bodyLarge
                            .copy(color = colorScheme.onBackground),
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun ProfilePreview() {
    //EduPlayTheme {
    ProfileScreen(
        PaddingValues(),
        {},
        remember { mutableStateOf("email") },
        { false },
        {},
        "",
        remember { mutableStateOf(NetworkHeaders.Builder().build()) },
        { it },
        remember { mutableStateOf(flowOf(OfflineModeManager.AppModes.ONLINE)) },
        {},
        remember { mutableStateOf(AppSettingsManager.Themes.SYSTEM) },
        { _ -> },
        remember { mutableStateListOf() },
        { _ -> },
        rememberNavController(),
        {},
        remember { mutableStateOf(false) },
    )
    //}
}