package com.eduplay.moblie.utils

import android.content.Context
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus

@OptIn(ExperimentalPermissionsApi::class)
fun Context.hasPermission(permissionType: PermissionState): Boolean {
    return permissionType.status is PermissionStatus.Granted
}
