package com.kidor.vigik.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import com.kidor.vigik.data.bluetooth.model.BluetoothDevice
import com.kidor.vigik.data.bluetooth.model.BluetoothScanError
import com.kidor.vigik.receivers.BluetoothDiscoveryReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val LE_SCAN_DURATION_IN_MS = 10_000L

/**
 * Class aimed to regroup actions related to the Bluetooth peripheral.
 */
class BluetoothAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val bluetoothAdapter: BluetoothAdapter,
    private val bluetoothDiscoveryReceiver: BluetoothDiscoveryReceiver
) : ScanCallback() {

    private lateinit var apiCallback: BluetoothApiCallback
    private var _scanCallback: BluetoothScanCallback? = null

    /**
     * Initializes this class with non-injectable parameters.
     * This method should be called before using this component.
     *
     * @param bluetoothApiCallback The callback to forward Bluetooth events.
     */
    fun initialize(bluetoothApiCallback: BluetoothApiCallback) {
        apiCallback = bluetoothApiCallback
    }

    @SuppressLint("MissingPermission")
    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        result?.device?.let { device ->
            _scanCallback?.onDeviceFound(
                BluetoothDevice(
                    majorDeviceClass = device.bluetoothClass.majorDeviceClass,
                    name = device.name,
                    hardwareAddress = device.address
                )
            )
        }
    }

    override fun onScanFailed(errorCode: Int) {
        Timber.e("Error during BLE scan: $errorCode")
        _scanCallback?.onScanError(BluetoothScanError.fromErrorCode(errorCode))
        apiCallback.onScanningStateChanged(isScanning = false)
    }

    /**
     * Returns true if Bluetooth is enabled, otherwise false.
     */
    fun isEnabled(): Boolean = bluetoothAdapter.isEnabled

    /**
     * Start scanning for Bluetooth devices.
     *
     * @param isLeScan True to perform a Low Energy Bluetooth scan, otherwise false.
     */
    @SuppressLint("MissingPermission", "UnspecifiedRegisterReceiverFlag")
    fun startScan(isLeScan: Boolean, scanCallback: BluetoothScanCallback) {
        if (isLeScan) {
            // Save callback
            _scanCallback = scanCallback
            // Notify the start of scan
            apiCallback.onScanningStateChanged(isScanning = true)
            // Start LE scan
            bluetoothAdapter.bluetoothLeScanner.startScan(this)
            // Stop scan after 10 sec
            coroutineScope.launch {
                delay(LE_SCAN_DURATION_IN_MS)
                stopLeScan()
            }
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
     * Stop scanning for Low Energy Bluetooth devices.
     */
    @SuppressLint("MissingPermission")
    fun stopLeScan() {
        // Stop LE scan
        bluetoothAdapter.bluetoothLeScanner.stopScan(this)
        // Notify the end of scan
        apiCallback.onScanningStateChanged(isScanning = false)
        // Remove callback
        _scanCallback = null
    }
}
