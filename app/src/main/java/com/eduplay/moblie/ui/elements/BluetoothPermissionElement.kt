package com.eduplay.moblie.ui.elements

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
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

    LaunchedEffect(askScanPermission) {
        if (askScanPermission) {
            bluetoothScanPermission.launchPermissionRequest()
            askScanPermission = false
        }
    }
    LaunchedEffect(askConnectPermission) {
        if (askConnectPermission) {
            bluetoothConnectPermission.launchPermissionRequest()
            askConnectPermission = false
        }
    }
    LaunchedEffect(askLocationPermission) {
        if (askLocationPermission) {
            fineLocationPermission.launchPermissionRequest()
            askLocationPermission = false
        }
    }
    LaunchedEffect(askAdvertisePermission) {
        if (askAdvertisePermission) {
            advertisePermission.launchPermissionRequest()
            askAdvertisePermission = false
        }
    }

    val context = LocalContext.current
    if (askForPermissions.value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            askScanPermission = !context.hasPermission(bluetoothScanPermission)
            askConnectPermission = !context.hasPermission(bluetoothConnectPermission)

            if (
                context.hasPermission(bluetoothScanPermission) &&
                context.hasPermission(bluetoothConnectPermission)
            ) {
                Log.d("BLUETOOTH_TEST", "permission granted")
            }
        } else {
            askLocationPermission = !context.hasPermission(fineLocationPermission)
        }
        askAdvertisePermission = !context.hasPermission(advertisePermission)
        if (context.hasPermission(fineLocationPermission)) {
            Log.d("BLUETOOTH_TEST", "permission granted")
        }
    }
}