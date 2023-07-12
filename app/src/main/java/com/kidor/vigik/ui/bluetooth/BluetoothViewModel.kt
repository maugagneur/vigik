package com.kidor.vigik.ui.bluetooth

import androidx.lifecycle.viewModelScope
import com.kidor.vigik.data.bluetooth.BluetoothApi
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Business logic of Bluetooth screen.
 */
@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothApi: BluetoothApi
) : BaseViewModel<BluetoothViewAction, BluetoothViewState, Nothing>() {

    init {
        viewModelScope.launch {
            bluetoothApi.bluetoothEnable.collect { bluetoothEnable ->
                updateViewState { it.copy(isBluetoothEnable = bluetoothEnable) }
            }
        }
        viewModelScope.launch {
            bluetoothApi.locationEnable.collect { locationEnable ->
                updateViewState { it.copy(isLocationEnable = locationEnable) }
            }
        }
    }

    /**
     * Update the current view state.
     *
     * @param update The operation to perform on view state.
     */
    private fun updateViewState(update: (BluetoothViewState) -> BluetoothViewState) {
        _viewState.value = update(viewState.value ?: BluetoothViewState())
    }
}
