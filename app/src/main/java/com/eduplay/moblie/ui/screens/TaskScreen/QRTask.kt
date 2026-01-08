package com.eduplay.moblie.ui.screens.TaskScreen

import android.Manifest
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.viewmodel.EventStageViewmodel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

//TODO("qr сделать так чтобы после согласия открывалась камера")

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun QRTask(
    hideSubmitBtn: () -> Unit,
    showSubmitBtn: () -> Unit,
    onScanQr: () -> Unit,
    viewModel: EventStageViewmodel
) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    var hasRequestedPermission by rememberSaveable { mutableStateOf(false) }
    var permissionRequestCompleted by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    var canScan by remember { mutableStateOf(true) }
    var answer by remember { mutableStateOf("") }

    LaunchedEffect(cameraPermissionState.status) {
        // Check if the permission state has changed after the request
        if (hasRequestedPermission) {
            permissionRequestCompleted = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.9f)
        ) {
            if (canScan) {
                hideSubmitBtn()
                Button(
                    enabled = !viewModel.disableTask.value,
                    onClick = {
                        when (val status = cameraPermissionState.status) {
                            is PermissionStatus.Granted -> {
                                onScanQr()
                            }
                            is PermissionStatus.Denied -> {
                                if (permissionRequestCompleted) {
                                    // Show rationale only after the permission request is completed
                                    if (status.shouldShowRationale) {
                                        cameraPermissionState.launchPermissionRequest()
                                        hasRequestedPermission = true
                                    }
                                } else {
                                    cameraPermissionState.launchPermissionRequest()
                                    hasRequestedPermission = true
                                }
                            }
                        }
                        if (cameraPermissionState.status.shouldShowRationale) {
                            cameraPermissionState.launchPermissionRequest()
                            hasRequestedPermission = true
                        }

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primaryContainer,
                        contentColor = colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .heightIn(100.dp, 150.dp)

                ) {
                    Text(
                        text = stringResource(R.string.scan_qr),
                        style = typography.headlineSmall
                    )
                }
                TextButton(
                    onClick = {
                        canScan = false
                    },
                    enabled = !viewModel.disableTask.value,
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.cant_scan),
                        style = typography.titleMedium
                    )
                }
            } else {
                showSubmitBtn()
                TextField(
                    value = answer,
                    enabled = !viewModel.disableTask.value,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.your_answer),
                            style = typography.bodyMedium
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = colorScheme.onBackground,
                        unfocusedTextColor = colorScheme.onBackground,
                        focusedContainerColor = colorScheme.background,
                        unfocusedContainerColor = colorScheme.background,
                        disabledTextColor = colorScheme.onPrimaryContainer,
                        disabledContainerColor = colorScheme.primaryContainer,
                        focusedPlaceholderColor = colorScheme.primary,
                        unfocusedPlaceholderColor = colorScheme.primary,
                        disabledPlaceholderColor = colorScheme.primary,
                    ),
                    textStyle = typography.bodyMedium
                        .copy(color = colorScheme.onSecondaryContainer),
                    onValueChange = {
                        answer = it
                        viewModel.answers.clear()
                        viewModel.answers.add(it)
                                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = colorScheme.primary,
                            shape = RoundedCornerShape(8.dp)
                        )
                )
                TextButton(
                    onClick = {
                        canScan = true
                    },
                    enabled = !viewModel.disableTask.value,
                    modifier = Modifier
                        .padding(top = 15.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.scan_qr),
                        style = typography.titleMedium
                    )
                }
            }
        }
    }
}