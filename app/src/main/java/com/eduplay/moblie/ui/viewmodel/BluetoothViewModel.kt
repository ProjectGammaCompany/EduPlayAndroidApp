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
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.R
import com.eduplay.moblie.useCases.bluetoothInteractions.BluetoothDataExchangeUseCase
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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

    private val uuid = UUID.fromString("eac368b1-ab18-4804-91fe-ab823227d8da")
    private val connectedDevices = mutableMapOf<BluetoothDevice, BluetoothSocket>()

    // all devices bound and scanned; key - mac address; value - name
    val foundDevices = mutableStateMapOf<String, String?>()
    val devicesConnectionStatus = mutableStateMapOf<String, Boolean>()
    val isScanning = mutableStateOf(false)
    val isReceivingConnections = AtomicBoolean(false)
    val devicesScore = mutableStateMapOf<String, Int>()
    var askForPermissions = mutableStateOf(false)
    val needLocation = mutableStateOf(false)
    val deviceNameTooLong = mutableStateOf(false)

    fun discoverDevices(
        context: Context,
        onScanFailed: () -> Unit
    ) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        needLocation.value = !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
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
//        foundDevices.putAll(
//            adapter.value?.bondedDevices
//                ?.filter { device -> device != null }
//                ?.map { device -> Pair(device.address, device.name) } ?: listOf()
//        )


        if (scanner == null) {
            Log.d("SCAN", "no scanner")
            return
        }
        if (isScanning.value) {
            Log.d("SCAN", "is already scanning")
            return
        }

        isScanning.value = true

        val filters: MutableList<ScanFilter?> = mutableListOf()
        val filter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(uuid))
            .build()
        filters.add(filter)

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            //.setMatchMode(ScanSettings.MATCH_MODE_STICKY)
            //.setNumOfMatches(ScanSettings.MATCH_NUM_FEW_ADVERTISEMENT)
            .setReportDelay(0L)
            .build()

        val scanCallback: ScanCallback = object : ScanCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                val device: BluetoothDevice? = result.device
                val deviceName = device?.name ?: result.scanRecord?.deviceName
                if (device != null && device.address != null && deviceName != null)
                    foundDevices.put(device.address!!, deviceName)
            }
        }
        devicesScore[context.getString(R.string.you)] = 0
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
        isScanning.value = false
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        scanner.stopScan(scanCallback)
        stopSocketConnection()
        stopAdvertising()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    private fun advertise() {
        val advertiser =
            adapter.value?.bluetoothLeAdvertiser
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setConnectable(true)
            .build()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false)
            .addServiceUuid(ParcelUuid(uuid))
            .build()
        val scanResponse = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .build()

        val callback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.e("advert", "cant start $errorCode")
                deviceNameTooLong.value = true
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
                Log.e("advert", "start")
            }
        }

        advertiser?.startAdvertising(settings, data, scanResponse, callback)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    private fun stopAdvertising() {
        val callback: AdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
                Log.e("advert", "cant stop $errorCode")
                deviceNameTooLong.value = true
            }

            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                super.onStartSuccess(settingsInEffect)
                Log.e("advert", "stop")
            }
        }
        adapter.value?.bluetoothLeAdvertiser?.stopAdvertising(callback)
    }

    private var serverSocket: BluetoothServerSocket? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun startServerSocket() {
        try {
            serverSocket =
                adapter.value?.listenUsingRfcommWithServiceRecord("EduPlay", uuid)
        } catch (e: IOException) {
            Log.e("SOCKET_CREATE", e.message ?: e.toString())
        }

        if (serverSocket != null) {
            isReceivingConnections.store(true)
            var socket: BluetoothSocket?
            viewModelScope.launch(Dispatchers.IO) {
                while (true) {
                    if (!isReceivingConnections.load()) {
                        break
                    }
                    try {
                        socket = serverSocket!!.accept()
                        Log.i("SOCKET_CREATE", "accepted ${socket.remoteDevice.name}")
                    } catch (e: IOException) {
                        Log.e("SOCKET_CREATE", e.message ?: e.toString(), e)
                        continue
                    } catch (e: Exception) {
                        Log.e("SOCKET_CREATE", e.message ?: "", e)
                        break
                    }
                    if (socket != null && !connectedDevices.keys.contains(socket.remoteDevice)) {
                        connectedDevices[socket.remoteDevice] = socket
                        devicesConnectionStatus[socket.remoteDevice.address] = true
                        val device = socket.remoteDevice
                        val deviceName = device?.name ?: "unknown"
                        if (device != null && device.address != null && deviceName != null)
                            foundDevices.put(device.address!!, deviceName)
                        listenToSocket(socket)
                    }
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    fun stopSocketConnection() {
        isReceivingConnections.store(false)
        stopAdvertising()
//        serverSocket?.close()
//        serverSocket = null
    }

    fun connect(
        context: Context,
        address: String,
        onCouldNotConnect: () -> Unit
    ) {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForPermissions.value = true
            onCouldNotConnect()
            return
        }

        connectSockets(address, onCouldNotConnect)
    }

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT])
    private fun connectSockets(address: String, onCouldNotConnect: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val device = adapter.value?.getRemoteDevice(address)
            if (device == null) {
                onCouldNotConnect()
                return@launch
            }


            if (!connectedDevices.keys.contains(device)) {
                var connectionTries = 0
                var connected = false
                while (!connected && connectionTries < 3) {
                    try {
                        val bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
                        Log.d("CONNECT", "$address ${device.address}")
                        bluetoothSocket.connect()
                        connectedDevices[device] = bluetoothSocket
                        devicesConnectionStatus[device.address] = true
                        devicesScore[device.name] = 0
                        listenToSocket(bluetoothSocket)
                        connected = true
                    } catch (e: IOException) {
                        if (device.bondState != BluetoothDevice.BOND_BONDED) {
                            device.createBond()
                            delay(3000)
                        }
                        Log.e("CONNECT", "bonding", e)
                    } catch (e: Exception) {
                        onCouldNotConnect()
                        Log.e("CONNECT", "can't connect to device as client", e)
                    }
                    connectionTries++
                }
            }
        }
    }


    private val RECIEVED_SCORE = 1001

    private fun listenToSocket(socket: BluetoothSocket) {
        val handler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                Log.d("Bluetooth_handler", "{${msg.what}}")
                when (msg.what) {
                    RECIEVED_SCORE -> {
                        try {
                            Log.d("SCORES", "${(msg.obj as BluetoothSocket).remoteDevice.name} ${msg.arg1} ${devicesScore[(msg.obj as BluetoothSocket).remoteDevice.name]}")
                            devicesScore[(msg.obj as BluetoothSocket).remoteDevice.name] = msg.arg1
                        } catch (e: SecurityException) {
                            Log.e("SCORES", "cant display scores without permissions", e)
                        }
                    }
                }
            }
        }
        exchangeUseCase.listenToSocket(
            socket,
            handler
        )
    }


    fun sendResultsToSockets(points: Int, context: Context) {
        devicesScore[context.getString(R.string.you)] = points +
                (devicesScore[context.getString(R.string.you)] ?: 0)
        for (socket in connectedDevices.values)
            exchangeUseCase.writePointsToSocket(devicesScore[context.getString(R.string.you)] ?: 0, socket)
    }

    fun clearResults() {
        devicesScore.keys.forEach {
            devicesScore[it] = 0
        }
    }

    fun stopAllSocketConnections() {
        devicesConnectionStatus.clear()
        devicesScore.clear()
        foundDevices.clear()
        devicesScore.clear()
        try {
            for (socket in connectedDevices.values) {
                exchangeUseCase.cancel(socket)
            }
        } catch (e: Exception) {
            Log.e("DISCONNECT", e.message ?: "", e)
        }
        connectedDevices.clear()
    }
}