package com.kidor.vigik.ui.bluetooth

import com.kidor.vigik.data.bluetooth.model.BluetoothDevice
import com.kidor.vigik.ui.base.ViewState

/**
 * State of the Bluetooth view.
 *
 * @param isBluetoothEnable True if the Bluetooth adapter is turned ON, otherwise false.
 * @param isLocationEnable  True if the location is turned ON, otherwise false.
 * @param leScanSelected    True if LE (low energy) scan is selected, otherwise false.
 * @param isScanning        True if the Bluetooth adapter is currently scanning, otherwise false.
 * @param detectedDevices   The list of Bluetooth devices detected.
 * @param errorMessage      The error description if an error occurred, otherwise null.
 */
data class BluetoothViewState(
    val isBluetoothEnable: Boolean = false,
    val isLocationEnable: Boolean = false,
    val leScanSelected: Boolean = false,
    val isScanning: Boolean = false,
    val detectedDevices: List<BluetoothDevice> = emptyList(),
    val errorMessage: String? = null
) : ViewState
