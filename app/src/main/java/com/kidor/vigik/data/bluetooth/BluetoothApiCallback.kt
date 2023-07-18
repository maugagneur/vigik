package com.kidor.vigik.data.bluetooth

/**
 * Callback used to notify changes on Bluetooth peripheral to the [BluetoothApi].
 */
interface BluetoothApiCallback {

    /**
     * Notifies that Bluetooth adapter state has changed.
     *
     * @param enable True if the Bluetooth adapter turns ON, false if it turns OFF.
     */
    fun onBluetoothStateChanged(enable: Boolean)

    /**
     * Notifies that location state has changed.
     */
    fun onLocationStateChanged()

    /**
     * Notifies that Bluetooth adapter start or stop scanning.
     *
     * @param isScanning True if the adapter started scanning, false if is stopped.
     */
    fun onScanningStateChanged(isScanning: Boolean)
}
