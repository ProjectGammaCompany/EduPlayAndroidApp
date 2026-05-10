package com.eduplay.moblie.ui.viewmodel.factories

import android.bluetooth.BluetoothAdapter
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eduplay.moblie.ui.viewmodel.BluetoothViewModel
import com.eduplay.moblie.useCases.bluetoothInteractions.BluetoothDataExchangeUseCase

class BluetoothViewModelFactory(
    val adapter: State<BluetoothAdapter?>,
    val exchangeUseCase: BluetoothDataExchangeUseCase
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
            return BluetoothViewModel(adapter, exchangeUseCase) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}