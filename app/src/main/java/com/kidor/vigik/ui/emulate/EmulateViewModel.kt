package com.kidor.vigik.ui.emulate

import android.nfc.cardemulation.HostApduService
import android.os.RemoteException
import androidx.annotation.RestrictTo
import com.kidor.vigik.extensions.startWith
import com.kidor.vigik.extensions.toHex
import com.kidor.vigik.nfc.hostapdu.ApduStatusBytes
import com.kidor.vigik.nfc.hostapdu.HostApduListener
import com.kidor.vigik.nfc.hostapdu.HostApduManager
import com.kidor.vigik.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

// ISO-DEP command HEADER for selecting an AID.
// Format: [Class | Instruction | Parameter 1 | Parameter 2]
private val SELECT_FILE_HEADER = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00)

// ID wrote on tag: 0xAC86574E
private val TAG_ID = byteArrayOf(0x4E.toByte(), 0x57, 0x86.toByte(), 0xAC.toByte())

/**
 * Business logic for emulating a NFC tag.
 */
@HiltViewModel
class EmulateViewModel @Inject constructor(
    private val hostApduManager: HostApduManager
) : BaseViewModel<Nothing, EmulateViewState, Nothing>(), HostApduListener {

    init {
        _viewState.value = EmulateViewState.DisplayLogLine("Start -> Register to host APDU manager")
        hostApduManager.register(this)
    }

    @RestrictTo(RestrictTo.Scope.TESTS)
    public override fun onCleared() {
        super.onCleared()
        _viewState.value = EmulateViewState.DisplayLogLine("Stop -> Unregister to host APDU manager")
        hostApduManager.unregister(this)
    }

    override fun onApduCommandReceived(apduCommand: ByteArray?) {
        _viewState.value = EmulateViewState.DisplayLogLine("APDU command received: " + apduCommand?.toHex() + ")")

        val apduResponse = when {
            apduCommand == null -> ApduStatusBytes.UNKNOWN_COMMAND.value
            apduCommand.startWith(SELECT_FILE_HEADER) -> getSelectFileResponse()
            else -> ApduStatusBytes.INSTRUCTION_NOT_SUPPORTED.value
        }

        _viewState.value = EmulateViewState.DisplayLogLine("Sending APDU response: " + apduResponse.toHex())
        sendCommandApdu(apduResponse)
    }

    override fun onConnectionLost(reason: Int) {
        when (reason) {
            HostApduService.DEACTIVATION_DESELECTED ->
                _viewState.value =
                    EmulateViewState.DisplayLogLine("Deactivation's reason: An other AID has been selected")
            HostApduService.DEACTIVATION_LINK_LOSS ->
                _viewState.value = EmulateViewState.DisplayLogLine("Deactivation's reason: NFC link lost")
            else -> _viewState.value = EmulateViewState.DisplayLogLine("Deactivation's reason: Unknown")
        }
    }

    /**
     * Builds and returns response to SELECT_FILE command.
     *
     * Format: [Data field | Status bytes]
     */
    private fun getSelectFileResponse(): ByteArray {
        // Data field = File Control Information or empty
        // Status bytes = OK (0x9000)
        return ApduStatusBytes.COMMAND_CORRECT.value
    }

    private fun sendCommandApdu(commandApdu: ByteArray) {
        try {
            hostApduManager.sendApduResponse(commandApdu)
        } catch (exception: RemoteException) {
            _viewState.value = EmulateViewState.DisplayLogLine("Error when sending message to service")
            Timber.e(exception, "Error when sending message to service")
        }
    }
}
