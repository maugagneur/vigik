package com.kidor.vigik.nfc.hostapdu

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * Implementation of [HostApduService].
 */
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
        /** The intent action for new message. **/
        const val APDU_SERVICE_ACTION_NEW_MESSAGE = "com.kidor.vigik.actions.NEW_MESSAGE"
        /** The key for message extra data. **/
        const val KEY_MESSAGE_TYPE = "message_type"
        /** The value of message when process APDU command. **/
        const val TYPE_APDU_COMMAND = "type_apdu_command"
        /** The value of message when deactivated. **/
        const val TYPE_DEACTIVATED = "type_deactivated"
        /** The key for APDU command extra data. **/
        const val KEY_APDU_COMMAND = "apdu_command"
        /** The key for deactivated reason extra data. **/
        const val KEY_DEACTIVATED_REASON = "deactivated_reason"

        /** The ID of the message. **/
        const val MSG_RESPONSE_APDU = 1
        /** The key of the extra data. **/
        const val KEY_DATA = "data"
    }
}
