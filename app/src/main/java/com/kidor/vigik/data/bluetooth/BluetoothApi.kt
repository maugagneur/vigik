package com.kidor.vigik.data.bluetooth

import android.location.LocationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Singleton that provides all Bluetooth services to the app.
 */
@Singleton
class BluetoothApi @Inject constructor(
    bluetoothAdapter: BluetoothAdapter,
    private val locationManager: LocationManager
) {

    /**
     * Indicates if the device's Bluetooth adapter is enabled.
     */
    val bluetoothEnable: StateFlow<Boolean> get() = _bluetoothEnable
    private val _bluetoothEnable: MutableStateFlow<Boolean> = MutableStateFlow(bluetoothAdapter.isEnabled())

    /**
     * Indicates if one of the device's location providers is enabled.
     */
    val locationEnable: StateFlow<Boolean> get() = _locationEnable
    private val _locationEnable: MutableStateFlow<Boolean> = MutableStateFlow(checkIfLocationIsEnabled())

    /**
     * Notifies that Bluetooth adapter state has changed.
     *
     * @param enable True if the Bluetooth adapter turns ON, false if it turns OFF.
     */
    fun onBluetoothStateChanged(enable: Boolean) {
        _bluetoothEnable.tryEmit(enable)
    }

    /**
     * Notifies that location state has changed.
     */
    fun onLocationStateChanged() {
        _locationEnable.tryEmit(checkIfLocationIsEnabled())
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
}
