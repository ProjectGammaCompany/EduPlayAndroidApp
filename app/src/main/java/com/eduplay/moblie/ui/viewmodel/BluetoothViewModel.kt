package com.eduplay.moblie.ui.viewmodel

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


@OptIn(ExperimentalAtomicApi::class)
class BluetoothViewModel(private val adapter: State<BluetoothAdapter?>): ViewModel() {

    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private val connectedDevices =  mutableMapOf<BluetoothDevice, BluetoothSocket>()
    private val connectedClients = mutableSetOf<BluetoothSocket>()

    // all devices bound and scanned; key - mac address; value - name
    val foundDevices = mutableStateMapOf<String, String?>()
    val isScanning = mutableStateOf(false)
    val isReceivingConnections = AtomicBoolean(false)

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    suspend fun discoverDevices(
        onScanFailed: () -> Unit
    ) {
        if (adapter.value == null) {
            onScanFailed()
            return
        }
        val scanner = adapter.value?.bluetoothLeScanner

        foundDevices.clear()

        foundDevices.putAll(
                        adapter.value?.bondedDevices
                ?.filter { device -> device != null }
                ?.map { device -> Pair(device.address, device.name) } ?: listOf()
        )


        if (scanner == null) {
            Log.d("SCAN", "no scanner")
            return
        }
        if (isScanning.value) {
            Log.d("SCAN", "is already scanning")
            return
        }

        isScanning.value = true

        val filters: List<ScanFilter?> = listOf()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
            .setNumOfMatches(ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT)
            .setReportDelay(0L)
            .build()

        val scanCallback: ScanCallback = object : ScanCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val device: BluetoothDevice? = result.device
                Log.d("SCANNER", device?.name ?: device?.address ?: "unknown")
                if (device != null && device.address != null)
                    foundDevices.put(device.address!!, device.name)
            }
        }
        startServerSocket()
        scanner.startScan(filters, scanSettings, scanCallback);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan() {

        val scanner = adapter.value?.bluetoothLeScanner
        if (scanner == null) {
            return
        }

        val scanCallback: ScanCallback = object : ScanCallback() {
        }
        scanner.stopScan(scanCallback)
        stopSocketConnection()
    }

    private var serverSocket: BluetoothServerSocket? = null
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun startServerSocket() {
        try {
            // Create a server socket
            serverSocket = adapter.value?.listenUsingRfcommWithServiceRecord("EduPlay", uuid)
        } catch (e: IOException) {
            Log.e("SOCKET_CREATE", e.message ?: e.toString())
        }

        if (serverSocket != null) {
            isReceivingConnections.store(true)
            var socket: BluetoothSocket?
            viewModelScope.launch (Dispatchers.IO) {
                while (isReceivingConnections.load()) {
                    try {
                        // Accept incoming connection
                        socket = serverSocket!!.accept()
                    } catch (e: IOException) {
                        Log.e("SOCKET_CREATE", e.message ?: e.toString())
                        break
                    }
                    // If a connection was accepted, handle the connection in a separate thread
                    if (socket != null) {
                        connectedClients.add(socket)
                        // Handle the connection in a separate thread
                        // For example, create a ConnectedThread to manage the connection
                        // (code for ConnectedThread is shown later)
                    }
                }
            }
        }
    }

    fun stopSocketConnection() {
        isReceivingConnections.store(false)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(
        address: String,
        onCouldNotConnect: ()->Unit
    ) {
        val device = adapter.value?.getRemoteDevice(address)
        if (device == null)
        {
            onCouldNotConnect()
            return
        }
        val bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
        bluetoothSocket.connect();
    }
}