package com.eduplay.moblie

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.eduplay.moblie.ui.screens.BluetoothDeviceListScreen
import com.eduplay.moblie.ui.screens.EventScreen
import org.junit.After
import org.junit.Rule
import org.junit.Test

class BluetoothDeviceListScreenUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    val foundDevices: SnapshotStateMap<String, String?> = mutableStateMapOf()
    var connect: (String, () -> Unit) -> Unit = {_, _ ->}
    val devicesConnectionStatus: SnapshotStateMap<String, Boolean> = mutableStateMapOf()
    var onProceed: () -> Unit = {}
    var innerPaddingValues = PaddingValues()
    var onReturn: () -> Unit = {}
    val needLocation: State<Boolean> = mutableStateOf(false)
    val deviceNameTooLong: State<Boolean> = mutableStateOf(false)



    @Composable
    fun FillScreen() {
        BluetoothDeviceListScreen(
            foundDevices = foundDevices,
            connect = connect,
            devicesConnectionStatus = devicesConnectionStatus,
            onProceed = onProceed,
            innerPaddingValues = innerPaddingValues,
            onReturn = onReturn,
            needLocation = needLocation,
            deviceNameTooLong = deviceNameTooLong,
            onScanAgain = {}
        )
    }

    @After
    fun clearData() {
        foundDevices.clear()
        connect = {_, _ ->}
        devicesConnectionStatus.clear()
        onProceed = {}
        innerPaddingValues = PaddingValues()
        onReturn = {}
    }

    @Test
    fun check_found_devices_are_displayed() {
        composeTestRule.apply {
            val testDevices = mapOf(
                Pair("id_1", "name_1"),
                Pair("id_2", "name_2"),
                Pair("id_3", "name_3"),
            )
            foundDevices.putAll(testDevices)
            setContent {
                FillScreen()
            }

            for (device in testDevices.entries) {
                onNodeWithTag("device_button${device.key}", useUnmergedTree = true).assertIsDisplayed()
                onNodeWithTag("device_name${device.key}", useUnmergedTree = true).assertIsDisplayed()
            }
        }
    }

    @Test
    fun check_deviceName_is_displayed() {
        composeTestRule.apply {
            val testDevices = mapOf(
                Pair("id_1", "name_1"),
            )
            foundDevices.putAll(testDevices)

            setContent {
                FillScreen()
            }

            for (device in testDevices.entries) {
                onNodeWithTag("device_name${device.key}", useUnmergedTree = true).assertTextEquals(device.value)
            }
        }
    }

    @Test
    fun check_deviceCode_is_displayed_when_deviceName_is_null() {
        composeTestRule.apply {
            val testDevices = mapOf(
                Pair("id_1", null)
            )
            foundDevices.putAll(testDevices)

            setContent {
                FillScreen()
            }

            for (device in testDevices.entries) {
                onNodeWithTag("device_name${device.key}", useUnmergedTree = true).assertTextEquals(device.key)
            }
        }
    }

    @Test
    fun check_connected_icon_is_displayed_when_device_is_connected() {
        composeTestRule.apply {
            val testDevices = mapOf(
                Pair("id_1", null)
            )
            foundDevices.putAll(testDevices)
            val testStatuses = mapOf(
                Pair("id_1", true)
            )
            devicesConnectionStatus.putAll(testStatuses)
            setContent {
                FillScreen()
            }

            for (device in testDevices.entries) {
                onNodeWithTag("connected_icon${device.key}", useUnmergedTree = true).assertIsDisplayed()
            }
        }
    }
}