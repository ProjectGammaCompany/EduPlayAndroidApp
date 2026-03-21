package com.eduplay.moblie.useCases

import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.coroutines.delay


class BluetoothConnectionFragment : Fragment() {

    private lateinit var bluetoothEnableLauncher: ActivityResultLauncher<Intent>
    private final val defaultValue = -2
    private var activityResult = defaultValue




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
        onConnectionTookTooLong: () -> Unit
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
            while (activityResult == defaultValue && maxTime > 0) {
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
            val manager: BluetoothManager =
                getSystemService(requireContext(), BluetoothManager::class.java)
                    ?: throw NoSuchMethodException("does not support bluetooth")
            updateManger(manager)
            return manager
        }
        return bluetoothManager
    }

}