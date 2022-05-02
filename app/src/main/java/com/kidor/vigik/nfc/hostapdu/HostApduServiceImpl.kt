package com.kidor.vigik.nfc.hostapdu

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class HostApduServiceImpl : HostApduService() {

    private lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onCreate() {
        super.onCreate()
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray? {
        val intent = Intent(APDU_SERVICE_ACTION_NEW_MESSAGE).apply {
            putExtra(KEY_MESSAGE_TYPE, TYPE_APDU_COMMAND)
            putExtra(KEY_APDU_COMMAND, commandApdu)
        }
        localBroadcastManager.sendBroadcast(intent)
        return null
    }

    override fun onDeactivated(reason: Int) {
        val intent = Intent(APDU_SERVICE_ACTION_NEW_MESSAGE).apply {
            putExtra(KEY_MESSAGE_TYPE, TYPE_DEACTIVATED)
            putExtra(KEY_DEACTIVATED_REASON, reason)
        }
        localBroadcastManager.sendBroadcast(intent)
    }

    companion object {
        const val APDU_SERVICE_ACTION_NEW_MESSAGE = "com.kidor.vigik.actions.NEW_MESSAGE"
        const val KEY_MESSAGE_TYPE = "message_type"
        const val TYPE_APDU_COMMAND = "type_apdu_command"
        const val TYPE_DEACTIVATED = "type_deactivated"
        const val KEY_APDU_COMMAND = "apdu_command"
        const val KEY_DEACTIVATED_REASON = "deactivated_reason"

        // Abstract super class constant overrides
        const val MSG_RESPONSE_APDU = 1
        const val KEY_DATA = "data"
    }
}
