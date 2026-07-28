package com.eduplay.moblie.useCases.bluetoothInteractions

import android.Manifest
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay


class BluetoothConnectionFragment : Fragment() {

    private lateinit var bluetoothEnableLauncher: ActivityResultLauncher<Intent>
    private val defaultValue = -2
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
    ) : Boolean {
        val manager: BluetoothManager
        val adapter: BluetoothAdapter
        try {
            manager = getManager(bluetoothManager.value, updateManger)
            adapter = getAdapter(manager, bluetoothAdapter.value, updateAdapter)
        } catch (e: NoSuchMethodException) {
            Log.e("BLUETOOTH_INIT", e.message ?: "")
            onDoesNotSupportBluetooth()
            return false
        }

        if (!adapter.isEnabled) {
            try {
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
                    return false
                } else {
                    onDoesNotSupportBluetooth()
                    return false
                }
            } catch (e: SecurityException) {
                Log.e("Permission_bluetooth", e.message ?: "", e)
                return false
            } catch (e: Exception) {
                Log.e("ERROR_bluetooth", e.message ?: "", e)
                onDoesNotSupportBluetooth()
                return false
            }
            return true
        } else {
            onCanProceedToFindDevices()
            return true
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