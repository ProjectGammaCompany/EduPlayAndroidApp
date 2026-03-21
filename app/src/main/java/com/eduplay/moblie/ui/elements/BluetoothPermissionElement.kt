package com.eduplay.moblie.ui.elements

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.eduplay.moblie.utils.hasPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothPermissionElement(
    askForPermissions: State<Boolean>
) {

    val bluetoothScanPermission =
        rememberPermissionState(permission = Manifest.permission.BLUETOOTH_SCAN)
    var askScanPermission by remember { mutableStateOf(false) }

    val bluetoothConnectPermission =
        rememberPermissionState(permission = Manifest.permission.BLUETOOTH_CONNECT)
    var askConnectPermission by remember { mutableStateOf(false) }

    val fineLocationPermission =
        rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    var askLocationPermission by remember { mutableStateOf(false) }

    val advertisePermission =
        rememberPermissionState(permission = Manifest.permission.BLUETOOTH_ADVERTISE)
    var askAdvertisePermission by remember { mutableStateOf(false) }

    LaunchedEffect(
        askScanPermission,
        askConnectPermission,
        askLocationPermission,
        askAdvertisePermission
    ) {
        if (askScanPermission) {
            bluetoothScanPermission.launchPermissionRequest()
            askScanPermission = false
        }
        if (askConnectPermission) {
            bluetoothConnectPermission.launchPermissionRequest()
            askConnectPermission = false
        }
        if (askLocationPermission) {
            fineLocationPermission.launchPermissionRequest()
            askLocationPermission = false
        }
        if (askAdvertisePermission) {
            advertisePermission.launchPermissionRequest()
            askAdvertisePermission = false
        }
    }

    val context = LocalContext.current
    if (askForPermissions.value) {
        askScanPermission = !context.hasPermission(bluetoothScanPermission)
        askConnectPermission = !context.hasPermission(bluetoothConnectPermission)
        askLocationPermission = !context.hasPermission(fineLocationPermission)
        askAdvertisePermission = !context.hasPermission(advertisePermission)
    }
}