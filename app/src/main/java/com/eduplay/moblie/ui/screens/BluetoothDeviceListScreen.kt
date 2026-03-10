package com.eduplay.moblie.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R

@Composable
fun BluetoothDeviceListScreen(
    foundDevices: SnapshotStateMap<String, String?>,
    connect: (String, () -> Unit) -> Unit,
    devicesConnectionStatus: SnapshotStateMap<String, Boolean>,
    onProceed: () -> Unit
) {
    val context = LocalContext.current
    var showCouldNotConnectToast by remember { mutableStateOf(false) }
    val failedDevices = remember { mutableStateListOf<String?>() }
    if (failedDevices.isNotEmpty()) {
        for (device in failedDevices) {
            Toast.makeText(
                context,
                stringResource(R.string.failed_to_connect) + (device ?: ""),
                Toast.LENGTH_LONG
            ).show()
            failedDevices.remove(device)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            stringResource(R.string.choose_bluetooth_device_header),
            style = typography.headlineMedium.copy(color = colorScheme.onBackground),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp)
        )
        Text(
            stringResource(R.string.choose_bluetooth_device_comment),
            style = typography.bodyMedium.copy(color = colorScheme.secondary),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp)
        )
        HorizontalDivider()
        LazyColumn {
            items(foundDevices.entries.toList()) {
                TextButton(
                    onClick = {
                        try {
                            connect(
                                it.key, { failedDevices.add(it.value) })
                        } catch (e: SecurityException) {
                            Log.e("CONNECT", e.message ?: "", e)
                        }
                    }, modifier = Modifier.fillMaxWidth()
                ) {
                    Row {
                        Text(
                            text = it.value ?: it.key, modifier = Modifier.weight(1f)
                        )
                        if (devicesConnectionStatus[it.value] ?: false) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.bluetooth),
                                contentDescription = stringResource(R.string.connected)
                            )
                        }
                    }
                }

            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    onProceed()
                }, modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 20.dp)
            ) {
                Text(stringResource(R.string.proceed))
            }
        }
    }
}

@Preview
@Composable
fun DeviceListPrew() {
    BluetoothDeviceListScreen(
        foundDevices = remember { mutableStateMapOf(Pair("qq", "Device 1"), Pair("", "Device 2")) },
        connect = { it, func -> },
        devicesConnectionStatus = remember { mutableStateMapOf(Pair("Device 1", true)) },
        onProceed = {})
}