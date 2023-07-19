package com.kidor.vigik.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.content.Context
import com.kidor.vigik.receivers.BluetoothDiscoveryReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Class aimed to regroup actions related to the Bluetooth peripheral.
 */
class BluetoothAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothDiscoveryReceiver: BluetoothDiscoveryReceiver
) : ScanCallback() {

    private lateinit var apiCallback: BluetoothApiCallback

    /**
     * Initializes this class with non-injectable parameters.
     * This method should be called before using this component.
     *
     * @param bluetoothApiCallback The callback to forward Bluetooth events.
     */
    fun initialize(bluetoothApiCallback: BluetoothApiCallback) {
        apiCallback = bluetoothApiCallback
    }
    /**
     * Returns true if Bluetooth is enabled, otherwise false.
     */
    fun isEnabled(): Boolean = bluetoothAdapter.isEnabled

    /**
     * Start scanning for Bluetooth devices.
     *
     * @param isLEScan True to perform a Low Energy Bluetooth scan, otherwise false.
     */
    @SuppressLint("MissingPermission")
    fun startScan(isLEScan: Boolean, scanCallback: BluetoothScanCallback) {
        if (isLEScan) {
            bluetoothAdapter.bluetoothLeScanner.startScan(this)
        } else {
            // Register callback
            bluetoothDiscoveryReceiver.registerCallbacks(apiCallback, scanCallback)
            // Register receiver
            context.registerReceiver(bluetoothDiscoveryReceiver, BluetoothDiscoveryReceiver.INTENT_FILTER)
            val isSuccessful = bluetoothAdapter.startDiscovery()
            if (!isSuccessful) {
                Timber.e("Fail to start Bluetooth device discovery")
                scanCallback.onScanError(BluetoothScanError.SCAN_FAILED_TO_START)
            }
        }
    }

    /**
     * Stop scanning for Bluetooth devices.
     *
     * @param isLEScan True to stop a Low Energy Bluetooth scan, otherwise false.
     */
    @SuppressLint("MissingPermission")
    fun stopScan(isLEScan: Boolean) {
        if (isLEScan) {
            bluetoothAdapter.bluetoothLeScanner.stopScan(this)
        } else {
            // Unregister callback
            bluetoothDiscoveryReceiver.unregisterCallbacks()
            val isSuccessful = bluetoothAdapter.cancelDiscovery()
            if (!isSuccessful) {
                Timber.e("Fail to stop Bluetooth device discovery")
            }
            // Unregister receiver
            context.unregisterReceiver(bluetoothDiscoveryReceiver)
        }
    }
}
