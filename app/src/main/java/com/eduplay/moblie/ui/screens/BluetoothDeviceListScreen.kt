package com.eduplay.moblie.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.eduplay.moblie.R
import com.eduplay.moblie.ui.theme.danger

@Composable
fun BluetoothDeviceListScreen(
    foundDevices: SnapshotStateMap<String, String?>,
    connect: (String, () -> Unit) -> Unit,
    devicesConnectionStatus: SnapshotStateMap<String, Boolean>,
    onProceed: () -> Unit,
    innerPaddingValues: PaddingValues,
    onReturn: () -> Unit,
    needLocation: State<Boolean>,
    deviceNameTooLong: State<Boolean>,
    onScanAgain: () -> Unit
) {
    val context = LocalContext.current
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
        modifier = Modifier
            .padding(innerPaddingValues)
            .padding(horizontal = 5.dp)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            IconButton(
                onClick = onReturn,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(0.dp)
                    .wrapContentHeight()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.go_back),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                )
            }
        }
        Text(
            stringResource(R.string.choose_bluetooth_device_header),
            style = typography.headlineMedium.copy(color = colorScheme.onBackground),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 10.dp)
        )
        Text(
            stringResource(R.string.choose_bluetooth_device_comment),
            style = typography.bodyMedium.copy(color = colorScheme.tertiary),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(10.dp)
        )
        if (needLocation.value) {
            Text(
                text = stringResource(R.string.ble_need_location),
                style = typography.bodyMedium.copy(
                    color = colorScheme.danger,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )
        }
        if (deviceNameTooLong.value) {
            Text(
                text = stringResource(R.string.ble_device_name_too_long),
                style = typography.bodyMedium.copy(
                    color = colorScheme.danger,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            )
        }
        if (needLocation.value || deviceNameTooLong.value) {
            TextButton(
                onScanAgain,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
            ) {
                Text(stringResource(R.string.scan_again))
            }
        }
        val infiniteTransition = rememberInfiniteTransition()
        val angle by infiniteTransition.animateFloat(
            initialValue = 0F, targetValue = 360F, animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing)
            )
        )
        HorizontalDivider(
            color = colorScheme.tertiary
        )

        Row(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 2.dp)
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.progress),
                contentDescription = "",
                tint = colorScheme.secondary,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .rotate(angle)
            )
            Text(
                stringResource(R.string.scanning_devices),
                style = typography.bodyMedium.copy(color = colorScheme.secondary)
            )
        }

        LazyColumn(
            Modifier
                .weight(4f)
                .testTag("bluetooth_device_list")
        ) {
            items(foundDevices.entries.toList()) {
                TextButton(
                    onClick = {
                        connect(it.key, { failedDevices.add(it.value) })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .border(1.dp, colorScheme.primary, shape = RoundedCornerShape(5.dp))
                        .testTag("device_button${it.key}")
                ) {
                    Row {
                        Text(
                            text = it.value ?: it.key,
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .testTag("device_name${it.key}")
                        )
                        if (devicesConnectionStatus[it.key] ?: false) {
                            Icon(
                                ImageVector.vectorResource(R.drawable.bluetooth),
                                contentDescription = stringResource(R.string.connected),
                                modifier = Modifier.testTag("connected_icon${it.key}")
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Button(
                onClick = {
                    onProceed()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 20.dp)
            ) {
                Text(stringResource(R.string.proceed))
            }
        }
    }
}