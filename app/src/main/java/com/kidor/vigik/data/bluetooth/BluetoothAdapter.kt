package com.kidor.vigik.data.bluetooth

import android.bluetooth.BluetoothAdapter
import javax.inject.Inject

/**
 * Class aimed to regroup actions related to the Bluetooth peripheral.
 */
class BluetoothAdapter @Inject constructor(
    private val adapter: BluetoothAdapter
) {

    /**
     * Returns true if Bluetooth is enabled, otherwise false.
     */
    fun isEnabled(): Boolean = adapter.isEnabled
}
