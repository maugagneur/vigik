package com.kidor.vigik.receivers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.IntentCompat
import com.kidor.vigik.data.bluetooth.BluetoothApiCallback
import com.kidor.vigik.data.bluetooth.BluetoothScanCallback
import com.kidor.vigik.data.bluetooth.model.BluetoothDeviceType
import timber.log.Timber
import javax.inject.Inject

private const val ACTION_DISCOVERY_STARTED = BluetoothAdapter.ACTION_DISCOVERY_STARTED
private const val ACTION_DISCOVERY_FINISHED = BluetoothAdapter.ACTION_DISCOVERY_FINISHED
private const val ACTION_DEVICE_FOUND = BluetoothDevice.ACTION_FOUND

/**
 * Broadcast receiver listening to Bluetooth device discovery process.
 */
class BluetoothDiscoveryReceiver @Inject constructor() : BroadcastReceiver() {

    private var apiCallback: BluetoothApiCallback? = null
    private var scanCallback: BluetoothScanCallback? = null

    /**
     * Registers a [BluetoothApiCallback] and a [BluetoothScanCallback] to be notify of scan events.
     *
     * @param bluetoothApiCallback  The API callback.
     * @param bluetoothScanCallback The scan callback.
     * @see [unregisterCallbacks]
     */
    fun registerCallbacks(bluetoothApiCallback: BluetoothApiCallback, bluetoothScanCallback: BluetoothScanCallback) {
        apiCallback = bluetoothApiCallback
        scanCallback = bluetoothScanCallback
    }

    /**
     * Unregisters the previous [BluetoothApiCallback] and [BluetoothScanCallback] registered.
     *
     * @see [registerCallbacks]
     */
    fun unregisterCallbacks() {
        apiCallback = null
        scanCallback = null
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_DISCOVERY_STARTED -> {
                Timber.d("Discovery started")
                apiCallback?.onScanningStateChanged(isScanning = true)
            }

            ACTION_DEVICE_FOUND -> {
                val device = IntentCompat.getParcelableExtra(
                    intent,
                    BluetoothDevice.EXTRA_DEVICE,
                    BluetoothDevice::class.java
                )
                val deviceClass = IntentCompat.getParcelableExtra(
                    intent,
                    BluetoothDevice.EXTRA_CLASS,
                    BluetoothClass::class.java
                )
                scanCallback?.onDeviceFound(
                    device = com.kidor.vigik.data.bluetooth.model.BluetoothDevice(
                        type = BluetoothDeviceType.fromMajorBluetoothClass(deviceClass?.majorDeviceClass),
                        name = device?.name ?: "???",
                        hardwareAddress = device?.address ?: ""
                    ).also {
                        Timber.d("New Bluetooth device discovered: $it")
                    }
                )
            }

            ACTION_DISCOVERY_FINISHED -> {
                Timber.d("Discovery finished")
                apiCallback?.onScanningStateChanged(isScanning = false)
            }
        }
    }

    companion object {
        /**
         * The [IntentFilter] to use in order to receive broadcasts from this [BroadcastReceiver].
         */
        val INTENT_FILTER = IntentFilter().apply {
            addAction(ACTION_DISCOVERY_STARTED)
            addAction(ACTION_DISCOVERY_FINISHED)
            addAction(ACTION_DEVICE_FOUND)
        }
    }
}
