package com.kidor.vigik.ui.bluetooth

import com.kidor.vigik.ui.base.ViewState

/**
 * State of the Bluetooth view.
 *
 * @param isBluetoothEnable True if the Bluetooth adapter is turned ON, otherwise false.
 * @param isLocationEnable  True if the location is turned ON, otherwise false.
 * @param isScanning        True if the Bluetooth adapter is currently scanning, otherwise false.
 */
data class BluetoothViewState(
    val isBluetoothEnable: Boolean = false,
    val isLocationEnable: Boolean = false,
    val isScanning: Boolean = false
) : ViewState()
