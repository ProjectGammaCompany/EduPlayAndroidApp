package com.eduplay.moblie.ui.viewmodel

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.useCases.BluetoothDataExchangeUseCase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi


@OptIn(ExperimentalAtomicApi::class, ExperimentalPermissionsApi::class)
class BluetoothViewModel(
    private val adapter: State<BluetoothAdapter?>,
    private val exchangeUseCase: BluetoothDataExchangeUseCase

) : ViewModel() {

    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")
    private val connectedDevices = mutableMapOf<BluetoothDevice, BluetoothSocket>()

    // all devices bound and scanned; key - mac address; value - name
    val foundDevices = mutableStateMapOf<String, String?>()
    val devicesConnectionStatus = mutableStateMapOf<String, Boolean>()
    val isScanning = mutableStateOf(false)
    val isReceivingConnections = AtomicBoolean(false)
    val devicesScore = mutableStateMapOf<String, Int>()
    var askForPermissions = mutableStateOf(false)

    fun discoverDevices(
        context: Context,
        onScanFailed: () -> Unit
    ) {
        if (adapter.value == null) {
            onScanFailed()
            return
        }
        val scanner = adapter.value?.bluetoothLeScanner

        foundDevices.clear()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForPermissions.value = true
            return
        }
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

                Log.d(
                    "SCANNER",
                    device?.name ?: result.scanRecord?.deviceName ?: device?.address ?: "unknown"
                )
                if (device != null && device.address != null && device.name != null)
                    foundDevices.put(device.address!!, device.name ?: result.scanRecord?.deviceName)
            }
        }
        startServerSocket()
        scanner.startScan(filters, scanSettings, scanCallback)
        advertise()
    }

    fun stopScan(context: Context) {
        val scanner = adapter.value?.bluetoothLeScanner
        if (scanner == null) {
            return
        }

        val scanCallback: ScanCallback = object : ScanCallback() {
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        scanner.stopScan(scanCallback)
        stopSocketConnection()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    private fun advertise() {
        val advertiser =
            adapter.value?.bluetoothLeAdvertiser
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(true)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .addServiceUuid(ParcelUuid(uuid))
            .build()

        val callback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.e("advert", "cant start")
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
                Log.e("advert", "start")
            }
        }

        advertiser?.startAdvertising(settings, data, callback)
    }

    private var serverSocket: BluetoothServerSocket? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun startServerSocket() {
        try {
            // Create a server socket
            serverSocket = adapter.value?.listenUsingRfcommWithServiceRecord("EduPlay", uuid)
        } catch (e: IOException) {
            Log.e("SOCKET_CREATE", e.message ?: e.toString())
        }

        if (serverSocket != null) {
            isReceivingConnections.store(true)
            var socket: BluetoothSocket?
            viewModelScope.launch(Dispatchers.IO) {
                while (isReceivingConnections.load()) {
                    try {
                        // Accept incoming connection
                        socket = serverSocket!!.accept()
                    } catch (e: IOException) {
                        Log.e("SOCKET_CREATE", e.message ?: e.toString())
                        break
                    }
                    // If a connection was accepted, handle the connection in a separate thread
                    if (socket != null && !connectedDevices.keys.contains(socket.remoteDevice)) {
                        connectedDevices[socket.remoteDevice] = socket
                        devicesConnectionStatus[socket.remoteDevice.name] = true
                        listenToSocket(socket)
                    }
                }
            }
        }
    }

    fun stopSocketConnection() {
        isReceivingConnections.store(false)
    }

    fun connect(
        context: Context,
        address: String,
        onCouldNotConnect: () -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForPermissions.value = true
            onCouldNotConnect()
            return
        }
        val device = adapter.value?.getRemoteDevice(address)
        if (device == null) {
            onCouldNotConnect()
            return
        }

        if (!connectedDevices.keys.contains(device)) {
            val bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid)
            try {
                bluetoothSocket.connect()
                connectedDevices[device] = bluetoothSocket
                devicesConnectionStatus[device.name] = true
                listenToSocket(bluetoothSocket)
            } catch (e: Exception) {
                Log.e("CONNECT", "can't connect to device as client", e)
            }
        }


    }

    private val RECIEVED_SCORE = 1001

    private fun listenToSocket(socket: BluetoothSocket) {
        val callback = Handler.Callback { message ->
            when (message.what) {
                RECIEVED_SCORE -> {
                    try {
                        devicesScore[(message.obj as BluetoothSocket).remoteDevice.name] =
                            message.arg1
                    } catch (e: SecurityException) {
                        Log.e("SCORES", "cant display scores without permissions", e)
                    }
                }
            }
            true
        }
        exchangeUseCase.listenToSocket(
            socket,
            Handler(Looper.getMainLooper(), callback)
        )
    }


    fun sendResultsToSockets(points: Int) {
        for (socket in connectedDevices.values)
            exchangeUseCase.writePointsToSocket(points, socket)
    }

    fun stopAllSocketConnections() {
        devicesConnectionStatus.clear()
        for (socket in connectedDevices.values) {
            exchangeUseCase.cancel(socket)
        }
    }


}