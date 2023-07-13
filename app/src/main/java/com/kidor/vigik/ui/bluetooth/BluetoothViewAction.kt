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
}
