package com.kidor.vigik.nfc

import android.nfc.cardemulation.HostApduService

/**
 * Listener used to receive data from [HostApduManager].
 */
interface HostApduListener {

    /**
     * Called when an APDU command is received from the NFC reader.
     */
    fun onApduCommandReceived(apduCommand: ByteArray?)

    /**
     * Called when the connection with NFC reader is lost.
     *
     * @param reason The reason of the connection lost.
     * @see HostApduService.DEACTIVATION_DESELECTED
     * @see HostApduService.DEACTIVATION_LINK_LOSS
     */
    fun onConnectionLost(reason: Int)
}