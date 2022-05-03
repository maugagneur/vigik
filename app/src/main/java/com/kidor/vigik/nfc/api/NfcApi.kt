package com.kidor.vigik.nfc.api

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.kidor.vigik.utils.SystemWrapper
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
    private val nfcAdapter: NfcAdapter,
    private val systemWrapper: SystemWrapper
) {

    private val listeners: MutableList<NfcApiListener> = mutableListOf()

    /**
     * Return true if this NFC Adapter has any features enabled.
     *
     * @see [NfcAdapter.isEnabled]
     */
    fun isNfcEnable() = nfcAdapter.isEnabled

    /**
     * Enable foreground dispatch to the given Activity.
     *
     * This will give priority to the foreground activity when dispatching a discovered tag.
     *
     * @param activity  The Activity to dispatch to.
     * @param javaClass The Java class of the component from which the intent will be created.
     * @see [NfcAdapter.enableForegroundDispatch]
     */
    fun <T : Any> enableNfcForegroundDispatch(activity: Activity, javaClass: Class<T>) {
        val intent = Intent(activity, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val nfcPendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        try {
            nfcAdapter.enableForegroundDispatch(activity, nfcPendingIntent, null, null)
        } catch (exception: IllegalStateException) {
            Timber.e(exception, "Error enabling NFC foreground dispatch")
        }
    }

    /**
     * Disable foreground dispatch to the given activity.
     *
     * @param activity The activity to disable dispatch to.
     * @see [NfcAdapter.disableForegroundDispatch]
     */
    fun disableNfcForegroundDispatch(activity: Activity) {
        try {
            nfcAdapter.disableForegroundDispatch(activity)
        } catch (exception: IllegalStateException) {
            Timber.e(exception, "Error disabling NFC foreground dispatch")
        }
    }

    /**
     * Call this method when receiving a NFC intent.
     *
     * @param intent The intent received.
     */
    fun onNfcIntentReceived(intent: Intent) {
        if (intent.action != NfcAdapter.ACTION_TAG_DISCOVERED) {
            Timber.d("Not a NFC intent received")
            return
        }

        val tag: Tag? = getTag(intent)
        val tagData = com.kidor.vigik.nfc.model.Tag(
            timestamp = systemWrapper.currentTimeMillis(),
            uid = tag?.id,
            techList = tag?.toString(),
            data = extractData(intent),
            id = extractLowLevelId(intent)
        )

        // Notify listeners
        listeners.forEach {
            it.onNfcTagRead(tagData)
        }
    }

    /**
     * Register to notification related to NFC.
     *
     * @param listener The listener to register.
     */
    fun register(listener: NfcApiListener) {
        listeners.let {
            // Prevent duplicated listeners
            if (!it.contains(listener)) {
                it.add(listener)
            }
        }
    }

    /**
     * Unregister to notifications related to NFC.
     *
     * @param listener The listener to unregister.
     */
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
