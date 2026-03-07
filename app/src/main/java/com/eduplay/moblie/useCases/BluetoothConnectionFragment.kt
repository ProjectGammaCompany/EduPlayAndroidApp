package com.eduplay.moblie.useCases

import android.Manifest
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BluetoothConnectionFragment : Fragment() {

    private lateinit var bluetoothEnableLauncher: ActivityResultLauncher<Intent>
    private final val defaultValue = -2
    private var activityResult = defaultValue

    val foundDevices = mutableStateListOf<Pair<String?, String?>>()
    val isScanning = mutableStateOf(false)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // причина существования этого фрагмента
        // из типа классов не связанных с компонентами нельзя сделать
        // подписку на полчуение результата от активити
        bluetoothEnableLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            { result ->
                activityResult = result.resultCode
            })
    }

    suspend fun startBluetooth(
        bluetoothManager: State<BluetoothManager?>,
        bluetoothAdapter: State<BluetoothAdapter?>,
        updateManger: (BluetoothManager?) -> Unit,
        updateAdapter: (BluetoothAdapter?) -> Unit,
        onCanProceedToFindDevices: () -> Unit,
        onDoesNotSupportBluetooth: () -> Unit,
        onConnectionTookTooLong:() -> Unit
    ) {
        val manager: BluetoothManager
        val adapter: BluetoothAdapter
        try {
             manager = getManager(bluetoothManager.value, updateManger)
             adapter = getAdapter(manager, bluetoothAdapter.value, updateAdapter)
        } catch (e: NoSuchMethodException) {
            Log.e("BLUETOOTH_INIT", e.message ?: "")
            onDoesNotSupportBluetooth()
            return
        }

        if (!adapter.isEnabled) {
            activityResult = defaultValue
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothEnableLauncher.launch(intent)

            var maxTime = 600
            while(activityResult == defaultValue && maxTime > 0){
                delay(100)
                maxTime--
            }
            if (activityResult == RESULT_OK) {
                onCanProceedToFindDevices()
            } else if (maxTime <= 0) {
                onConnectionTookTooLong()
                return
            } else {
                onDoesNotSupportBluetooth()
                return
            }

        } else {
            onCanProceedToFindDevices()
        }

    }

    private fun getAdapter(
        manager: BluetoothManager,
        bluetoothAdapter: BluetoothAdapter?,
        updateAdapter: (BluetoothAdapter?) -> Unit
    ): BluetoothAdapter {
        if (bluetoothAdapter == null) {
            val adapter = manager.adapter
                ?: throw NoSuchMethodException("does not support bluetooth")
            updateAdapter(adapter)
            return adapter
        }
        return bluetoothAdapter
    }

    private fun getManager(
        bluetoothManager: BluetoothManager?,
        updateManger: (BluetoothManager?) -> Unit
    ): BluetoothManager {
        if (bluetoothManager == null) {
            val manager: BluetoothManager = getSystemService( requireContext(), BluetoothManager::class.java)
                ?: throw NoSuchMethodException("does not support bluetooth")
            updateManger(manager)
            return manager
        }
        return bluetoothManager
    }

    fun getBondedDevices(adapter: BluetoothAdapter, askForConnectPermission:()->Unit) {
        try {
            foundDevices.addAll( adapter.bondedDevices?.map { device ->
                Pair(device.name, device.address)
            }?: listOf<Pair<String?, String?>>())
        }catch (e: SecurityException) {
            askForConnectPermission()
        }
    }



    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    suspend fun discoverDevices(
        adapter: BluetoothAdapter,
        onScanFailed: ()->Unit
    ) {
        val scanner = adapter.bluetoothLeScanner

        if (scanner == null) {
            Log.d("SCAN", "no scanner")
            return
        }
        if (isScanning.value) {
            Log.d("SCAN", "is already scanning")
            return
        }

        val filters: List<ScanFilter?> = listOf()

        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
            .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
            .setReportDelay(0L)
            .build()

        val scanCallback: ScanCallback = object : ScanCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device: BluetoothDevice? = result.device
                foundDevices.add(Pair(device?.name, device?.address))
            }

            override fun onBatchScanResults(results: MutableList<ScanResult?>?) {
            }

            override fun onScanFailed(errorCode: Int) {
                isScanning.value = false
                onScanFailed()
            }
        }
        coroutineScope {
            launch(Dispatchers.IO) {
                scanner.startScan(filters, scanSettings, scanCallback);
            }
        }

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScan(adapter: BluetoothAdapter) {
        val scanner = adapter.bluetoothLeScanner

        val scanCallback: ScanCallback = object : ScanCallback() {
            override fun onScanResult(
                callbackType: Int,
                result: ScanResult?
            ) {
                isScanning.value = false
            }

            override fun onScanFailed(errorCode: Int) {
                isScanning.value = false
            }
        }
        scanner.stopScan(scanCallback)
    }

    fun connectAsServer() {

    }

    fun connectAsClient() {

    }
}