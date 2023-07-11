package com.kidor.vigik.ui.bluetooth

import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Business logic of Bluetooth screen.
 */
@HiltViewModel
class BluetoothViewModel @Inject constructor() : BaseViewModel<BluetoothViewAction, BluetoothViewState, Nothing>() {

    init {
        _viewState.value = BluetoothViewState
    }
}
