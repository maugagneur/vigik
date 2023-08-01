package com.kidor.vigik.ui.bluetooth

import com.kidor.vigik.ui.base.ViewAction

/**
 * Available actions from Bluetooth view.
 */
sealed class BluetoothViewAction : ViewAction {

    /**
     * Action to start Bluetooth scan.
     */
    object StartBluetoothScan : BluetoothViewAction()

    /**
     * Action to toggle the low energy scan.
     *
     * @param isChecked True if the low energy scan is enabled, otherwise false.
     */
    data class ChangeLeScanState(val isChecked: Boolean) : BluetoothViewAction()
}
