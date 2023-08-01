package com.kidor.vigik.ui.bluetooth

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
        viewModelScope.launch(ioDispatcher) {
            bluetoothApi.bluetoothEnable.collect { bluetoothEnable ->
                updateViewState { it.copy(isBluetoothEnable = bluetoothEnable) }
            }
        }
        viewModelScope.launch(ioDispatcher) {
            bluetoothApi.locationEnable.collect { locationEnable ->
                updateViewState { it.copy(isLocationEnable = locationEnable) }
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
                bluetoothApi.startScan(lowEnergyScanSelected, object : BluetoothScanCallback {
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
                })
            }

            is BluetoothViewAction.ChangeLeScanState -> {
                updateViewState { it.copy(leScanSelected = viewAction.isChecked) }
            }
        }
    }

    /**
     * Update the current view state.
     *
     * @param update The operation to perform on view state.
     */
    private fun updateViewState(update: (BluetoothViewState) -> BluetoothViewState) {
        viewModelScope.launch {
            _viewState.value = update(viewState.value ?: BluetoothViewState())
        }
    }
}
