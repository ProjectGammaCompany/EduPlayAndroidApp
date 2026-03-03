package com.eduplay.moblie.useCases

import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.State
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

class BluetoothConnectionFragment : Fragment() {

    private lateinit var bluetoothEnableLauncher: ActivityResultLauncher<Intent>
    private final val defaultValue = -2
    private var activityResult = defaultValue


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bluetoothEnableLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            { result ->
                activityResult = result.resultCode
            })
    }

    suspend fun connect(
        bluetoothManager: State<BluetoothManager?>,
        bluetoothAdapter: State<BluetoothAdapter?>,
        updateManger: (BluetoothManager?) -> Unit,
        updateAdapter: (BluetoothAdapter?) -> Unit,
        showConnectionList: () -> Unit,
        onDoesNotSupportBluetooth: () -> Unit
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

        // включаем bluetooth
        if (!adapter.isEnabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            bluetoothEnableLauncher.launch(intent)

            while(activityResult == defaultValue){
                delay(100)
            }
            if (activityResult == RESULT_OK) {
                findDevices(showConnectionList)
            } else {
                onDoesNotSupportBluetooth()
            }

        } else {
            findDevices(showConnectionList)
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

    private fun findDevices(showConnectionList: () -> Unit,) {

    }
}