package com.kidor.vigik.data.bluetooth

import com.kidor.vigik.data.bluetooth.model.BluetoothDevice
import com.kidor.vigik.data.bluetooth.model.BluetoothScanError

/**
 * Callback used to forward devices found and errors during a bluetooth scan to the element that initiated the scan.
 */
interface BluetoothScanCallback {

    /**
     * Method called when a device is found during Bluetooth scan.
     *
     * @param device The device found.
     */
    fun onDeviceFound(device: BluetoothDevice)

    /**
     * Method called when an error occurred during Bluetooth scan.
     *
     * @param scanError The encountered error.
     */
    fun onScanError(scanError: BluetoothScanError)
}
