package com.kidor.vigik.ui.bluetooth

import androidx.annotation.RestrictTo
import androidx.lifecycle.viewModelScope
import com.kidor.vigik.R
import com.kidor.vigik.data.Localization
import com.kidor.vigik.data.bluetooth.BluetoothApi
import com.kidor.vigik.data.bluetooth.BluetoothScanCallback
import com.kidor.vigik.data.bluetooth.model.BluetoothDevice
import com.kidor.vigik.data.bluetooth.model.BluetoothScanError
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Business logic of Bluetooth screen.
 */
@HiltViewModel
class BluetoothViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val bluetoothApi: BluetoothApi,
    private val localization: Localization
) : BaseViewModel<BluetoothViewAction, BluetoothViewState, Nothing>() {

    init {
        // Emit view state with default values at start
        _viewState.value = BluetoothViewState(
            isBluetoothEnable = bluetoothApi.bluetoothEnable.value,
            isLocationEnable = bluetoothApi.locationEnable.value
        )

        viewModelScope.launch(ioDispatcher) {
            bluetoothApi.bluetoothEnable.collect { bluetoothEnable ->
                updateViewState { it.copy(isBluetoothEnable = bluetoothEnable) }
            }
        }
        viewModelScope.launch(ioDispatcher) {
            bluetoothApi.locationEnable.collect { locationEnable ->
                updateViewState {
                    // Force LE scan to false when location is turned OFF
                    it.copy(isLocationEnable = locationEnable, leScanSelected = it.leScanSelected && locationEnable)
                }
            }
        }
        viewModelScope.launch(ioDispatcher) {
            bluetoothApi.isScanning.collect { isScanning ->
                updateViewState { it.copy(isScanning = isScanning) }
            }
        }
    }

    override fun handleAction(viewAction: BluetoothViewAction) {
        when (viewAction) {
            BluetoothViewAction.StartBluetoothScan -> viewModelScope.launch(ioDispatcher) {
                // Reset the list of detected devices and the error message
                updateViewState {
                    it.copy(
                        detectedDevices = emptyList(),
                        errorMessage = null
                    )
                }

                val lowEnergyScanSelected = viewState.value?.leScanSelected ?: false
                bluetoothApi.startScan(
                    lowEnergy = lowEnergyScanSelected,
                    scanCallback = object : BluetoothScanCallback {
                        override fun onDeviceFound(device: BluetoothDevice) {
                            Timber.d("Bluetooth device detected: $device")

                            // Ignore devices with an already known hardware address
                            viewState.value?.detectedDevices?.let { deviceList ->
                                if (deviceList.none { it.hardwareAddress == device.hardwareAddress }) {
                                    updateViewState { currentState ->
                                        currentState.copy(
                                            detectedDevices = mutableListOf<BluetoothDevice>().apply {
                                                addAll(currentState.detectedDevices)
                                                add(device)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        override fun onScanError(scanError: BluetoothScanError) {
                            Timber.e("Bluetooth scan error: $scanError")
                            val errorMessage = if (scanError == BluetoothScanError.SCAN_FAILED_TO_START) {
                                localization.getString(R.string.bluetooth_start_scan_error_message)
                            } else {
                                scanError.name
                            }
                            updateViewState { it.copy(errorMessage = errorMessage) }
                        }
                    }
                )
            }

            is BluetoothViewAction.ChangeLeScanState -> {
                updateViewState { it.copy(leScanSelected = viewAction.isChecked) }
            }
        }
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public override fun onCleared() {
        super.onCleared()
        // Stop scan if still running
        if (viewState.value?.isScanning == true) {
            bluetoothApi.stopScan()
        }
    }
}
