package com.kidor.vigik.ui.bluetooth

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.bluetooth.BluetoothApi
import com.kidor.vigik.di.IoDispatcher
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Business logic of Bluetooth screen.
 */
@HiltViewModel
class BluetoothViewModel @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val bluetoothApi: BluetoothApi
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
    }

    override fun handleAction(viewAction: BluetoothViewAction) {
        when (viewAction) {
            BluetoothViewAction.StartBluetoothScan -> viewModelScope.launch(ioDispatcher) {
                updateViewState { it.copy(isScanning = true) }
                delay(5000)
                updateViewState { it.copy(isScanning = false) }
            }
        }
    }

    /**
     * Update the current view state.
     *
     * @param update The operation to perform on view state.
     */
    private fun updateViewState(update: (BluetoothViewState) -> BluetoothViewState) {
        _viewState.postValue(update(viewState.value ?: BluetoothViewState()))
    }
}
