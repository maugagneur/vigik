package com.kidor.vigik.data.bluetooth

import android.location.LocationManager
import com.kidor.vigik.data.bluetooth.model.BluetoothScanError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton that provides all Bluetooth services to the app.
 */
@Singleton
class BluetoothApi @Inject constructor(
    private val adapter: BluetoothAdapter,
    private val locationManager: LocationManager
) : BluetoothApiCallback {

    private val _bluetoothEnable: MutableStateFlow<Boolean> = MutableStateFlow(adapter.isEnabled())
    /**
     * Indicates if the device's Bluetooth adapter is enabled.
     */
    val bluetoothEnable: StateFlow<Boolean> get() = _bluetoothEnable

    private val _locationEnable: MutableStateFlow<Boolean> = MutableStateFlow(checkIfLocationIsEnabled())
    /**
     * Indicates if one of the device's location providers is enabled.
     */
    val locationEnable: StateFlow<Boolean> get() = _locationEnable

    private val _isScanning: MutableStateFlow<Boolean> = MutableStateFlow(false)
    /**
     * Indicates if the Bluetooth adapter of the device is scanning.
     */
    val isScanning: StateFlow<Boolean> get() = _isScanning

    init {
        adapter.initialize(this)
    }

    override fun onBluetoothStateChanged(enable: Boolean) {
        _bluetoothEnable.tryEmit(enable)
    }

    override fun onLocationStateChanged() {
        _locationEnable.tryEmit(checkIfLocationIsEnabled())
    }

    override fun onScanningStateChanged(isScanning: Boolean) {
        _isScanning.tryEmit(isScanning)
    }

    /**
     * Tells if location is enabled base on GPS and network providers.
     *
     * @return True if at least one of GPS and network providers is enable, otherwise false.
     */
    private fun checkIfLocationIsEnabled(): Boolean {
        val isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return isGpsProviderEnabled || isNetworkProviderEnabled
    }

    /**
     * Starts a Bluetooth scan.
     *
     * @param lowEnergy    True to perform a Low Energy scan, otherwise false.
     * @param scanCallback The Bluetooth scan callback.
     */
    fun startScan(lowEnergy: Boolean, scanCallback: BluetoothScanCallback) {
        if (isScanning.value) {
            Timber.e("startScan() called when already scanning")
            scanCallback.onScanError(BluetoothScanError.SCAN_FAILED_ALREADY_STARTED)
        } else {
            adapter.startScan(isLeScan = lowEnergy, scanCallback = scanCallback)
        }
    }

    /**
     * Stops current Bluetooth scan.
     */
    fun stopScan() {
        if (isScanning.value) {
            adapter.stopScan()
        } else {
            Timber.w("Requested to stop scan while not scanning")
        }
    }
}
