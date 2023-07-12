package com.kidor.vigik.data.bluetooth

import android.bluetooth.BluetoothAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton that provides all Bluetooth services to the app.
 */
@Singleton
class BluetoothApi @Inject constructor(
    bluetoothAdapter: BluetoothAdapter
) {

    /**
     * Indicates if the device's Bluetooth adapter is enable.
     */
    val bluetoothEnable: StateFlow<Boolean> get() = _bluetoothEnable
    private val _bluetoothEnable: MutableStateFlow<Boolean> = MutableStateFlow(bluetoothAdapter.isEnabled)

    /**
     * Notifies that Bluetooth adapter state has changed.
     *
     * @param enable True if the Bluetooth adapter turns ON, false if it turns OFF.
     */
    fun onBluetoothStateChanged(enable: Boolean) {
        _bluetoothEnable.tryEmit(enable)
    }
}
