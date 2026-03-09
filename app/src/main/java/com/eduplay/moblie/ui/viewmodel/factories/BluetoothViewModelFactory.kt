package com.eduplay.moblie.ui.viewmodel.factories

import android.bluetooth.BluetoothAdapter
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eduplay.moblie.ui.viewmodel.BluetoothViewModel

class BluetoothViewModelFactory(val adapter: State<BluetoothAdapter?>) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
            return BluetoothViewModel(adapter) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}