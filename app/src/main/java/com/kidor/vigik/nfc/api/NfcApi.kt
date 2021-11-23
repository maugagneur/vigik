package com.kidor.vigik.nfc.api

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * API used to interact with NFC sensor.
 *
 * You can use it to:
 * - Check if the device has NFC hardware and if it is ready to communicate
 * - Enable/Disable tag discovery
 * - Register/Unregister to be notify when a tag is read
 */
@Singleton
class NfcApi @Inject constructor(
    private val nfcAdapter: NfcAdapter
) {

    private val listeners: MutableList<NfcApiListener> = mutableListOf()

    fun isNfcEnable() = nfcAdapter.isEnabled

    fun <T : Any> enableNfcForegroundDispatch(activity: Activity, javaClass: Class<T>) {
        val intent = Intent(activity, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val nfcPendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        try {
            nfcAdapter.enableForegroundDispatch(activity, nfcPendingIntent, null, null)
        } catch (exception: IllegalStateException) {
            Timber.e(exception, "Error enabling NFC foreground dispatch")
        }
    }

    fun disableNfcForegroundDispatch(activity: Activity) {
        try {
            nfcAdapter.disableForegroundDispatch(activity)
        } catch (exception: IllegalStateException) {
            Timber.e(exception, "Error disabling NFC foreground dispatch")
        }
    }

    fun onNfcIntentReceived(intent: Intent) {
        if (intent.action != NfcAdapter.ACTION_TAG_DISCOVERED) {
            Timber.d("Not a NFC intent received")
            return
        }

        val tag: Tag? = getTag(intent)
        val tagData = TagData(tag?.id, tag?.toString(), extractData(intent), extractLowLevelId(intent))

        // Notify listeners
        listeners.forEach {
            it.onNfcTagRead(tagData)
        }
    }

    fun register(listener: NfcApiListener) {
        listeners.let {
            // Prevent duplicated listeners
            if (!it.contains(listener)) {
                it.add(listener)
            }
        }
    }

    fun unregister(listener: NfcApiListener) {
        listeners.remove(listener)
    }

    private fun getTag(intent: Intent): Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

    private fun extractData(intent: Intent): String {
        val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            ?: return "No NDEF messages"
        val messages = rawMessages.map { it as NdefMessage }
        val records = messages[0].records
        var recordData = ""
        records.forEachIndexed { index, ndefRecord ->
            recordData += ndefRecord.toString()
            if (index < records.size - 1) {
                recordData += "\n"
            }
        }
        return recordData
    }

    private fun extractLowLevelId(intent: Intent): ByteArray? =
        intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
}